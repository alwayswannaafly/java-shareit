package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ItemRepository itemRepository,
            UserService userService,
            ItemService itemService
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto createBooking(BookingCreateDto bookingCreateDto, Long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new IdNotFoundException("Booker not found with id: " + bookerId));

        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + bookingCreateDto.getItemId()));

        if (!item.getAvailable()) {
            throw new InvalidInputException("Item is not available for booking");
        }

        if (bookingCreateDto.getStartDate().isAfter(bookingCreateDto.getEndDate()) ||
                bookingCreateDto.getStartDate().isEqual(bookingCreateDto.getEndDate())) {
            throw new InvalidInputException("End date must be after start date");
        }

        // Проверка на пересечение бронирований
        List<Booking> existingBookings = bookingRepository.findByItem_Id(item.getId());
        for (Booking existing : existingBookings) {
            if (existing.getStatus() == BookingStatus.APPROVED) {
                if (isDateRangeOverlapping(
                        bookingCreateDto.getStartDate(), bookingCreateDto.getEndDate(),
                        existing.getStartDate(), existing.getEndDate()
                )) {
                    throw new InvalidInputException("Item is already booked for this period");
                }
            }
        }

        Booking booking = new Booking();
        booking.setStartDate(bookingCreateDto.getStartDate());
        booking.setEndDate(bookingCreateDto.getEndDate());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        Booking savedBooking = bookingRepository.save(booking);
        return toBookingDto(savedBooking);
    }

    private boolean isDateRangeOverlapping(LocalDateTime start1, LocalDateTime end1,
                                           LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    @Override
    public BookingDto updateBookingStatus(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Booking not found with id: " + bookingId));

        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new InvalidInputException("Only owner can update booking status");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return toBookingDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IdNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new InvalidInputException("You don't have access to this booking");
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, String state) {
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStatusAndEndDateAfter(userId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndStatusAndEndDateBefore(userId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStatusAndStartDateAfter(userId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByBooker_Id(userId);
                break;
        }

        return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, String state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("User not found with id: " + ownerId));

        List<Item> items = itemRepository.findByOwnerId(ownerId);
        if (items.isEmpty()) {
            throw new InvalidInputException("User has no items, so cannot retrieve bookings as owner");
        }
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusAndEndDateAfter(ownerId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusAndEndDateBefore(ownerId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItem_Owner_IdAndStatusAndStartDateAfter(ownerId, BookingStatus.APPROVED, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItem_Owner_Id(ownerId);
                break;
        }

        return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    private BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setStatus(booking.getStatus());
        dto.setBooker(userService.getUserById(booking.getBooker().getId()));
        dto.setItem(itemService.getItemSimpleDto(booking.getItem().getId()));
        return dto;
    }
}

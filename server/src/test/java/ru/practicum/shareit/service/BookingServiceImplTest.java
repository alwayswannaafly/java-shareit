package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private Item item;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);

        User owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(10L);
        item.setAvailable(true);
        item.setOwner(owner);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(10L);
        bookingCreateDto.setStartDate(LocalDateTime.now().plusDays(1));
        bookingCreateDto.setEndDate(LocalDateTime.now().plusDays(2));
    }

    // =============== createBooking ===============

    @Test
    void createBooking_WhenBookerNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                bookingService.createBooking(bookingCreateDto, 1L));
    }

    @Test
    void createBooking_WhenItemNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                bookingService.createBooking(bookingCreateDto, 1L));
    }

    @Test
    void createBooking_WhenItemNotAvailable_ShouldThrowInvalidInputException() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(InvalidInputException.class, () ->
                bookingService.createBooking(bookingCreateDto, 1L));
    }

    @Test
    void createBooking_WhenEndDateBeforeStartDate_ShouldThrowInvalidInputException() {
        bookingCreateDto.setEndDate(bookingCreateDto.getStartDate().minusHours(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(InvalidInputException.class, () ->
                bookingService.createBooking(bookingCreateDto, 1L));
    }

    @Test
    void createBooking_WhenOverlappingBookingExists_ShouldThrowInvalidInputException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        Booking existing = new Booking();
        existing.setStatus(BookingStatus.APPROVED);
        existing.setStartDate(LocalDateTime.now().plusDays(1).minusHours(1));
        existing.setEndDate(LocalDateTime.now().plusDays(1).plusHours(1));
        when(bookingRepository.findByItem_Id(10L)).thenReturn(List.of(existing));

        assertThrows(InvalidInputException.class, () ->
                bookingService.createBooking(bookingCreateDto, 1L));
    }

    @Test
    void createBooking_WhenValid_ShouldReturnBookingDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_Id(10L)).thenReturn(List.of());

        Booking saved = new Booking();
        saved.setId(100L);
        saved.setBooker(booker);
        saved.setItem(item);
        saved.setStartDate(bookingCreateDto.getStartDate());
        saved.setEndDate(bookingCreateDto.getEndDate());
        saved.setStatus(BookingStatus.WAITING);
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        ItemSimpleDto itemDto = new ItemSimpleDto();
        itemDto.setId(10L);
        when(userService.getUserById(1L)).thenReturn(userDto);
        when(itemService.getItemSimpleDto(10L)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(any(Booking.class), any(UserDto.class), any(ItemSimpleDto.class)))
                .thenReturn(new BookingDto());

        BookingDto result = bookingService.createBooking(bookingCreateDto, 1L);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }

    // =============== updateBookingStatus ===============

    @Test
    void updateBookingStatus_WhenBookingNotFound_ShouldThrowIdNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                bookingService.updateBookingStatus(999L, true, 2L));
    }

    @Test
    void updateBookingStatus_WhenNotOwner_ShouldThrowInvalidInputException() {
        Booking booking = new Booking();
        booking.setItem(item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidInputException.class, () ->
                bookingService.updateBookingStatus(1L, true, 999L)); // 999 ≠ owner (2L)
    }

    @Test
    void updateBookingStatus_WhenApprovedTrue_ShouldSetApproved() {
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        UserDto userDto = new UserDto();
        ItemSimpleDto itemDto = new ItemSimpleDto();
        when(userService.getUserById(booker.getId())).thenReturn(userDto);
        when(itemService.getItemSimpleDto(item.getId())).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(any(), any(), any())).thenReturn(new BookingDto());

        bookingService.updateBookingStatus(1L, true, 2L);

        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateBookingStatus_WhenApprovedFalse_ShouldSetRejected() {
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        UserDto userDto = new UserDto();
        ItemSimpleDto itemDto = new ItemSimpleDto();
        when(userService.getUserById(booker.getId())).thenReturn(userDto);
        when(itemService.getItemSimpleDto(item.getId())).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(any(), any(), any())).thenReturn(new BookingDto());

        bookingService.updateBookingStatus(1L, false, 2L);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    // =============== getBookingById ===============

    @Test
    void getBookingById_WhenBookingNotFound_ShouldThrowIdNotFoundException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                bookingService.getBookingById(999L, 1L));
    }

    @Test
    void getBookingById_WhenUserIsNotBookerOrOwner_ShouldThrowInvalidInputException() {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(InvalidInputException.class, () ->
                bookingService.getBookingById(1L, 999L)); // 999 ≠ booker (1L) и ≠ owner (2L)
    }

    @Test
    void getBookingById_WhenUserIsBooker_ShouldReturnBooking() {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        UserDto userDto = new UserDto();
        ItemSimpleDto itemDto = new ItemSimpleDto();
        when(userService.getUserById(1L)).thenReturn(userDto);
        when(itemService.getItemSimpleDto(10L)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(any(), any(), any())).thenReturn(new BookingDto());

        BookingDto result = bookingService.getBookingById(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void getBookingById_WhenUserIsOwner_ShouldReturnBooking() {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        UserDto userDto = new UserDto();
        ItemSimpleDto itemDto = new ItemSimpleDto();
        when(userService.getUserById(1L)).thenReturn(userDto);
        when(itemService.getItemSimpleDto(10L)).thenReturn(itemDto);
        when(bookingMapper.toBookingDto(any(), any(), any())).thenReturn(new BookingDto());

        BookingDto result = bookingService.getBookingById(1L, 2L); // owner id = 2L
        assertNotNull(result);
    }

    // =============== getBookingsByOwner ===============

    @Test
    void getBookingsByOwner_WhenOwnerNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                bookingService.getBookingsByOwner(999L, "ALL"));
    }

    @Test
    void getBookingsByOwner_WhenOwnerHasNoItems_ShouldThrowInvalidInputException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findByOwnerId(2L)).thenReturn(List.of());

        assertThrows(InvalidInputException.class, () ->
                bookingService.getBookingsByOwner(2L, "ALL"));
    }

    @Test
    void getBookingsByOwner_WhenValid_ShouldReturnBookings() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findBookingsByOwnerWithState(eq(2L), anyString(), any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<BookingDto> result = bookingService.getBookingsByOwner(2L, "ALL");
        assertNotNull(result);
    }
}
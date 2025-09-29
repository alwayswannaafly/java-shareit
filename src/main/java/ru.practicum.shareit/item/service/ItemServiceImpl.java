package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserSimpleDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentService commentService;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentService commentService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentService = commentService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IdNotFoundException("User with id " + ownerId + " not found"));

        if (itemDto.getAvailable() == null) {
            throw new InvalidInputException("Available field is required");
        }

        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);

        Item savedItem = itemRepository.save(item);
        return toItemDto(savedItem, ownerId);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + itemId));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Only owner can update the item");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().trim().isEmpty()) {
                throw new InvalidInputException("Name cannot be empty");
            }
            existingItem.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().trim().isEmpty()) {
                throw new InvalidInputException("Description cannot be empty");
            }
            existingItem.setDescription(itemDto.getDescription().trim());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item savedItem = itemRepository.save(existingItem);
        return toItemDto(savedItem, ownerId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + itemId));
        return toItemDto(item, userId);
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text, Long userId) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String lowerText = text.toLowerCase();
        List<Item> items = itemRepository.findAvailableBySearchText(lowerText);
        return items.stream().map(item -> toItemDto(item, userId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> toItemDto(item, ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemSimpleDto getItemSimpleDto(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + itemId));
        return new ItemSimpleDto(item.getId(), item.getName(), item.getDescription());
    }

    private BookingSimpleDto getLastBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findLastBookings(itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        Booking last = bookings.get(0);
        return new BookingSimpleDto(last.getStartDate(), last.getEndDate());
    }

    private BookingSimpleDto getNextBooking(Long itemId) {
        List<Booking> bookings = bookingRepository.findNextBookings(itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        Booking next = bookings.get(0);
        return new BookingSimpleDto(next.getStartDate(), next.getEndDate());
    }

    private ItemDto toItemDto(Item item, Long userId) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwner(new UserSimpleDto(item.getOwner().getId(), item.getOwner().getName()));
        if (item.getOwner().getId().equals(userId)) {
            dto.setLastBooking(getLastBooking(item.getId()));
            dto.setNextBooking(getNextBooking(item.getId()));
        } else {
            dto.setLastBooking(null);
            dto.setNextBooking(null);
        }
        dto.setComments(commentService.getCommentsByItem(item.getId()));
        return dto;
    }
}

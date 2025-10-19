package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);
        item.setOwner(owner);

        itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Power drill");
        itemDto.setAvailable(true);
    }

    // =============== createItem ===============

    @Test
    void createItem_WhenOwnerNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                itemService.createItem(itemDto, 1L));
    }

    @Test
    void createItem_WhenAvailableIsNull_ShouldThrowInvalidInputException() {
        itemDto.setAvailable(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        assertThrows(InvalidInputException.class, () ->
                itemService.createItem(itemDto, 1L));
    }

    @Test
    void createItem_WhenRequestIdProvidedButNotFound_ShouldThrowIdNotFoundException() {
        itemDto.setRequestId(100L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                itemService.createItem(itemDto, 1L));
    }

    @Test
    void createItem_WhenValid_ShouldReturnItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentService.getCommentsByItem(10L)).thenReturn(List.of());

        ItemDto result = itemService.createItem(itemDto, 1L);

        assertNotNull(result);
        assertEquals("Drill", result.getName());
    }

    // =============== updateItem ===============

    @Test
    void updateItem_WhenItemNotFound_ShouldThrowIdNotFoundException() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                itemService.updateItem(itemDto, 1L, 10L));
    }

    @Test
    void updateItem_WhenNotOwner_ShouldThrowAccessDeniedException() {
        User anotherOwner = new User();
        anotherOwner.setId(2L);
        item.setOwner(anotherOwner);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () ->
                itemService.updateItem(itemDto, 1L, 10L));
    }

    @Test
    void updateItem_WhenNameIsEmpty_ShouldThrowInvalidInputException() {
        itemDto.setName("   ");
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(InvalidInputException.class, () ->
                itemService.updateItem(itemDto, 1L, 10L));
    }

    @Test
    void updateItem_WhenDescriptionIsEmpty_ShouldThrowInvalidInputException() {
        itemDto.setDescription("   ");
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(InvalidInputException.class, () ->
                itemService.updateItem(itemDto, 1L, 10L));
    }

    @Test
    void updateItem_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        ItemDto partialDto = new ItemDto();
        partialDto.setName("New Name");

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(commentService.getCommentsByItem(10L)).thenReturn(List.of());

        ItemDto result = itemService.updateItem(partialDto, 1L, 10L);

        assertEquals("New Name", result.getName());
        assertEquals("Power drill", result.getDescription());
    }

    // =============== getItemById ===============

    @Test
    void getItemById_WhenItemNotFound_ShouldThrowIdNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                itemService.getItemById(999L, 1L));
    }

    @Test
    void getItemById_WhenUserIsOwner_ShouldIncludeBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentService.getCommentsByItem(10L)).thenReturn(List.of());

        Booking lastBooking = new Booking();
        lastBooking.setStartDate(LocalDateTime.now().minusDays(2));
        lastBooking.setEndDate(LocalDateTime.now().minusDays(1));
        when(bookingRepository.findLastBookings(eq(10L), any(), any()))
                .thenReturn(List.of(lastBooking));

        Booking nextBooking = new Booking();
        nextBooking.setStartDate(LocalDateTime.now().plusDays(1));
        nextBooking.setEndDate(LocalDateTime.now().plusDays(2));
        when(bookingRepository.findNextBookings(eq(10L), any(), any()))
                .thenReturn(List.of(nextBooking));

        ItemDto result = itemService.getItemById(10L, 1L); // 1L == owner

        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void getItemById_WhenUserIsNotOwner_ShouldNotIncludeBookings() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(commentService.getCommentsByItem(10L)).thenReturn(List.of());

        ItemDto result = itemService.getItemById(10L, 2L); // 2L != owner

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    // =============== searchAvailableItems ===============

    @Test
    void searchAvailableItems_WhenTextIsNull_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchAvailableItems(null, 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_WhenTextIsEmpty_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchAvailableItems("   ", 1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchAvailableItems_WhenValidText_ShouldReturnItems() {
        Item foundItem = new Item();
        foundItem.setId(20L);
        foundItem.setName("Saw");
        foundItem.setDescription("Hand saw");
        foundItem.setAvailable(true);
        foundItem.setOwner(owner);

        when(itemRepository.findAvailableBySearchText("saw")).thenReturn(List.of(foundItem));
        when(commentService.getCommentsByItem(20L)).thenReturn(List.of());

        List<ItemDto> result = itemService.searchAvailableItems("Saw", 1L);

        assertFalse(result.isEmpty());
        assertEquals("Saw", result.get(0).getName());
    }

    // =============== getAllItemsByOwner ===============

    @Test
    void getAllItemsByOwner_ShouldReturnItems() {
        when(itemRepository.findByOwnerId(1L)).thenReturn(List.of(item));
        when(commentService.getCommentsByItem(10L)).thenReturn(List.of());

        List<ItemDto> result = itemService.getAllItemsByOwner(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // =============== getItemSimpleDto ===============

    @Test
    void getItemSimpleDto_WhenItemNotFound_ShouldThrowIdNotFoundException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                itemService.getItemSimpleDto(999L));
    }

    @Test
    void getItemSimpleDto_WhenValid_ShouldReturnItemSimpleDto() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        ItemSimpleDto result = itemService.getItemSimpleDto(10L);

        assertEquals(10L, result.getId());
        assertEquals("Drill", result.getName());
    }
}
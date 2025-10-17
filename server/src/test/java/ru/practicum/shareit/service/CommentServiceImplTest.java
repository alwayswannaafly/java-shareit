package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.CommentServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User author;
    private Item item;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setName("Author Name");

        item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setOwner(new User());
    }

    // =============== addComment ===============

    @Test
    void addComment_WhenAuthorNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                commentService.addComment(10L, 1L, "Great!"));
    }

    @Test
    void addComment_WhenItemNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                commentService.addComment(10L, 1L, "Great!"));
    }

    @Test
    void addComment_WhenNoCompletedBookings_ShouldThrowInvalidInputException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByBooker_IdAndItem_IdAndStatusAndEndDateBefore(
                eq(1L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(List.of());

        assertThrows(InvalidInputException.class, () ->
                commentService.addComment(10L, 1L, "Great!"));
    }

    @Test
    void addComment_WhenHasCompletedBooking_ShouldSaveAndReturnComment() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        Booking completedBooking = new Booking();
        completedBooking.setStatus(BookingStatus.APPROVED);
        completedBooking.setEndDate(LocalDateTime.now().minusHours(1));
        when(bookingRepository.findByBooker_IdAndItem_IdAndStatusAndEndDateBefore(
                eq(1L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(List.of(completedBooking));

        Comment savedComment = new Comment();
        savedComment.setId(100L);
        savedComment.setText("Great!");
        savedComment.setAuthor(author);
        savedComment.setItem(item);
        savedComment.setCreated(LocalDateTime.now());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = commentService.addComment(10L, 1L, "Great!");

        assertNotNull(result);
        assertEquals("Great!", result.getText());
        assertEquals("Author Name", result.getAuthorName());
        verify(commentRepository).save(any(Comment.class));
    }

    // =============== getCommentsByItem ===============

    @Test
    void getCommentsByItem_WhenNoComments_ShouldReturnEmptyList() {
        when(commentRepository.findByItem_Id(10L)).thenReturn(List.of());

        List<CommentDto> result = commentService.getCommentsByItem(10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentsByItem_WhenHasComments_ShouldReturnDtos() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice!");
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);

        when(commentRepository.findByItem_Id(10L)).thenReturn(List.of(comment));

        List<CommentDto> result = commentService.getCommentsByItem(10L);

        assertFalse(result.isEmpty());
        assertEquals("Nice!", result.get(0).getText());
        assertEquals("Author Name", result.get(0).getAuthorName());
    }
}
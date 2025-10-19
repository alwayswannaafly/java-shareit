package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository,
                              BookingRepository bookingRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public CommentDto addComment(Long itemId, Long authorId, String text) {
        log.info("Service received text: {}", text);
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IdNotFoundException("Author not found with id: " + authorId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + itemId));

        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndStatusAndEndDateBefore(
                authorId, itemId, BookingStatus.APPROVED, LocalDateTime.now()
        );
        if (bookings.isEmpty()) {
            throw new InvalidInputException("You can only leave a comment after using the item");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        log.info("Saving comment with text: {}", comment.getText());
        Comment savedComment = commentRepository.save(comment);
        return toCommentDto(savedComment);
    }

    @Override
    public List<CommentDto> getCommentsByItem(Long itemId) {
        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        return comments.stream().map(this::toCommentDto).collect(Collectors.toList());
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getAuthor().getName());
        return dto;
    }
}

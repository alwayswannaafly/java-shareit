package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long itemId, Long authorId, String text);

    List<CommentDto> getCommentsByItem(Long itemId);
}
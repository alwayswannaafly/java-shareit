package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemSimpleDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    List<ItemDto> getAllItemsByOwner(Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> searchAvailableItems(String text, Long userId);

    ItemSimpleDto getItemSimpleDto(Long itemId);
}

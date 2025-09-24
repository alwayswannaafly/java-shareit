package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getOwner());
    }

    public static Item toModel(ItemDto dto) {
        return new Item(dto.getId(), dto.getName(), dto.getDescription(),
                dto.getAvailable(), dto.getOwner());
    }
}

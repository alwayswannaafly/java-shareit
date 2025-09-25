package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Validated(OnCreate.class) @RequestBody ItemDto itemDto) {
        ItemDto createdItem = itemService.createItem(itemDto, ownerId);
        return ResponseEntity.ok(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        ItemDto updatedItem = itemService.updateItem(itemDto, ownerId, itemId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        List<ItemDto> items = itemService.getAllItemsByOwner(ownerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchAvailableItems(@RequestParam(name = "text") String text) {
        List<ItemDto> items = itemService.searchAvailableItems(text);
        return ResponseEntity.ok(items);
    }
}

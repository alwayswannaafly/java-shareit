package ru.practicum.shareit.item;

import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.OnCreate;

@RestController
@RequestMapping(path = "/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                             @Validated(OnCreate.class) @RequestBody ItemDto itemCreateDto) {
        return itemClient.createItem(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                             @PathVariable @Positive Long itemId,
                                             @RequestBody ItemDto itemUpdateDto) {
        return itemClient.updateItem(ownerId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @Positive Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId) {
        return itemClient.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable @Positive Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") @Positive Long authorId,
                                             @RequestBody String text) {
        return itemClient.addComment(itemId, authorId, text);
    }
}
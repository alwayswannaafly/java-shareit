package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "http://localhost:9090";

    public ItemClient() {
        super();
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemDto itemCreateDto) {
        return post("/items", ownerId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, Object itemUpdateDto) {
        return patch("/items/" + itemId, ownerId, itemUpdateDto);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(Long ownerId) {
        return get("/items", ownerId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        String path = "/items/search?text=" + text;
        return get(path, null);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, String text) {
        return post("/items/" + itemId + "/comment", authorId, text);
    }
}
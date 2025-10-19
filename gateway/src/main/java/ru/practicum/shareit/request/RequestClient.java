package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

@Service
public class RequestClient extends BaseClient {

    public RequestClient() {
        super();
    }

    public ResponseEntity<Object> createItemRequest(Long userId, String description) {
        return post("/requests", userId, description);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long requestId, Long userId) {
        return get("/requests/" + requestId, userId);
    }
}
package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @Autowired
    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @Valid @RequestBody ItemRequestCreateDto requestDto) {
        return requestClient.createItemRequest(userId, requestDto.getDescription());
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable @Positive Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return requestClient.getRequestById(requestId, userId);
    }
}
package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody String description) {
        ItemRequestDto requestDto = itemRequestService.createItemRequest(userId, description);
        return ResponseEntity.ok(requestDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDto> requests = itemRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequestDto requestDto = itemRequestService.getRequestById(requestId, userId);
        return ResponseEntity.ok(requestDto);
    }
}
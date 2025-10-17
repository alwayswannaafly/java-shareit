package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRepository itemRepository,
                                  UserRepository userRepository, ItemRequestMapper itemRequestMapper) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestMapper = itemRequestMapper;
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, String description) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("User not found with id: " + userId));

        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(requestor);
        ItemRequest savedRequest = itemRequestRepository.save(request);
        return itemRequestMapper.toItemRequestDto(savedRequest, List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_Id(userId);
        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findAllByRequestId(request.getId());
                    return itemRequestMapper.toItemRequestDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findOtherRequests(userId);
        return requests.stream().map(request -> {
            List<Item> items = itemRepository.findAllByRequestId(request.getId());
            return itemRequestMapper.toItemRequestDto(request, items);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new IdNotFoundException("Request not found with id: " + requestId));
        List<Item> items = itemRepository.findAllByRequestId(request.getId());
        return itemRequestMapper.toItemRequestDto(request, items);
    }
}

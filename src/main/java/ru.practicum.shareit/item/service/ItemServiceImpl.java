package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemRepository itemRepository;
    private final InMemoryUserRepository userRepository;

    @Autowired
    public ItemServiceImpl(InMemoryItemRepository itemRepository, InMemoryUserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) {
            throw new IdNotFoundException("Owner not found with id: " + ownerId);
        }
        itemDto.setOwner(ownerId);
        Item savedItem = itemRepository.save(ItemMapper.toModel(itemDto));
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        Optional<Item> existingItemOpt = itemRepository.findById(itemId);
        if (existingItemOpt.isEmpty()) {
            throw new IdNotFoundException("Item not found with id: " + itemId);
        }

        Item existingItem = existingItemOpt.get();
        if (!existingItem.getOwner().equals(ownerId)) {
            throw new AccessDeniedException("Only owner can update the item");
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().trim().isEmpty()) {
                throw new InvalidInputException("Name cannot be empty");
            }
            existingItem.setName(itemDto.getName().trim());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().trim().isEmpty()) {
                throw new InvalidInputException("Description cannot be empty");
            }
            existingItem.setDescription(itemDto.getDescription().trim());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        itemRepository.update(existingItem);
        return ItemMapper.toDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new IdNotFoundException("Item not found with id: " + itemId));
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long ownerId) {
        return itemRepository.findAllByOwner(ownerId).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        return itemRepository.findAvailableByText(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}

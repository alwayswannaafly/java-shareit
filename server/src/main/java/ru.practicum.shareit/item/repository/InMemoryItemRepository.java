package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final AtomicLong counter = new AtomicLong(0);

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(counter.incrementAndGet());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    public List<Item> findAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .toList();
    }

    public List<Item> findAvailableByText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .toList();
    }

    public void update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        }
    }

    public void deleteById(Long id) {
        items.remove(id);
    }
}

package ru.practicum.shareit.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();
        assertNull(item.getId());
    }

    @Test
    void testAllArgsConstructor() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item(1L, "name", "desc", true, owner, 100L);
        assertEquals(1L, item.getId());
        assertEquals("name", item.getName());
        assertEquals("desc", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(100L, item.getRequestId());
    }

    @Test
    void testEqualsAndHashCode() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        Item item3 = new Item();
        item3.setId(2L);

        // this == o
        assertEquals(item1, item1);

        // null
        assertNotEquals(null, item1);

        // different class
        assertNotEquals("string", item1);

        // same id
        assertEquals(item1, item2);

        // different id
        assertNotEquals(item1, item3);

        // id = null
        Item item4 = new Item();
        assertNotEquals(item4, item1);

        // hashCode
        assertEquals(31, item1.hashCode());
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testToString() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Drill");
        item.setDescription("Power drill");
        item.setAvailable(true);

        String str = item.toString();
        assertNotNull(str);
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("name='Drill'"));
        assertTrue(str.contains("available=true"));
    }
}
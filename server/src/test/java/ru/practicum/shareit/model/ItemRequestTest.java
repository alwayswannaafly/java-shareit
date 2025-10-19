package ru.practicum.shareit.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();
        assertNull(request.getId());
    }

    @Test
    void testAllArgsConstructor() {
        User requestor = new User();
        requestor.setId(1L);

        LocalDateTime now = LocalDateTime.now();
        ItemRequest request = new ItemRequest(10L, "Need a drill", requestor, now);

        assertEquals(10L, request.getId());
        assertEquals("Need a drill", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(now, request.getCreated());
    }

    @Test
    void testEqualsAndHashCode() {
        ItemRequest r1 = new ItemRequest();
        r1.setId(1L);

        ItemRequest r2 = new ItemRequest();
        r2.setId(1L);

        ItemRequest r3 = new ItemRequest();
        r3.setId(2L);

        // this == o
        assertEquals(r1, r1);

        // null
        assertNotEquals(null, r1);

        // different class
        assertNotEquals("string", r1);

        // same id
        assertEquals(r1, r2);

        // different id
        assertNotEquals(r1, r3);

        // id = null
        ItemRequest r4 = new ItemRequest();
        assertNotEquals(r4, r1);

        // hashCode
        assertEquals(31, r1.hashCode());
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        ItemRequest request = new ItemRequest();
        request.setId(5L);
        request.setDescription("Urgent!");
        request.setCreated(LocalDateTime.of(2025, 10, 17, 14, 30));

        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("description='Urgent!'"));
        assertTrue(str.contains("created=2025-10-17T14:30"));
    }
}
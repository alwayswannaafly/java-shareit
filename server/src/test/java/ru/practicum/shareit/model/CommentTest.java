package ru.practicum.shareit.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();
        assertNull(comment.getId());
    }

    @Test
    void testAllArgsConstructor() {
        Item item = new Item();
        item.setId(1L);

        User author = new User();
        author.setId(2L);

        LocalDateTime now = LocalDateTime.now();

        Comment comment = new Comment(10L, "Great!", now, item, author);
        assertEquals(10L, comment.getId());
        assertEquals("Great!", comment.getText());
        assertEquals(now, comment.getCreated());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
    }

    @Test
    void testEqualsAndHashCode() {
        Comment c1 = new Comment();
        c1.setId(1L);

        Comment c2 = new Comment();
        c2.setId(1L);

        Comment c3 = new Comment();
        c3.setId(2L);

        assertEquals(c1, c1);
        assertNotEquals(null, c1);
        assertNotEquals("not a comment", c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);

        Comment c4 = new Comment(); // id = null
        assertNotEquals(c4, c1);

        assertEquals(31, c1.hashCode());
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString() {
        Comment comment = new Comment();
        comment.setId(5L);
        comment.setText("Awesome!");
        comment.setCreated(LocalDateTime.of(2025, 10, 17, 12, 0));

        String str = comment.toString();
        assertNotNull(str);
        assertTrue(str.contains("id=5"));
        assertTrue(str.contains("text='Awesome!'"));
        assertTrue(str.contains("created=2025-10-17T12:00"));
    }
}
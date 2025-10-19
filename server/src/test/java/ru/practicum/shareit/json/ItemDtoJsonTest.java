package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemDto_ShouldReturnCorrectJson() throws Exception {
        // Given
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("A test item");
        itemDto.setAvailable(true);

        // When
        String json = objectMapper.writeValueAsString(itemDto);

        // Then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test Item\"");
        assertThat(json).contains("\"description\":\"A test item\"");
        assertThat(json).contains("\"available\":true");
    }

    @Test
    void deserializeItemDto_ShouldReturnCorrectObject() throws Exception {
        // Given
        String json = "{ \"id\": 1, \"name\": \"Test Item\", \"description\": \"A test item\", \"available\": true }";

        // When
        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        // Then
        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Test Item");
        assertThat(itemDto.getDescription()).isEqualTo("A test item");
        assertThat(itemDto.getAvailable()).isTrue();
    }
}
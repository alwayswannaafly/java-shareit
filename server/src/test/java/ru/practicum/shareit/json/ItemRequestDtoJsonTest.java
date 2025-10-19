package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeItemRequestDto_ShouldReturnCorrectJson() throws Exception {
        // Given
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.of(2025, 10, 15, 10, 0));
        requestDto.setItems(List.of());

        // When
        String json = objectMapper.writeValueAsString(requestDto);

        // Then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Need a drill\"");
        assertThat(json).contains("\"created\"");
        assertThat(json).contains("\"items\":[]");
    }

    @Test
    void deserializeItemRequestDto_ShouldReturnCorrectObject() throws Exception {
        // Given
        String json = " {\"id\": 1, \"description\": \"Need a drill\", " +
                "\"created\": \"2025-10-15T10:00:00\", \"items\": []} ";

        // When
        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        // Then
        assertThat(requestDto.getId()).isEqualTo(1L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getCreated())
                .isEqualTo(LocalDateTime.of(2025, 10, 15, 10, 0));
        assertThat(requestDto.getItems()).isEmpty();
    }
}
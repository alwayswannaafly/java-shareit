package ru.practicum.shareit.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serializeUserDto_ShouldReturnCorrectJson() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        // When
        String json = objectMapper.writeValueAsString(userDto);

        // Then
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Test User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
    }

    @Test
    void deserializeUserDto_ShouldReturnCorrectObject() throws Exception {
        // Given
        String json = " {\"id\": 1, \"name\": \"Test User\", \"email\": \"test@example.com\"} ";

        // When
        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        // Then
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("Test User");
        assertThat(userDto.getEmail()).isEqualTo("test@example.com");
    }
}
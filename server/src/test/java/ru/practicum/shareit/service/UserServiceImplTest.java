package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
    }

    // =============== createUser ===============

    @Test
    void createUser_WhenEmailAlreadyExists_ShouldThrowAlreadyExistsException() {
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () ->
                userService.createUser(userDto));
    }

    @Test
    void createUser_WhenValid_ShouldReturnUserDto() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Test User", "test@example.com"));

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    // =============== updateUser ===============

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                userService.updateUser(999L, userDto));
    }

    @Test
    void updateUser_WhenNameIsEmpty_ShouldThrowInvalidInputException() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto updateDto = new UserDto();
        updateDto.setName("   ");

        assertThrows(InvalidInputException.class, () ->
                userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_WhenEmailIsEmpty_ShouldThrowInvalidInputException() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto updateDto = new UserDto();
        updateDto.setEmail("   ");

        assertThrows(InvalidInputException.class, () ->
                userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_WhenEmailIsInvalid_ShouldThrowInvalidInputException() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto updateDto = new UserDto();
        updateDto.setEmail("invalid-email");

        assertThrows(InvalidInputException.class, () ->
                userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_WhenEmailAlreadyExistsForAnotherUser_ShouldThrowAlreadyExistsException() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        User another = new User(2L, "Other", "new@example.com");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(another));

        UserDto updateDto = new UserDto();
        updateDto.setEmail("new@example.com");

        assertThrows(AlreadyExistsException.class, () ->
                userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_WhenEmailAlreadyExistsButSameUser_ShouldAllowUpdate() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);

        UserDto updateDto = new UserDto();
        updateDto.setEmail("old@example.com"); // тот же email

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("old@example.com", result.getEmail());
    }

    @Test
    void updateUser_WhenPartialUpdate_ShouldUpdateOnlyProvidedFields() {
        User existing = new User(1L, "Old Name", "old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("New Name", result.getName());
        assertEquals("old@example.com", result.getEmail());
    }

    // =============== getUserById ===============

    @Test
    void getUserById_WhenUserNotFound_ShouldThrowIdNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IdNotFoundException.class, () ->
                userService.getUserById(999L));
    }

    // =============== getAllUsers ===============

    @Test
    void getAllUsers_ShouldReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "User1", "u1@example.com"),
                new User(2L, "User2", "u2@example.com")
        ));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }

    // =============== deleteUser ===============

    @Test
    void deleteUser_ShouldCallRepository() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }
}
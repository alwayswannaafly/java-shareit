package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.exception.InvalidInputException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmailUniqueness(userDto.getEmail(), null);
        User user = UserMapper.toModel(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));

        if (userDto.getName() != null) {
            if (userDto.getName().trim().isEmpty()) {
                throw new InvalidInputException("Name cannot be empty");
            }
            existingUser.setName(userDto.getName().trim());
        }

        if (userDto.getEmail() != null) {
            if (userDto.getEmail().trim().isEmpty()) {
                throw new InvalidInputException("Email cannot be empty");
            }

            if (!isValidEmail(userDto.getEmail())) {
                throw new InvalidInputException("Email is not valid");
            }

            validateEmailUniqueness(userDto.getEmail(), id);
            existingUser.setEmail(userDto.getEmail().trim());
        }

        User savedUser = userRepository.save(existingUser);
        return UserMapper.toDto(savedUser);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new IdNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void validateEmailUniqueness(String email, Long excludeId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            if (excludeId == null || !existingUser.get().getId().equals(excludeId)) {
                throw new AlreadyExistsException("Email already exists: " + email);
            }
        }
    }
}
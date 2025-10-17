package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;

@Service
public class UserClient extends BaseClient {

    public UserClient() {
        super();
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("/users", null, userDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        return patch("/users/" + userId, null, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/users/" + userId, null);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users", null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/users/" + userId, null);
    }
}
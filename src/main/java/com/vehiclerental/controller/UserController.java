package com.vehiclerental.controller;

import com.vehiclerental.model.User;
import com.vehiclerental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register a new user (public)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    // Login (public)
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(user);
    }

    // Update user profile (authenticated)
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateProfile(@PathVariable Long userId,
                                              @RequestBody User updatedUser,
                                              @RequestHeader("X-User-Id") Long authUserId) {
        if (!userId.equals(authUserId)) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        User user = userService.updateProfile(userId, updatedUser);
        return ResponseEntity.ok(user);
    }
}

// Simple DTO for login request
class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
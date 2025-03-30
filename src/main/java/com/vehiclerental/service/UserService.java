package com.vehiclerental.service;

import com.vehiclerental.exception.ResourceNotFoundException;
import com.vehiclerental.model.User;
import com.vehiclerental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Find user by username (for login)
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    // Register a new user
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    // Update user profile (e.g., email)
    public User updateProfile(Long userId, User updatedUser) {
        User existingUser = findById(userId);
        existingUser.setEmail(updatedUser.getEmail());
        return userRepository.save(existingUser);
    }

    // Simple login method
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }

    // New method to handle Optional<User>
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}

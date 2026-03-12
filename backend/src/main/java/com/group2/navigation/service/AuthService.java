package com.group2.navigation.service;

import com.group2.navigation.model.User;
import com.group2.navigation.model.UserPreferences;
import com.group2.navigation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles user signup and login.
 * Passwords are hashed with BCrypt before storing in H2.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Register a new user.
     *
     * @throws IllegalArgumentException if the username is already taken
     */
    public User signup(String username, String password, String displayName) {
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User(username, encoder.encode(password), displayName);
        return userRepo.save(user);
    }

    /**
     * Authenticate an existing user.
     *
     * @return the User if credentials are valid
     * @throws IllegalArgumentException if username not found or password is wrong
     */
    public User login(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return user;
    }

    /** Update a user's saved route preferences. */
    public User updatePreferences(Long userId, UserPreferences prefs) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.applyPreferences(prefs);
        return userRepo.save(user);
    }

    /** Get a user by ID. */
    public Optional<User> getUser(Long userId) {
        return userRepo.findById(userId);
    }
}

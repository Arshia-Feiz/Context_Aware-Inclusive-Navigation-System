package com.group2.navigation.controller;

import com.group2.navigation.model.User;
import com.group2.navigation.model.UserPreferences;
import com.group2.navigation.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST endpoints for user signup, login, and preference management.
 *
 * Stateless — the frontend stores the userId after login and sends it
 * with preference-related requests. No session or JWT needed for the prototype.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user.
     *
     * POST /api/auth/signup
     * Body: { "username": "arshia", "password": "pass123", "displayName": "Arshia" }
     */
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String displayName = body.get("displayName");

            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Username and password are required"));
            }

            User user = authService.signup(username.trim(), password, displayName);
            return ResponseEntity.ok(userResponse(user, "Account created"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Log in with existing credentials.
     *
     * POST /api/auth/login
     * Body: { "username": "arshia", "password": "pass123" }
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");

            User user = authService.login(username, password);
            return ResponseEntity.ok(userResponse(user, "Login successful"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /**
     * Get a user's saved preferences.
     *
     * GET /api/auth/preferences/{userId}
     */
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<Object> getPreferences(@PathVariable Long userId) {
        return authService.getUser(userId)
                .map(user -> ResponseEntity.ok((Object) Map.of(
                        "success", true,
                        "userId", user.getId(),
                        "preferences", user.toPreferences())))
                .orElse(ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User not found")));
    }

    /**
     * Update a user's saved preferences.
     *
     * PUT /api/auth/preferences/{userId}
     * Body: { "wheelchairWeight": 10, "crimeWeight": 5, ... }
     */
    @PutMapping("/preferences/{userId}")
    public ResponseEntity<Object> updatePreferences(
            @PathVariable Long userId,
            @RequestBody UserPreferences prefs) {
        try {
            User user = authService.updatePreferences(userId, prefs);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Preferences updated",
                    "preferences", user.toPreferences()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        }
    }

    /** Build a consistent user response (never expose the password hash). */
    private Map<String, Object> userResponse(User user, String message) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        resp.put("message", message);
        resp.put("userId", user.getId());
        resp.put("username", user.getUsername());
        resp.put("displayName", user.getDisplayName());
        resp.put("preferences", user.toPreferences());
        return resp;
    }
}

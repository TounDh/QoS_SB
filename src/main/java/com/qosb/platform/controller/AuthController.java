package com.qosb.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Hardcoded users — move to application.properties later
    private final Map<String, String> users = Map.of(
            "admin", "qosb2025",
            "operator", "netops123"
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (users.containsKey(username) && users.get(username).equals(password)) {
            // For now: return username as token (swap with real JWT later)
            String token = Base64.getEncoder().encodeToString(
                    (username + ":" + System.currentTimeMillis()).getBytes()
            );
            return ResponseEntity.ok(Map.of("token", token, "user", username));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
}
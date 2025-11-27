package com.happytraining.happytraining.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Verify token (you already have this)
    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyToken(@RequestHeader("Authorization") String idToken) {
        try {
            if (idToken != null && idToken.startsWith("Bearer ")) {
                idToken = idToken.substring(7);
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            // Get user details
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);

            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Token verified successfully",
                    uid,
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    idToken
            ));

        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Error verifying token: " + e.getMessage())
            );
        }
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.email)
                    .setPassword(request.password)
                    .setDisplayName(request.name);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());
            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Registration successful",
                    userRecord.getUid(),
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    customToken // Token will be generated on client side
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Registration failed: " + e.getMessage())
            );
        }
    }

    // User login (Firebase handles authentication, this just verifies)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Firebase web SDK handles login, this endpoint just verifies the user exists
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(request.email);

            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Login successful",
                    userRecord.getUid(),
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    customToken // Token comes from client
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Login failed: Invalid credentials")
            );
        }
    }
    @PostMapping("/test-token")
    public ResponseEntity<AuthResponse> generateTestIdToken(@RequestParam String email) {
        try {
            // Get user by email
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

            // Create a custom token
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());

            // For testing, we'll simulate what the frontend would do
            // This is a simplified version - in production, frontend handles this
            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Test token generated for: " + email,
                    userRecord.getUid(),
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    customToken
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Error generating test token: " + e.getMessage())
            );
        }
    }

    // Get user profile
    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(@RequestHeader("Authorization") String idToken) {
        try {
            if (idToken != null && idToken.startsWith("Bearer ")) {
                idToken = idToken.substring(7);
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);

            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Profile retrieved successfully",
                    uid,
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    idToken
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Failed to get profile: " + e.getMessage())
            );
        }
    }

    // Update user profile
    @PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(
            @RequestHeader("Authorization") String idToken,
            @RequestBody UpdateProfileRequest request) {
        try {
            if (idToken != null && idToken.startsWith("Bearer ")) {
                idToken = idToken.substring(7);
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(uid);
            if (request.name != null) {
                updateRequest.setDisplayName(request.name);
            }
            if (request.email != null) {
                updateRequest.setEmail(request.email);
            }

            UserRecord userRecord = FirebaseAuth.getInstance().updateUser(updateRequest);

            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Profile updated successfully",
                    uid,
                    userRecord.getDisplayName(),
                    userRecord.getEmail(),
                    idToken
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Failed to update profile: " + e.getMessage())
            );
        }
    }

    // Delete user account
    @DeleteMapping("/account")
    public ResponseEntity<AuthResponse> deleteAccount(@RequestHeader("Authorization") String idToken) {
        try {
            if (idToken != null && idToken.startsWith("Bearer ")) {
                idToken = idToken.substring(7);
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            FirebaseAuth.getInstance().deleteUser(uid);

            return ResponseEntity.ok(new AuthResponse(
                    true,
                    "Account deleted successfully",
                    uid,
                    null,
                    null,
                    null
            ));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.badRequest().body(
                    new AuthResponse(false, "Failed to delete account: " + e.getMessage())
            );
        }
    }
    @GetMapping("/protected-test")
    public ResponseEntity<String> protectedTest(@AuthenticationPrincipal FirebaseToken token) {
        if (token != null) {
            return ResponseEntity.ok("Hello " + token.getEmail() + "! You accessed a protected endpoint!");
        } else {
            return ResponseEntity.status(401).body("Unauthorized - no valid token");
        }
    }
    // Request and Response classes
    public static class LoginRequest {
        public String email;
        public String password;
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    public static class UpdateProfileRequest {
        public String name;
        public String email;
    }

    public static class AuthResponse {
        private boolean success;
        private String message;
        private String userId;
        private String userName;
        private String userEmail;
        private String token;

        public AuthResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AuthResponse(boolean success, String message, String userId,
                            String userName, String userEmail, String token) {
            this.success = success;
            this.message = message;
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.token = token;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        @GetMapping("/protected-test")
        public ResponseEntity<String> protectedTest(@AuthenticationPrincipal FirebaseToken token) {
            return ResponseEntity.ok("Hello " + token.getEmail() + "! You accessed a protected endpoint!");
        }
    }
}
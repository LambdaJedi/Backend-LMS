package com.happytraining.happytraining.controller;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/firebase-check")
    public ResponseEntity<Map<String, Object>> checkFirebase() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if Firebase is initialized
            boolean isInitialized = !FirebaseApp.getApps().isEmpty();
            response.put("firebaseInitialized", isInitialized);

            if (isInitialized) {
                FirebaseApp app = FirebaseApp.getInstance();
                response.put("firebaseAppName", app.getName());
                response.put("projectId", app.getOptions().getProjectId());

                // Test a simple Firebase Auth operation
                try {
                    // This will fail if credentials are invalid
                    FirebaseAuth.getInstance().listUsers(null, 1);
                    response.put("firebaseAuth", "WORKING");
                    response.put("status", "✅ Firebase is properly configured!");
                } catch (Exception e) {
                    response.put("firebaseAuth", "FAILED: " + e.getMessage());
                    response.put("status", "❌ Firebase Auth not working");
                }
            } else {
                response.put("status", "❌ Firebase not initialized");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/firebase-project")
    public ResponseEntity<Map<String, Object>> checkProject() {
        Map<String, Object> response = new HashMap<>();

        try {
            String fileName = "happy-training-and-consultancy-firebase-adminsdk-fbsvc-c8e7815253.json";

            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream(fileName);

            if (serviceAccount == null) {
                response.put("fileStatus", "NOT_FOUND");
                response.put("message", "Service account file not found: " + fileName);
                return ResponseEntity.ok(response);
            }

            // Read the service account file
            String content = new String(serviceAccount.readAllBytes());
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();

            response.put("fileStatus", "FOUND");
            response.put("projectId", json.get("project_id").getAsString());
            response.put("clientEmail", json.get("client_email").getAsString());
            response.put("tokenUri", json.get("token_uri").getAsString());
            response.put("privateKeyId", json.get("private_key_id").getAsString().substring(0, 10) + "...");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/security")
    public ResponseEntity<Map<String, Object>> checkSecurity() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Security debug endpoint is accessible");
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "Server is running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/network-test")
    public ResponseEntity<Map<String, Object>> networkTest() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Test if we can reach Google's OAuth server
            URL url = new URL("https://oauth2.googleapis.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            response.put("googleOAuthReachable", responseCode == 200 || responseCode == 301 || responseCode == 302);
            response.put("googleResponseCode", responseCode);

            // Test Firebase Auth endpoint
            try {
                URL firebaseUrl = new URL("https://identitytoolkit.googleapis.com");
                HttpURLConnection firebaseConnection = (HttpURLConnection) firebaseUrl.openConnection();
                firebaseConnection.setRequestMethod("HEAD");
                firebaseConnection.setConnectTimeout(5000);
                firebaseConnection.setReadTimeout(5000);

                int firebaseResponseCode = firebaseConnection.getResponseCode();
                response.put("firebaseAuthReachable", firebaseResponseCode == 200 || firebaseResponseCode == 301 || firebaseResponseCode == 302);
                response.put("firebaseResponseCode", firebaseResponseCode);
            } catch (Exception e) {
                response.put("firebaseAuthReachable", false);
                response.put("firebaseError", e.getMessage());
            }

            // Test general internet connectivity
            try {
                URL googleUrl = new URL("https://www.google.com");
                HttpURLConnection googleConnection = (HttpURLConnection) googleUrl.openConnection();
                googleConnection.setRequestMethod("HEAD");
                googleConnection.setConnectTimeout(5000);
                googleConnection.setReadTimeout(5000);

                int googleResponseCode = googleConnection.getResponseCode();
                response.put("internetReachable", googleResponseCode == 200 || googleResponseCode == 301 || googleResponseCode == 302);
                response.put("internetResponseCode", googleResponseCode);
            } catch (Exception e) {
                response.put("internetReachable", false);
                response.put("internetError", e.getMessage());
            }

            response.put("status", "Network test completed");

        } catch (Exception e) {
            response.put("error", e.getMessage());
            response.put("status", "Network test failed");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/firebase-detailed")
    public ResponseEntity<Map<String, Object>> detailedFirebaseCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check Firebase initialization
            boolean isInitialized = !FirebaseApp.getApps().isEmpty();
            response.put("firebaseInitialized", isInitialized);

            if (isInitialized) {
                FirebaseApp app = FirebaseApp.getInstance();
                response.put("appName", app.getName());
                response.put("projectId", app.getOptions().getProjectId());
                response.put("databaseUrl", app.getOptions().getDatabaseUrl());
                response.put("storageBucket", app.getOptions().getStorageBucket());

                // Test credentials
                try {
                    InputStream serviceAccount = getClass().getClassLoader()
                            .getResourceAsStream("happy-training-and-consultancy-firebase-adminsdk-fbsvc-c8e7815253.json");

                    if (serviceAccount != null) {
                        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                        response.put("credentialsCreated", true);

                        // Try to refresh credentials
                        try {
                            credentials.refresh();
                            response.put("credentialsRefresh", "SUCCESS");
                        } catch (Exception e) {
                            response.put("credentialsRefresh", "FAILED: " + e.getMessage());
                        }
                    } else {
                        response.put("credentialsCreated", false);
                    }
                } catch (Exception e) {
                    response.put("credentialsError", e.getMessage());
                }

                // Test Firebase Auth
                try {
                    FirebaseAuth.getInstance().listUsers(null, 1);
                    response.put("firebaseAuth", "WORKING");
                } catch (Exception e) {
                    response.put("firebaseAuth", "FAILED: " + e.getMessage());
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

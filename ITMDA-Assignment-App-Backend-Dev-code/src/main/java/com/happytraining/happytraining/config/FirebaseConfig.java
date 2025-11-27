package com.happytraining.happytraining.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    // Put this JSON in src/main/resources/
    private static final String SERVICE_ACCOUNT_FILE = "happy-training-and-consultancy-firebase-adminsdk-fbsvc-025268e8bb.json";

    @Bean
    public FirebaseApp firebaseApp() throws Exception {
        // if already initialized (devtools hot reload), return existing
        if (!FirebaseApp.getApps().isEmpty()) {
            logger.info("FirebaseApp already initialized - returning existing instance.");
            return FirebaseApp.getInstance();
        }

        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(SERVICE_ACCOUNT_FILE);
        if (serviceAccount == null) {
            String msg = "Firebase service account file not found on classpath: " + SERVICE_ACCOUNT_FILE;
            logger.error(msg);
            throw new IllegalStateException(msg); // fail fast so services depending on Firestore won't get a null bean
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
        logger.info("FirebaseApp initialized successfully (project: {}).", app.getOptions().getProjectId());
        return app;
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        // This is the bean your services expect to be injected
        Firestore db = FirestoreClient.getFirestore(firebaseApp);
        logger.info("Firestore bean created.");
        return db;
    }
}

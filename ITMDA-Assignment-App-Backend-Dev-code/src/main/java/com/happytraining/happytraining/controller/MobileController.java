package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Course;
import com.happytraining.happytraining.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/mobile")
public class MobileController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return ResponseEntity.ok(courses);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Downloading course material
    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        // 1. Logic to find the file (You will need to implement this properly)
        // For now, let's assume files are stored in a directory called "uploads"
        java.io.File file = new java.io.File("uploads/" + fileName);

        // 2. Check if file actually exists
        if (!file.exists()) {
            // Return a 404 Not Found error if the file doesn't exist
            return ResponseEntity.notFound().build();
        }

        try {
            // 3. Read the file's content into a byte array
            byte[] fileContent = java.nio.file.Files.readAllBytes(file.toPath());

            // 4. Create a Resource object from the byte array
            ByteArrayResource resource = new ByteArrayResource(fileContent);

            // 5. Build the response with the file data and correct headers
            return ResponseEntity.ok()
                    // This header tells the browser to download the file instead of displaying it
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    // Set the correct content type (you might want to detect this dynamically)
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(resource);

        } catch (Exception e) {
            // Return a 500 error if something goes wrong reading the file
            return ResponseEntity.internalServerError().build();
        }
    }

    // You might want to add this for mobile app to get a single course
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        try {
            Course course = courseService.getCourseById(id);
            if (course != null) {
                return ResponseEntity.ok(course);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

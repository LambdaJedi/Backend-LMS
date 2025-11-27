package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.dto.EnrollmentResponse;
import com.happytraining.happytraining.model.Course;
import com.happytraining.happytraining.service.CourseService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() throws ExecutionException, InterruptedException {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Course course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) throws ExecutionException, InterruptedException {
        Course created = courseService.createCourse(course);
        return ResponseEntity.status(201).body(created);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) throws ExecutionException, InterruptedException {
        try {
            boolean deleted = courseService.deleteCourse(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Course not found: " + id));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting course: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody Course updates) throws ExecutionException, InterruptedException {
        Course updated = courseService.updateCourse(id, updates);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enroll(@PathVariable("id") String courseId,
                                    @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "userId is required"));
        }

        try {
            Course updated = courseService.enrollUser(courseId, userId);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Course not found: " + courseId));
            }

            EnrollmentResponse resp = new EnrollmentResponse(
                    "Enrollment successful",
                    updated.getId(),
                    updated.getEnrolledUsers()
            );

            return ResponseEntity.ok(resp);

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        } catch (ExecutionException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error enrolling user", "error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error", "error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<List<Course>> myCourses() throws ExecutionException, InterruptedException {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(courseService.getCoursesForUser(uid));
    }
}


package com.happytraining.happytraining.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.Course;
import com.happytraining.happytraining.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CollectionReference courses;

    public CourseServiceImpl(Firestore firestore) {
        this.courses = firestore.collection("courses");
    }

    @Override
    public List<Course> getAllCourses() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = courses.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(doc -> {
                    Course course = doc.toObject(Course.class);
                    course.setId(doc.getId());  // Manually set ID for each course
                    return course;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Course getCourseById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = courses.document(id).get().get();
        if (snap.exists()) {
            Course course = snap.toObject(Course.class);
            if (course != null) {
                course.setId(id);  // Manually set the ID
            }
            return course;
        }
        return null;
    }

    @Override
    public Course createCourse(Course course) throws ExecutionException, InterruptedException {
        DocumentReference ref = (course.getId() == null || course.getId().isEmpty()) ? courses.document() : courses.document(course.getId());
        ref.set(course).get();
        course.setId(ref.getId());
        return course;
    }

    @Override
    public Course updateCourse(String id, Course updates) throws ExecutionException, InterruptedException {
        DocumentReference ref = courses.document(id);
        Map<String, Object> updateMap = new HashMap<>();

        if (updates.getTitle() != null) updateMap.put("title", updates.getTitle());
        if (updates.getDescription() != null) updateMap.put("description", updates.getDescription());
        if (updates.getInstructor() != null) updateMap.put("instructor", updates.getInstructor());
        if (updates.getCategory() != null) updateMap.put("category", updates.getCategory());
        if (updates.getMode() != null) updateMap.put("mode", updates.getMode());
        if (updates.getStartDate() != null) updateMap.put("startDate", updates.getStartDate());
        if (updates.getEndDate() != null) updateMap.put("endDate", updates.getEndDate());
        // if (updates.getCreatedAt() != null) updateMap.put("createdAt", updates.getCreatedAt());

        if (!updateMap.isEmpty()) {
            ref.update(updateMap).get();
        }

        return getCourseById(id);
    }

    @Override
    public boolean deleteCourse(String courseId) throws ExecutionException, InterruptedException {
        try {
            DocumentSnapshot snap = courses.document(courseId).get().get();
            if (!snap.exists()) {
                return false; // Course not found
            }

            courses.document(courseId).delete().get();
            return true; // Successfully deleted
        } catch (Exception e) {
            System.err.println("Error deleting course: " + e.getMessage());
            return false; // Deletion failed
        }
    }

    @Override
    public Course enrollUser(String courseId, String userId) throws ExecutionException, InterruptedException {
        DocumentReference courseRef = courses.document(courseId);
        Firestore db = courseRef.getFirestore();

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(courseRef).get();
            if (!snap.exists()) {
                throw new IllegalStateException("Course not found: " + courseId);
            }

            // read existing enrolledUsers (may be null)
            List<String> enrolled = (List<String>) snap.get("enrolledUsers");
            if (enrolled == null) {
                transaction.update(courseRef, "enrolledUsers", List.of(userId));
            } else if (!enrolled.contains(userId)) {
                List<String> mod = new java.util.ArrayList<>(enrolled);
                mod.add(userId);
                transaction.update(courseRef, "enrolledUsers", mod);
            }
            return null;
        }).get();

        // fetch and return the updated course
        DocumentSnapshot updatedSnap = courseRef.get().get();
        if (!updatedSnap.exists()) {
            return null; // caller/controller will handle as 404
        }
        Course updated = updatedSnap.toObject(Course.class);
        if (updated != null) updated.setId(courseId);
        return updated;
    }

    @Override
    public List<Course> getCoursesForUser(String userId) throws ExecutionException, InterruptedException {
        Query q = courses.whereArrayContains("enrolledUsers", userId);
        return q.get().get().toObjects(Course.class);
    }

    @Override
    public List<Course> listCourses(int limit) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = courses.limit(limit).get();
        return future.get().toObjects(Course.class);
    }
}



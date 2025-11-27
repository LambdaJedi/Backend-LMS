package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Course;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CourseService {
    List<Course> getAllCourses() throws ExecutionException, InterruptedException;
    Course getCourseById(String id) throws ExecutionException, InterruptedException;
    Course createCourse(Course course) throws ExecutionException, InterruptedException;
    Course updateCourse(String id, Course updates) throws ExecutionException, InterruptedException;
    boolean deleteCourse(String courseId) throws ExecutionException, InterruptedException;
    Course enrollUser(String courseId, String userId) throws ExecutionException, InterruptedException;
    List<Course> getCoursesForUser(String userId) throws ExecutionException, InterruptedException;
    List<Course> listCourses(int limit) throws ExecutionException, InterruptedException;


}


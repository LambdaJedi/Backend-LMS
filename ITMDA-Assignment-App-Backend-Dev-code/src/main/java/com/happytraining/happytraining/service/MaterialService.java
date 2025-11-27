package com.happytraining.happytraining.service;

import com.happytraining.happytraining.model.Material;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface MaterialService {
    List<Material> getMaterialsForCourse(String courseId) throws ExecutionException, InterruptedException;
    Material getMaterialById(String id) throws ExecutionException, InterruptedException;
    Material addMaterialToCourse(String courseId, Material metadata, MultipartFile file) throws ExecutionException, InterruptedException;
    void deleteMaterial(String id) throws ExecutionException, InterruptedException;

    // ==================== NEW METHODS FOR SUBMISSIONS ====================
    List<Material> getAllMaterials() throws ExecutionException, InterruptedException;
    Material updateMaterial(String id, Material updates) throws ExecutionException, InterruptedException;
}


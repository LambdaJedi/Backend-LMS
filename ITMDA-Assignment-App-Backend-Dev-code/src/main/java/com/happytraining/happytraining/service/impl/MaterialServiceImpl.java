package com.happytraining.happytraining.service.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.happytraining.happytraining.model.Material;
import com.happytraining.happytraining.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class MaterialServiceImpl implements MaterialService {

    private final CollectionReference materials;
    private final CollectionReference courses;

    public MaterialServiceImpl(Firestore firestore) {
        this.materials = firestore.collection("materials");
        this.courses = firestore.collection("courses");
    }

    // EXISTING METHODS - KEEP AS IS
    @Override
    public List<Material> getMaterialsForCourse(String courseId) throws ExecutionException, InterruptedException {
        Query q = materials.whereEqualTo("courseId", courseId);
        return q.get().get().toObjects(Material.class);
    }

    @Override
    public Material getMaterialById(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = materials.document(id).get().get();
        return snap.exists() ? snap.toObject(Material.class) : null;
    }

    @Override
    public Material addMaterialToCourse(String courseId, Material metadata, MultipartFile file) throws ExecutionException, InterruptedException {
        metadata.setCourseId(courseId);
        DocumentReference ref = materials.document();
        ref.set(metadata).get();
        courses.document(courseId).update("materialIds", FieldValue.arrayUnion(ref.getId())).get();
        metadata.setId(ref.getId());
        return metadata;
    }

    @Override
    public void deleteMaterial(String id) throws ExecutionException, InterruptedException {
        DocumentSnapshot snap = materials.document(id).get().get();
        if (!snap.exists()) return;
        Material m = snap.toObject(Material.class);
        if (m != null && m.getCourseId() != null) {
            courses.document(m.getCourseId()).update("materialIds", FieldValue.arrayRemove(id)).get();
        }
        materials.document(id).delete().get();
    }

    // ==================== NEW METHODS FOR SUBMISSIONS ====================

    @Override
    public List<Material> getAllMaterials() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = materials.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Material> allMaterials = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            allMaterials.add(document.toObject(Material.class));
        }
        return allMaterials;
    }

    @Override
    public Material updateMaterial(String id, Material updates) throws ExecutionException, InterruptedException {
        DocumentReference docRef = materials.document(id);
        DocumentSnapshot existingDoc = docRef.get().get();

        if (!existingDoc.exists()) {
            return null;
        }

        // Create update map with only non-null fields
        Map<String, Object> updateData = new HashMap<>();

        // Existing material fields
        if (updates.getTitle() != null) updateData.put("title", updates.getTitle());
        if (updates.getDescription() != null) updateData.put("description", updates.getDescription());
        if (updates.getFileUrl() != null) updateData.put("fileUrl", updates.getFileUrl());
        if (updates.getCourseId() != null) updateData.put("courseId", updates.getCourseId());

        // New submission fields
        if (updates.getType() != null) updateData.put("type", updates.getType());
        if (updates.getLearnerId() != null) updateData.put("learnerId", updates.getLearnerId());
        if (updates.getLearnerName() != null) updateData.put("learnerName", updates.getLearnerName());
        if (updates.getStatus() != null) updateData.put("status", updates.getStatus());
        if (updates.getComment() != null) updateData.put("comment", updates.getComment());
        if (updates.getSubmittedAt() != null) updateData.put("submittedAt", updates.getSubmittedAt());
        if (updates.getReviewedAt() != null) updateData.put("reviewedAt", updates.getReviewedAt());
        if (updates.getReviewerId() != null) updateData.put("reviewerId", updates.getReviewerId());

        // Perform the update
        ApiFuture<WriteResult> future = docRef.update(updateData);
        future.get();

        // Return the updated material
        return getMaterialById(id);
    }
}
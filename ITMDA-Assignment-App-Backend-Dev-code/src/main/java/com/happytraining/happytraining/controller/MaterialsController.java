package com.happytraining.happytraining.controller;

import com.happytraining.happytraining.model.Material;
import com.happytraining.happytraining.service.MaterialService;
import com.happytraining.happytraining.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/materials")
public class MaterialsController {

    @Autowired
    private MaterialService materialService;

    // EXISTING ENDPOINTS - KEEP AS IS
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Material>> getMaterialsForCourse(@PathVariable String courseId) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(materialService.getMaterialsForCourse(courseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable String id) throws ExecutionException, InterruptedException {
        Material m = materialService.getMaterialById(id);
        return m == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(m);
    }

    @PostMapping(value = "/course/{courseId}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Material> addMaterial(@PathVariable String courseId,
                                                @RequestPart("metadata") Material metadata,
                                                @RequestPart(value = "file", required = false) MultipartFile file) throws ExecutionException, InterruptedException {
        String uid = AuthUtil.currentUid();
        if (uid == null) return ResponseEntity.status(401).build();
        Material saved = materialService.addMaterialToCourse(courseId, metadata, file);
        return ResponseEntity.status(201).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) throws ExecutionException, InterruptedException {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== TEMPORARY SUBMISSION ENDPOINTS ====================

    @GetMapping("/submissions")
    public ResponseEntity<List<Material>> getAllSubmissions() throws ExecutionException, InterruptedException {
        // Get all materials and filter for submissions
        List<Material> allMaterials = materialService.getAllMaterials();
        List<Material> submissions = allMaterials.stream()
                .filter(material -> "SUBMISSION".equals(material.getType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/submissions/course/{courseId}")
    public ResponseEntity<List<Material>> getSubmissionsForCourse(@PathVariable String courseId) throws ExecutionException, InterruptedException {
        List<Material> courseMaterials = materialService.getMaterialsForCourse(courseId);
        List<Material> submissions = courseMaterials.stream()
                .filter(material -> "SUBMISSION".equals(material.getType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/submissions/learner/{learnerId}")
    public ResponseEntity<List<Material>> getSubmissionsByLearner(@PathVariable String learnerId) throws ExecutionException, InterruptedException {
        List<Material> allMaterials = materialService.getAllMaterials();
        List<Material> submissions = allMaterials.stream()
                .filter(material -> "SUBMISSION".equals(material.getType()) && learnerId.equals(material.getLearnerId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(submissions);
    }

    @PatchMapping("/submissions/{id}")
    public ResponseEntity<Material> updateSubmission(@PathVariable String id, @RequestBody Material updates) throws ExecutionException, InterruptedException {
        Material existing = materialService.getMaterialById(id);

        if (existing == null || !"SUBMISSION".equals(existing.getType())) {
            return ResponseEntity.notFound().build();
        }

        // Only update submission-specific fields
        if (updates.getStatus() != null) existing.setStatus(updates.getStatus());
        if (updates.getComment() != null) existing.setComment(updates.getComment());
        if (updates.getReviewedAt() != null) existing.setReviewedAt(updates.getReviewedAt());
        if (updates.getReviewerId() != null) existing.setReviewerId(updates.getReviewerId());

        Material updated = materialService.updateMaterial(id, existing);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/submissions/stats")
    public ResponseEntity<Map<String, Object>> getSubmissionStats() throws ExecutionException, InterruptedException {
        List<Material> allMaterials = materialService.getAllMaterials();
        List<Material> submissions = allMaterials.stream()
                .filter(material -> "SUBMISSION".equals(material.getType()))
                .collect(Collectors.toList());

        int total = submissions.size();
        int reviewed = (int) submissions.stream().filter(s -> "REVIEWED".equals(s.getStatus())).count();
        int pending = total - reviewed;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("reviewed", reviewed);
        stats.put("pending", pending);

        return ResponseEntity.ok(stats);
    }

    // Helper endpoint to check if submission functionality is available
    @GetMapping("/submissions/health")
    public ResponseEntity<Map<String, String>> submissionsHealth() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "active");
        response.put("message", "Temporary submission endpoints are available");
        response.put("note", "This uses Materials controller temporarily");
        return ResponseEntity.ok(response);
    }
}



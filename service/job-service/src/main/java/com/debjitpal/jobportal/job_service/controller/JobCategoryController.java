package com.debjitpal.jobportal.job_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.debjitpal.jobportal.job_service.payload.JobCategoryRequest;
import com.debjitpal.jobportal.job_service.payload.JobCategoryResponse;
import com.debjitpal.jobportal.job_service.service.JobCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/job-categories")
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    @PostMapping
    public ResponseEntity<JobCategoryResponse> createCategory(@RequestBody JobCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(jobCategoryService.createCategory(request));
    }

    @GetMapping
    public ResponseEntity<List<JobCategoryResponse>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobCategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobCategoryResponse> updateCategory(@PathVariable Long id, @RequestBody JobCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        jobCategoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

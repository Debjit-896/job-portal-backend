package com.debjitpal.jobportal.job_service.repository;

import com.debjitpal.jobportal.domain.JobStatus;
import com.debjitpal.jobportal.job_service.model.Job;
import com.debjitpal.jobportal.job_service.payload.JobSearchRequest;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class JobSpecification {
    private JobSpecification() {}

    public static Specification<Job> build(JobSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // active flag in entity is named 'isActive'
            predicates.add(cb.isTrue(root.get("isActive")));

            JobStatus status = request.getJobStatus() != null ? request.getJobStatus() : JobStatus.OPEN;
            predicates.add(cb.equal(root.get("jobStatus"), status));

            if (request.getJobType() != null) {
                predicates.add(cb.equal(root.get("jobType"), request.getJobType()));
            }

            if (request.getWorkMode() != null) {
                predicates.add(cb.equal(root.get("workMode"), request.getWorkMode()));
            }

            if (request.getExperienceLevel() != null) {
                predicates.add(cb.equal(root.get("experienceLevel"), request.getExperienceLevel()));
            }

            if (request.getCompanyId() != null) {
                predicates.add(cb.equal(root.get("companyId"), request.getCompanyId()));
            }

            // Keyword search on title or description
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String like = "%" + request.getKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like)
                ));
            }

            if (request.getLocation() != null && !request.getLocation().isBlank()){
                String pattern = "%" + request.getLocation().toLowerCase() + "%";
                Path<String> city = root.get("location").get("city");
                Path<String> state = root.get("location").get("state");
                Path<String> country = root.get("location").get("country");
                predicates.add(cb.or(
                        cb.like(cb.lower(city), pattern),
                        cb.like(cb.lower(state), pattern),
                        cb.like(cb.lower(country), pattern)
                ));
            }

            if (request.getMinSalary() != null) {
                // match jobs whose maximum salary is >= requested min salary
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryRange").get("maximumSalary"), request.getMinSalary()));
            }

            if (request.getMaxSalary() != null) {
                // match jobs whose minimum salary is <= requested max salary
                predicates.add(cb.lessThanOrEqualTo(root.get("salaryRange").get("minimumSalary"), request.getMaxSalary()));
            }

            if (request.getMinOpenings() != null){
                predicates.add(cb.greaterThanOrEqualTo(root.get("openings"), request.getMinOpenings()));
            }

            if (request.getMaxOpenings() != null){
                predicates.add(cb.lessThanOrEqualTo(root.get("openings"), request.getMaxOpenings()));
            }

            // TODO: Filter of tag and skills

            // Combine predicates
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

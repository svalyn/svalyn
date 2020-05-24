/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.Requirement;
import com.svalyn.application.dto.output.Status;
import com.svalyn.application.dto.output.Test;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.StatusEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.DescriptionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentService {

    private final DescriptionRepository descriptionRepository;

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(DescriptionRepository descriptionRepository, AssessmentRepository assessmentRepository) {
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public Mono<Assessment> createAssessment(UUID projectId, UUID descriptionId, String label) {
        Optional<DescriptionEntity> optionalDescriptionEntity = this.descriptionRepository
                .findDescriptionById(descriptionId);
        if (optionalDescriptionEntity.isPresent()) {
            DescriptionEntity descriptionEntity = optionalDescriptionEntity.get();

            AssessmentEntity assessmentEntity = new AssessmentEntity();
            assessmentEntity.setId(UUID.randomUUID());
            assessmentEntity.setDescriptionId(descriptionEntity.getId());
            assessmentEntity.setProjectId(projectId);
            assessmentEntity.setLabel(label);
            assessmentEntity.setResults(new HashMap<>());
            assessmentEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
            assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

            return this.assessmentRepository.save(assessmentEntity)
                    .map(savedAssessmentEntity -> this.convert(descriptionEntity, savedAssessmentEntity));
        }
        return Mono.empty();
    }

    public Flux<Assessment> findByProjectId(UUID projectId) {
        // @formatter:off
        return this.assessmentRepository.findAll()
                .filter(assessmentEntity -> assessmentEntity.getProjectId().equals(projectId))
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Flux.fromStream(optionalAssessment.stream());
                });
        // @formatter:on
    }

    public Mono<Assessment> findById(UUID assessmentId) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment);
                });
        // @formatter:on
    }

    public Mono<Assessment> updateTest(UUID assessmentId, UUID testId, Status status) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    var statusEntity = this.convert(status);
                    assessmentEntity.getResults().put(testId, statusEntity);
                    assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    return this.assessmentRepository.save(assessmentEntity);
                }).flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment);
                });
        // @formatter:on
    }

    private Assessment convert(DescriptionEntity descriptionEntity, AssessmentEntity assessmentEntity) {
        List<Category> categories = new ArrayList<>();
        for (CategoryEntity categoryEntity : descriptionEntity.getCategories()) {
            List<Requirement> requirements = new ArrayList<>();
            for (RequirementEntity requirementEntity : categoryEntity.getRequirements()) {
                List<Test> tests = new ArrayList<>();
                for (TestEntity testEntity : requirementEntity.getTests()) {
                    StatusEntity statusEntity = assessmentEntity.getResults().get(testEntity.getId());
                    Status status = this.convert(statusEntity);

                    tests.add(new Test(testEntity.getId(), testEntity.getLabel(), testEntity.getDescription(), status));
                }

                requirements.add(new Requirement(requirementEntity.getId(), requirementEntity.getLabel(),
                        requirementEntity.getDescription(), tests));
            }

            categories.add(new Category(categoryEntity.getId(), categoryEntity.getLabel(),
                    categoryEntity.getDescription(), requirements));
        }
        Assessment assessment = new Assessment(assessmentEntity.getId(), assessmentEntity.getLabel(), categories,
                assessmentEntity.getCreatedOn(), assessmentEntity.getLastModifiedOn());
        return assessment;
    }

    private Status convert(StatusEntity statusEntity) {
        if (statusEntity == StatusEntity.SUCCESS) {
            return Status.SUCCESS;
        } else if (statusEntity == StatusEntity.FAILURE) {
            return Status.FAILURE;
        }
        return null;
    }

    private StatusEntity convert(Status status) {
        if (status == Status.SUCCESS) {
            return StatusEntity.SUCCESS;
        } else if (status == Status.FAILURE) {
            return StatusEntity.FAILURE;
        }
        return null;
    }

}

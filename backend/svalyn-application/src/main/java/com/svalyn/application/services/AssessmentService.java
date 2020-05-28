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
import com.svalyn.application.dto.output.AssessmentStatus;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.Requirement;
import com.svalyn.application.dto.output.Test;
import com.svalyn.application.dto.output.TestStatus;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.entities.TestStatusEntity;
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
            assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

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

    public Mono<Assessment> updateAssessmentStatus(UUID assessmentId, AssessmentStatus status) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    assessmentEntity.setStatus(this.convert(status));
                    assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    return this.assessmentRepository.save(assessmentEntity);
                }).flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment);
                });
        // @formatter:on
    }

    public Mono<Assessment> updateTest(UUID assessmentId, UUID testId, TestStatus status) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    if (assessmentEntity.getStatus() == AssessmentStatusEntity.OPEN) {
                        var statusEntity = this.convert(status);
                        assessmentEntity.getResults().put(testId, statusEntity);
                        assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    }
                    return this.assessmentRepository.save(assessmentEntity);
                }).flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment);
                });
        // @formatter:on
    }

    private Assessment convert(DescriptionEntity descriptionEntity, AssessmentEntity assessmentEntity) {
        int success = 0;
        int failure = 0;
        int testCount = 0;

        List<Category> categories = new ArrayList<>();
        for (CategoryEntity categoryEntity : descriptionEntity.getCategories()) {
            List<Requirement> requirements = new ArrayList<>();
            for (RequirementEntity requirementEntity : categoryEntity.getRequirements()) {
                List<Test> tests = new ArrayList<>();
                for (TestEntity testEntity : requirementEntity.getTests()) {
                    TestStatusEntity statusEntity = assessmentEntity.getResults().get(testEntity.getId());
                    TestStatus status = this.convert(statusEntity);

                    if (status == TestStatus.SUCCESS) {
                        success = success + 1;
                    } else if (status == TestStatus.FAILURE) {
                        failure = failure + 1;
                    }

                    tests.add(new Test(testEntity.getId(), testEntity.getLabel(), testEntity.getDescription(),
                            testEntity.getSteps(), status));
                    testCount = testCount + 1;
                }

                requirements.add(new Requirement(requirementEntity.getId(), requirementEntity.getLabel(),
                        requirementEntity.getDescription(), tests));
            }

            categories.add(new Category(categoryEntity.getId(), categoryEntity.getLabel(),
                    categoryEntity.getDescription(), requirements));
        }

        AssessmentStatus status = this.convert(assessmentEntity.getStatus());
        return new Assessment(assessmentEntity.getId(), assessmentEntity.getLabel(), categories,
                assessmentEntity.getCreatedOn(), assessmentEntity.getLastModifiedOn(), success, failure, testCount,
                status);
    }

    private TestStatus convert(TestStatusEntity statusEntity) {
        if (statusEntity == TestStatusEntity.SUCCESS) {
            return TestStatus.SUCCESS;
        } else if (statusEntity == TestStatusEntity.FAILURE) {
            return TestStatus.FAILURE;
        }
        return null;
    }

    private TestStatusEntity convert(TestStatus status) {
        if (status == TestStatus.SUCCESS) {
            return TestStatusEntity.SUCCESS;
        } else if (status == TestStatus.FAILURE) {
            return TestStatusEntity.FAILURE;
        }
        return null;
    }

    private AssessmentStatus convert(AssessmentStatusEntity status) {
        if (status == AssessmentStatusEntity.OPEN) {
            return AssessmentStatus.OPEN;
        }
        return AssessmentStatus.CLOSED;
    }

    private AssessmentStatusEntity convert(AssessmentStatus status) {
        if (status == AssessmentStatus.OPEN) {
            return AssessmentStatusEntity.OPEN;
        }
        return AssessmentStatusEntity.CLOSED;
    }

}

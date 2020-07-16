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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CreateAssessmentInput;
import com.svalyn.application.dto.input.DeleteAssessmentInput;
import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.AssessmentStatus;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.CreateAssessmentSuccessPayload;
import com.svalyn.application.dto.output.DeleteAssessmentSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Requirement;
import com.svalyn.application.dto.output.Test;
import com.svalyn.application.dto.output.TestStatus;
import com.svalyn.application.dto.output.UpdateAssessmentStatusSuccessPayload;
import com.svalyn.application.dto.output.UpdateTestSuccessPayload;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.entities.TestStatusEntity;
import com.svalyn.application.repositories.AssessmentRepository;
import com.svalyn.application.repositories.DescriptionRepository;
import com.svalyn.application.repositories.ProjectRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AssessmentService {

    private final ProjectRepository projectRepository;

    private final DescriptionRepository descriptionRepository;

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(ProjectRepository projectRepository, DescriptionRepository descriptionRepository,
            AssessmentRepository assessmentRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.descriptionRepository = Objects.requireNonNull(descriptionRepository);
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    public Mono<Long> countByProjectId(UUID projectId) {
        return this.assessmentRepository.countByProjectId(projectId);
    }

    public Mono<IPayload> createAssessment(CreateAssessmentInput input) {
        return this.projectRepository.existById(input.getProjectId()).flatMap(existById -> {
            if (existById.booleanValue()) {
                Optional<DescriptionEntity> optionalDescriptionEntity = this.descriptionRepository
                        .findDescriptionById(input.getDescriptionId());
                if (optionalDescriptionEntity.isPresent()) {
                    DescriptionEntity descriptionEntity = optionalDescriptionEntity.get();

                    AssessmentEntity assessmentEntity = new AssessmentEntity();
                    assessmentEntity.setId(UUID.randomUUID());
                    assessmentEntity.setDescriptionId(descriptionEntity.getId());
                    assessmentEntity.setProjectId(input.getProjectId());
                    assessmentEntity.setLabel(input.getLabel());
                    assessmentEntity.setResults(new HashMap<>());
                    assessmentEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                    assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

                    return this.assessmentRepository.save(assessmentEntity)
                            .map(savedAssessmentEntity -> this.convert(descriptionEntity, savedAssessmentEntity))
                            .map(CreateAssessmentSuccessPayload::new);
                }
                return Mono.just(new ErrorPayload("The description does not exist"));
            }
            return Mono.just(new ErrorPayload("The project does not exist"));
        }).filter(IPayload.class::isInstance).map(IPayload.class::cast);
    }

    public Mono<IPayload> deleteAssessment(DeleteAssessmentInput input) {
        return this.assessmentRepository.existById(input.getAssessmentId()).flatMap(existById -> {
            if (existById.booleanValue()) {
                // @formatter:off
                return this.assessmentRepository.deleteAssessment(input.getAssessmentId())
                        .then(Mono.just(new DeleteAssessmentSuccessPayload()));
                // @formatter:on
            }
            return Mono.just(new ErrorPayload("The assessment does not exist"));
        }).filter(IPayload.class::isInstance).map(IPayload.class::cast);

    }

    public Flux<Assessment> findAllByProjectId(UUID projectId, Pageable pageable) {
        // @formatter:off
        return this.assessmentRepository.findAllByProjectId(projectId, pageable)
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

    public Mono<IPayload> updateAssessmentStatus(UpdateAssessmentStatusInput input) {
        // @formatter:off
        return this.assessmentRepository.findById(input.getAssessmentId())
                .flatMap(assessmentEntity -> {
                    assessmentEntity.setStatus(this.convert(input.getStatus()));
                    assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    return this.assessmentRepository.save(assessmentEntity);
                }).flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment).map(UpdateAssessmentStatusSuccessPayload::new);
                }).filter(IPayload.class::isInstance).map(IPayload.class::cast)
                .switchIfEmpty(Mono.just(new ErrorPayload("The assessment does not exist")).filter(IPayload.class::isInstance).map(IPayload.class::cast));
        // @formatter:on
    }

    public Mono<IPayload> updateTest(UpdateTestInput input) {
        // @formatter:off
        return this.assessmentRepository.findById(input.getAssessmentId())
                .flatMap(assessmentEntity -> {
                    if (assessmentEntity.getStatus() == AssessmentStatusEntity.OPEN) {
                        var statusEntity = this.convert(input.getStatus());
                        assessmentEntity.getResults().put(input.getTestId(), statusEntity);
                        assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                    }
                    return this.assessmentRepository.save(assessmentEntity);
                }).flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return Mono.justOrEmpty(optionalAssessment).map(UpdateTestSuccessPayload::new);
                }).filter(IPayload.class::isInstance).map(IPayload.class::cast)
                .switchIfEmpty(Mono.just(new ErrorPayload("The assessment or its test does not exist")).filter(IPayload.class::isInstance).map(IPayload.class::cast));
        // @formatter:on
    }

    private Assessment convert(DescriptionEntity descriptionEntity, AssessmentEntity assessmentEntity) {
        int success = 0;
        int failure = 0;
        int testCount = 0;

        List<Category> categories = new ArrayList<>();
        for (CategoryEntity categoryEntity : descriptionEntity.getCategories()) {
            int previousCategorySuccess = success;
            int previousCategoryFailure = failure;

            List<Requirement> requirements = new ArrayList<>();
            for (RequirementEntity requirementEntity : categoryEntity.getRequirements()) {
                int previousRequirementSuccess = success;
                int previousRequirementFailure = failure;

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

                TestStatus requirementStatus = null;
                if (previousRequirementSuccess + requirementEntity.getTests().size() == success) {
                    requirementStatus = TestStatus.SUCCESS;
                } else if (previousRequirementFailure != failure) {
                    requirementStatus = TestStatus.FAILURE;
                }

                requirements.add(new Requirement(requirementEntity.getId(), requirementEntity.getLabel(),
                        requirementEntity.getDescription(), tests, requirementStatus));
            }

            // @formatter:off
            var tests = categoryEntity.getRequirements().stream()
                    .map(RequirementEntity::getTests)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            // @formatter:on

            TestStatus categoryStatus = null;
            if (previousCategorySuccess + tests.size() == success) {
                categoryStatus = TestStatus.SUCCESS;
            } else if (previousCategoryFailure != failure) {
                categoryStatus = TestStatus.FAILURE;
            }

            categories.add(new Category(categoryEntity.getId(), categoryEntity.getLabel(),
                    categoryEntity.getDescription(), requirements, categoryStatus));
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

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
import com.svalyn.application.dto.input.DeleteAssessmentsInput;
import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.AssessmentStatus;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.CreateAssessmentSuccessPayload;
import com.svalyn.application.dto.output.DeleteAssessmentsSuccessPayload;
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

    public int countByProjectId(UUID projectId) {
        return this.assessmentRepository.countByProjectId(projectId);
    }

    public IPayload createAssessment(UUID userId, CreateAssessmentInput input) {
        boolean exists = this.projectRepository.existsById(input.getProjectId());
        if (exists) {
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
                assessmentEntity.setCreatedBy(userId);
                assessmentEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setLastModifiedBy(userId);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));
                assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

                AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                Assessment assessment = this.convert(descriptionEntity, savedAssessmentEntity);
                return new CreateAssessmentSuccessPayload(assessment);
            }
            return new ErrorPayload("The description does not exist");
        }
        return new ErrorPayload("The project does not exist");
    }

    public IPayload deleteAssessments(DeleteAssessmentsInput input) {
        this.assessmentRepository.deleteAssessments(input.getAssessmentIds());
        return new DeleteAssessmentsSuccessPayload();
    }

    public List<Assessment> findAllByProjectId(UUID projectId, Pageable pageable) {
        // @formatter:off
        return this.assessmentRepository.findAllByProjectId(projectId, pageable).stream()
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    var optionalAssessment = optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                    return optionalAssessment.stream();
                }).collect(Collectors.toList());
        // @formatter:on
    }

    public Optional<Assessment> findById(UUID assessmentId) {
        // @formatter:off
        return this.assessmentRepository.findById(assessmentId)
                .flatMap(assessmentEntity -> {
                    var optionalDescriptionEntity = this.descriptionRepository.findDescriptionById(assessmentEntity.getDescriptionId());
                    return optionalDescriptionEntity.map(descriptionEntity -> this.convert(descriptionEntity, assessmentEntity));
                });
        // @formatter:on
    }

    public IPayload updateAssessmentStatus(UUID userId, UpdateAssessmentStatusInput input) {
        var optionalAssessmentEntity = this.assessmentRepository.findById(input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent()) {
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();
            assessmentEntity.setStatus(this.convert(input.getStatus()));
            assessmentEntity.setLastModifiedBy(userId);
            assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

            AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
            var optionalDescriptionEntity = this.descriptionRepository
                    .findDescriptionById(savedAssessmentEntity.getDescriptionId());
            var optionalAssessment = optionalDescriptionEntity
                    .map(descriptionEntity -> this.convert(descriptionEntity, savedAssessmentEntity));
            if (optionalAssessment.isPresent()) {
                var assessment = optionalAssessment.get();
                return new UpdateAssessmentStatusSuccessPayload(assessment);
            }
        }
        return new ErrorPayload("The assessment does not exist");
    }

    public IPayload updateTest(UUID userId, UpdateTestInput input) {
        var optionalAssessmentEntity = this.assessmentRepository.findById(input.getAssessmentId());
        if (optionalAssessmentEntity.isPresent()) {
            AssessmentEntity assessmentEntity = optionalAssessmentEntity.get();
            if (assessmentEntity.getStatus() == AssessmentStatusEntity.OPEN) {
                var statusEntity = this.convert(input.getStatus());
                assessmentEntity.getResults().put(input.getTestId(), statusEntity);
                assessmentEntity.setLastModifiedBy(userId);
                assessmentEntity.setLastModifiedOn(LocalDateTime.now(ZoneOffset.UTC));

                AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
                var optionalDescriptionEntity = this.descriptionRepository
                        .findDescriptionById(savedAssessmentEntity.getDescriptionId());
                var optionalAssessment = optionalDescriptionEntity
                        .map(descriptionEntity -> this.convert(descriptionEntity, savedAssessmentEntity));
                if (optionalAssessment.isPresent()) {
                    var assessment = optionalAssessment.get();
                    return new UpdateTestSuccessPayload(assessment);
                }
            }
        }
        return new ErrorPayload("The assessment does not exist");
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
        return new Assessment(assessmentEntity.getId(), assessmentEntity.getLabel(), assessmentEntity.getCreatedBy(),
                assessmentEntity.getCreatedOn(), assessmentEntity.getLastModifiedBy(),
                assessmentEntity.getLastModifiedOn(), categories, success, failure, testCount, status);
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

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Account;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.AssessmentStatus;
import com.svalyn.application.dto.output.Category;
import com.svalyn.application.dto.output.Requirement;
import com.svalyn.application.dto.output.Test;
import com.svalyn.application.dto.output.TestStatus;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.entities.TestResultEntity;
import com.svalyn.application.entities.TestResultStatusEntity;

@Service
public class AssessmentConverter {
    public Assessment convert(AssessmentEntity assessmentEntity) {
        int success = 0;
        int failure = 0;
        int testCount = 0;

        // @formatter:off
        Map<UUID, TestResultEntity> results = assessmentEntity.getResults().stream()
                .collect(Collectors.toMap(result -> result.getTest().getId(), result -> result));
        // @formatter:on

        List<Category> categories = new ArrayList<>();
        for (CategoryEntity categoryEntity : assessmentEntity.getDescription().getCategories()) {
            int previousCategorySuccess = success;
            int previousCategoryFailure = failure;

            List<Requirement> requirements = new ArrayList<>();
            for (RequirementEntity requirementEntity : categoryEntity.getRequirements()) {
                int previousRequirementSuccess = success;
                int previousRequirementFailure = failure;

                List<Test> tests = new ArrayList<>();
                for (TestEntity testEntity : requirementEntity.getTests()) {
                    var optionalTestResultEntity = Optional.ofNullable(results.get(testEntity.getId()));
                    TestResultStatusEntity statusEntity = optionalTestResultEntity.map(TestResultEntity::getStatus)
                            .orElse(null);
                    TestStatus status = this.convert(statusEntity);

                    if (status == TestStatus.SUCCESS) {
                        success = success + 1;
                    } else if (status == TestStatus.FAILURE) {
                        failure = failure + 1;
                    }

                    tests.add(new Test(testEntity.getId(), testEntity.getLabel(), testEntity.getDetails(),
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
                        requirementEntity.getDetails(), tests, requirementStatus));
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

            categories.add(new Category(categoryEntity.getId(), categoryEntity.getLabel(), categoryEntity.getDetails(),
                    requirements, categoryStatus));
        }

        AssessmentStatus status = this.convert(assessmentEntity.getStatus());
        Account createdBy = new Account(assessmentEntity.getCreatedBy().getId(),
                assessmentEntity.getCreatedBy().getUsername());
        Account lastModifiedBy = new Account(assessmentEntity.getLastModifiedBy().getId(),
                assessmentEntity.getLastModifiedBy().getUsername());
        return new Assessment(assessmentEntity.getId(), assessmentEntity.getLabel(), createdBy,
                assessmentEntity.getCreatedOn(), lastModifiedBy, assessmentEntity.getLastModifiedOn(), categories,
                success, failure, testCount, status);
    }

    private TestStatus convert(TestResultStatusEntity statusEntity) {
        if (statusEntity == TestResultStatusEntity.SUCCESS) {
            return TestStatus.SUCCESS;
        } else if (statusEntity == TestResultStatusEntity.FAILURE) {
            return TestStatus.FAILURE;
        }
        return null;
    }

    public TestResultStatusEntity convert(TestStatus status) {
        if (status == TestStatus.SUCCESS) {
            return TestResultStatusEntity.SUCCESS;
        } else if (status == TestStatus.FAILURE) {
            return TestResultStatusEntity.FAILURE;
        }
        return null;
    }

    private AssessmentStatus convert(AssessmentStatusEntity status) {
        if (status == AssessmentStatusEntity.OPEN) {
            return AssessmentStatus.OPEN;
        }
        return AssessmentStatus.CLOSED;
    }

    public AssessmentStatusEntity convert(AssessmentStatus status) {
        if (status == AssessmentStatus.OPEN) {
            return AssessmentStatusEntity.OPEN;
        }
        return AssessmentStatusEntity.CLOSED;
    }
}

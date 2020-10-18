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
import java.util.stream.Collectors;

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

@Service
public class AssessmentConverter {
    public Assessment convert(DescriptionEntity descriptionEntity, AssessmentEntity assessmentEntity) {
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

    public TestStatusEntity convert(TestStatus status) {
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

    public AssessmentStatusEntity convert(AssessmentStatus status) {
        if (status == AssessmentStatus.OPEN) {
            return AssessmentStatusEntity.OPEN;
        }
        return AssessmentStatusEntity.CLOSED;
    }
}

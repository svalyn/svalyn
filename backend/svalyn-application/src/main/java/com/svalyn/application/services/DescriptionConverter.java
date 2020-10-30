/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.input.CategoryInput;
import com.svalyn.application.dto.input.DescriptionInput;
import com.svalyn.application.dto.input.RequirementInput;
import com.svalyn.application.dto.input.TestInput;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;

@Service
public class DescriptionConverter {
    public DescriptionEntity convertToEntity(DescriptionInput descriptionInput) {
        DescriptionEntity descriptionEntity = new DescriptionEntity();
        descriptionEntity.setLabel(descriptionInput.getLabel());

        // @formatter:off
        var categories = descriptionInput.getCategories().stream()
                .map(categoryInput -> this.convertToEntity(descriptionEntity, categoryInput))
                .collect(Collectors.toList());
        // @formatter:on

        descriptionEntity.setCategories(categories);
        return descriptionEntity;
    }

    private CategoryEntity convertToEntity(DescriptionEntity descriptionEntity, CategoryInput categoryInput) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setLabel(categoryInput.getLabel());
        categoryEntity.setDetails(categoryInput.getDetails());
        categoryEntity.setDescription(descriptionEntity);

        // @formatter:off
        var requirements = categoryInput.getRequirements().stream()
                .map(requirementInput -> this.convertToEntity(descriptionEntity, categoryEntity, requirementInput))
                .collect(Collectors.toList());
        // @formatter:on

        categoryEntity.setRequirements(requirements);
        return categoryEntity;
    }

    private RequirementEntity convertToEntity(DescriptionEntity descriptionEntity, CategoryEntity categoryEntity,
            RequirementInput requirementInput) {
        RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setDescription(descriptionEntity);
        requirementEntity.setCategory(categoryEntity);
        requirementEntity.setLabel(requirementInput.getLabel());
        requirementEntity.setDetails(requirementInput.getDetails());

        // @formatter:off
        var tests = requirementInput.getTests().stream()
                .map(testInput -> this.convertToEntity(descriptionEntity, categoryEntity, requirementEntity, testInput))
                .collect(Collectors.toList());
        // @formatter:on

        requirementEntity.setTests(tests);
        return requirementEntity;
    }

    private TestEntity convertToEntity(DescriptionEntity descriptionEntity, CategoryEntity categoryEntity,
            RequirementEntity requirementEntity, TestInput testInput) {
        TestEntity testEntity = new TestEntity();
        testEntity.setDescription(descriptionEntity);
        testEntity.setCategory(categoryEntity);
        testEntity.setRequirement(requirementEntity);
        testEntity.setLabel(testInput.getLabel());
        testEntity.setDetails(testInput.getDetails());
        List<String> steps = Optional.ofNullable(testInput.getSteps()).orElse(new ArrayList<>());
        testEntity.setSteps(steps);
        return testEntity;
    }
}

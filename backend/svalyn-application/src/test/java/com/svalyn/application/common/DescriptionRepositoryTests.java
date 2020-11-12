/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.repositories.IDescriptionRepository;
import com.svalyn.application.repositories.PostgreSQLTestContainer;
import com.svalyn.application.repositories.RepositoryTestConfiguration;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class DescriptionRepositoryTests {
    @Container
    private static PostgreSQLTestContainer postgreSQLContainer = new PostgreSQLTestContainer();

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private IDescriptionRepository descriptionRepository;

    @Test
    @Transactional
    public void testDescriptionCreationAndRetrieval() {
        DescriptionEntity descriptionEntity = new DescriptionEntity();
        descriptionEntity.setLabel("label");

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setLabel("label");
        categoryEntity.setDetails("details");

        RequirementEntity requirementEntity = new RequirementEntity();
        requirementEntity.setLabel("label");
        requirementEntity.setDetails("details");

        TestEntity testEntity = new TestEntity();
        testEntity.setLabel("label");
        testEntity.setDetails("details");
        testEntity.setSteps(List.of("one", "two", "three"));
        testEntity.setDescription(descriptionEntity);
        testEntity.setCategory(categoryEntity);
        testEntity.setRequirement(requirementEntity);

        requirementEntity.setDescription(descriptionEntity);
        requirementEntity.setCategory(categoryEntity);
        requirementEntity.setTests(List.of(testEntity));

        categoryEntity.setDescription(descriptionEntity);
        categoryEntity.setRequirements(List.of(requirementEntity));

        descriptionEntity.setCategories(List.of(categoryEntity));

        assertThat(this.descriptionRepository.count()).isZero();

        DescriptionEntity savedDescriptionEntity = this.descriptionRepository.save(descriptionEntity);
        assertThat(this.descriptionRepository.count()).isEqualTo(1);
        assertThat(savedDescriptionEntity.getId()).isNotNull();
        assertThat(savedDescriptionEntity.getCategories().size()).isEqualTo(1);

        CategoryEntity savedCategoryEntity = savedDescriptionEntity.getCategories().get(0);
        assertThat(savedCategoryEntity.getId()).isNotNull();
        assertThat(savedCategoryEntity.getRequirements().size()).isEqualTo(1);

        RequirementEntity savedRequirementEntity = savedCategoryEntity.getRequirements().get(0);
        assertThat(savedRequirementEntity.getId()).isNotNull();
        assertThat(savedRequirementEntity.getTests().size()).isEqualTo(1);

        TestEntity savedTestEntity = savedRequirementEntity.getTests().get(0);
        assertThat(savedTestEntity.getId()).isNotNull();
        assertThat(savedTestEntity.getSteps().size()).isEqualTo(3);

    }
}

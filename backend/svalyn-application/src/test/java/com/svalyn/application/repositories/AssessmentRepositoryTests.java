/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.AssessmentEntity;
import com.svalyn.application.entities.AssessmentStatusEntity;
import com.svalyn.application.entities.CategoryEntity;
import com.svalyn.application.entities.DescriptionEntity;
import com.svalyn.application.entities.ProjectEntity;
import com.svalyn.application.entities.RequirementEntity;
import com.svalyn.application.entities.TestEntity;
import com.svalyn.application.entities.TestResultEntity;
import com.svalyn.application.entities.TestResultStatusEntity;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class AssessmentRepositoryTests {
    @Container
    private static PostgreSQLTestContainer postgreSQLContainer = new PostgreSQLTestContainer();

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private IDescriptionRepository descriptionRepository;

    @Autowired
    private IProjectRepository projectRepository;

    @Autowired
    private IAssessmentRepository assessmentRepository;

    @Test
    @Transactional
    public void testAssessmentCreationAndRetrieval() {
        AccountEntity accountEntity = this.createAccount();
        ProjectEntity projectEntity = this.createProject(accountEntity);
        DescriptionEntity descriptionEntity = this.createDescription();

        AssessmentEntity assessmentEntity = new AssessmentEntity();
        assessmentEntity.setDescription(descriptionEntity);
        assessmentEntity.setProject(projectEntity);
        assessmentEntity.setLabel("label");
        assessmentEntity.setCreatedBy(accountEntity);
        assessmentEntity.setCreatedOn(LocalDateTime.now());
        assessmentEntity.setLastModifiedBy(accountEntity);
        assessmentEntity.setLastModifiedOn(LocalDateTime.now());
        assessmentEntity.setResults(new ArrayList<>());
        assessmentEntity.setStatus(AssessmentStatusEntity.OPEN);

        AssessmentEntity savedAssessmentEntity = this.assessmentRepository.save(assessmentEntity);
        assertThat(savedAssessmentEntity.getId()).isNotNull();

        TestEntity testEntity = descriptionEntity.getCategories().get(0).getRequirements().get(0).getTests().get(0);

        TestResultEntity testResultEntity = new TestResultEntity();
        testResultEntity.setTest(testEntity);
        testResultEntity.setAssessment(savedAssessmentEntity);
        testResultEntity.setStatus(TestResultStatusEntity.FAILURE);
        savedAssessmentEntity.getResults().add(testResultEntity);

        this.assessmentRepository.save(savedAssessmentEntity);

        var optionalAssessmentEntity = this.assessmentRepository.findById(savedAssessmentEntity.getId());
        assertThat(optionalAssessmentEntity).isPresent();
        AssessmentEntity foundAssessmentEntity = optionalAssessmentEntity.get();
        assertThat(foundAssessmentEntity.getResults()).hasSize(1);
    }

    private AccountEntity createAccount() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUsername("username");
        accountEntity.setPassword("password");

        return this.accountRepository.save(accountEntity);
    }

    private ProjectEntity createProject(AccountEntity accountEntity) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setLabel("label");
        projectEntity.setCreatedBy(accountEntity);
        projectEntity.setCreatedOn(LocalDateTime.now(ZoneOffset.UTC));

        return this.projectRepository.save(projectEntity);
    }

    private DescriptionEntity createDescription() {
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

        return this.descriptionRepository.save(descriptionEntity);
    }

}

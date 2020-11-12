/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
    public void testFindByUserIdAndAssessmentId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Robert"),
                data.get("WestworldOpeningAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Robert"),
                data.get("WestworldMidSeasonAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Robert"),
                data.get("WestworldEndSeasonAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Dolores"),
                data.get("WestworldEndSeasonAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Maeve"),
                data.get("WestworldEndSeasonAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByUserIdAndAssessmentId(data.get("Musashi"),
                data.get("WestworldOpeningAssessment"))).isEmpty();
    }

    @Test
    @Transactional
    public void testCountByProjectId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();
        assertThat(this.assessmentRepository.countByProjectId(data.get("Westworld"))).isEqualTo(3);
        assertThat(this.assessmentRepository.countByProjectId(data.get("Eastworld"))).isZero();
    }

    @Test
    @Transactional
    public void testFindAllByProjectId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();
        assertThat(this.assessmentRepository.findAllByProjectId(data.get("Westworld"), PageRequest.of(0, 10)))
                .hasSize(3);
        assertThat(this.assessmentRepository.findAllByProjectId(data.get("Eastworld"), PageRequest.of(0, 10)))
                .isEmpty();
    }

    @Test
    @Transactional
    public void testFindByProjectIdAndAssessmentId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.assessmentRepository.findByProjectIdAndAssessmentId(data.get("Westworld"),
                data.get("WestworldOpeningAssessment"))).isPresent();
        assertThat(this.assessmentRepository.findByProjectIdAndAssessmentId(data.get("Eastworld"),
                data.get("WestworldOpeningAssessment"))).isEmpty();
    }

    @Test
    @Transactional
    public void testDeleteWithIds() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.assessmentRepository.countByProjectId(data.get("Westworld"))).isEqualTo(3);

        this.assessmentRepository.deleteWithIds(data.get("Robert"),
                List.of(data.get("WestworldOpeningAssessment"), data.get("WestworldMidSeasonAssessment")));

        assertThat(this.assessmentRepository.countByProjectId(data.get("Westworld"))).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testAreAllInProject() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.assessmentRepository.areAllInProject(data.get("Westworld"),
                List.of(data.get("WestworldOpeningAssessment"), data.get("WestworldMidSeasonAssessment"),
                        data.get("WestworldEndSeasonAssessment")))).isTrue();
        assertThat(this.assessmentRepository.areAllInProject(data.get("Eastworld"),
                List.of(data.get("WestworldOpeningAssessment"), data.get("WestworldMidSeasonAssessment"),
                        data.get("WestworldEndSeasonAssessment")))).isFalse();
    }

}

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RepositoryTestConfiguration.class)
public class ProjectRepositoryTests {

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
    public void testFindByUserIdAndProjectId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.findByUserIdAndProjectId(data.get("Robert"), data.get("Westworld")))
                .isPresent();
        assertThat(this.projectRepository.findByUserIdAndProjectId(data.get("Dolores"), data.get("Westworld")))
                .isPresent();
        assertThat(this.projectRepository.findByUserIdAndProjectId(data.get("Robert"), data.get("Eastworld")))
                .isPresent();
        assertThat(this.projectRepository.findByUserIdAndProjectId(data.get("Robert"), data.get("Northworld")))
                .isEmpty();
    }

    @Test
    @Transactional
    public void testFindAllByUserId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.findAllByUserId(data.get("Robert"), PageRequest.of(0, 10))).hasSize(3);
        assertThat(this.projectRepository.findAllByUserId(data.get("Dolores"), PageRequest.of(0, 10))).hasSize(3);
        assertThat(this.projectRepository.findAllByUserId(data.get("Bernard"), PageRequest.of(0, 10))).hasSize(2);
        assertThat(this.projectRepository.findAllByUserId(data.get("Maeve"), PageRequest.of(0, 10))).hasSize(2);
        assertThat(this.projectRepository.findAllByUserId(data.get("Musashi"), PageRequest.of(0, 10))).hasSize(1);

        assertThat(this.projectRepository.findAllByUserId(data.get("Robert"), PageRequest.of(0, 2))).hasSize(2);
        assertThat(this.projectRepository.findAllByUserId(data.get("Robert"), PageRequest.of(1, 2))).hasSize(1);
    }

    @Test
    @Transactional
    public void testCountByUserId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();
        assertThat(this.projectRepository.countByUserId(data.get("Robert"))).isEqualTo(3);
        assertThat(this.projectRepository.countByUserId(data.get("Dolores"))).isEqualTo(3);
        assertThat(this.projectRepository.countByUserId(data.get("Bernard"))).isEqualTo(2);
        assertThat(this.projectRepository.countByUserId(data.get("Maeve"))).isEqualTo(2);
        assertThat(this.projectRepository.countByUserId(data.get("Musashi"))).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testExistsByUserIdAndLabel() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.existsByUserIdAndLabel(data.get("Robert"), "Westworld")).isTrue();
        assertThat(this.projectRepository.existsByUserIdAndLabel(data.get("Robert"), "Eastworld")).isTrue();
        assertThat(this.projectRepository.existsByUserIdAndLabel(data.get("Robert"), "Northworld")).isFalse();
        assertThat(this.projectRepository.existsByUserIdAndLabel(data.get("Robert"), "Southworld")).isFalse();
    }

    @Test
    @Transactional
    public void testExistsByUserIdAndProjectId() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.isVisibleByUserIdAndProjectId(data.get("Robert"), data.get("Westworld")))
                .isTrue();
        assertThat(this.projectRepository.isVisibleByUserIdAndProjectId(data.get("Robert"), data.get("Eastworld")))
                .isTrue();
        assertThat(this.projectRepository.isVisibleByUserIdAndProjectId(data.get("Robert"), data.get("Northworld")))
                .isFalse();
        assertThat(this.projectRepository.isVisibleByUserIdAndProjectId(data.get("Robert"), data.get("Southworld")))
                .isTrue();
    }

    @Test
    @Transactional
    public void testDeleteByUserIdAndProjectIds() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.countByUserId(data.get("Robert"))).isEqualTo(3);

        this.projectRepository.deleteByUserIdAndProjectIds(data.get("Robert"),
                List.of(data.get("Westworld"), data.get("Eastworld")));

        assertThat(this.projectRepository.countByUserId(data.get("Robert"))).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testOwnsAllByIds() {
        Map<String, UUID> data = new TestSetupService(this.accountRepository, this.descriptionRepository,
                this.projectRepository, this.assessmentRepository).setup();

        assertThat(this.projectRepository.ownsAllByIds(data.get("Robert"),
                List.of(data.get("Westworld"), data.get("Eastworld")))).isTrue();
        assertThat(this.projectRepository.ownsAllByIds(data.get("Robert"),
                List.of(data.get("Westworld"), data.get("Eastworld"), data.get("Southworld")))).isFalse();
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

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

import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.Account;
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.PostgreSQLTestContainer;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public class ServiceIntegrationTests {
    @Container
    private static PostgreSQLTestContainer postgreSQLContainer = new PostgreSQLTestContainer();

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private AccountCreationService accountCreationService;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private ProjectCreationService projectCreationService;

    @Autowired
    private ProjectSearchService projectSearchService;

    @Test
    @Transactional
    public void testFirstScenario() {
        Optional<Account> optionalAccount = this.accountCreationService.createAccount("Robert", "password");
        assertThat(optionalAccount).isPresent();

        Account accountCreated = optionalAccount.get();
        UUID userId = accountCreated.getId();

        Optional<Account> optionalAccountFound = this.accountSearchService.findById(userId);
        assertThat(optionalAccountFound).isPresent();

        Account accountFound = optionalAccountFound.get();
        assertThat(accountFound.getId()).isEqualTo(accountCreated.getId());

        assertThat(this.projectSearchService.count(userId)).isEqualTo(0);
        IPayload projectCreatedPayload = this.projectCreationService.createProject(userId,
                new CreateProjectInput("Westworld"));
        assertThat(projectCreatedPayload).isInstanceOf(CreateProjectSuccessPayload.class);

        CreateProjectSuccessPayload createProjectSuccessPayload = (CreateProjectSuccessPayload) projectCreatedPayload;
        Project projectCreated = createProjectSuccessPayload.getProject();

        Optional<Project> optionalProjectFound = this.projectSearchService.findById(userId, projectCreated.getId());
        assertThat(optionalProjectFound).isPresent();

        assertThat(this.projectSearchService.count(userId)).isEqualTo(1);
    }
}

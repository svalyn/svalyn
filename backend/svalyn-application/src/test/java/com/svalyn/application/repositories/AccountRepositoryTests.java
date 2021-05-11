/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.services.IntegrationTestConfiguration;

@Testcontainers
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
public class AccountRepositoryTests {
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

    @Test
    @Transactional
    public void testAccountCreationAndRetrieval() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUsername("username");
        accountEntity.setPassword("password");

        assertThat(this.accountRepository.count()).isZero();

        AccountEntity savedAccountEntity = this.accountRepository.save(accountEntity);
        assertThat(this.accountRepository.count()).isEqualTo(1);
        assertThat(savedAccountEntity.getId()).isNotNull();
    }
}

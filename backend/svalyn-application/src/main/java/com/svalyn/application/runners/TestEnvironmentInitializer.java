/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.runners;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.svalyn.application.services.AccountCreationService;

@Component
public class TestEnvironmentInitializer implements CommandLineRunner {

    private final Environment environment;

    private final AccountCreationService accountCreationService;

    public TestEnvironmentInitializer(Environment environment, AccountCreationService accountCreationService) {
        this.environment = Objects.requireNonNull(environment);
        this.accountCreationService = Objects.requireNonNull(accountCreationService);
    }

    @Override
    public void run(String... args) throws Exception {
        boolean isTest = Arrays.asList(this.environment.getActiveProfiles()).contains("test");
        if (isTest) {
            this.accountCreationService.createAccount("user", "0123456789");
            this.accountCreationService.createAccount("user1", "0123456789");
            this.accountCreationService.createAccount("user2", "0123456789");
        }
    }

}

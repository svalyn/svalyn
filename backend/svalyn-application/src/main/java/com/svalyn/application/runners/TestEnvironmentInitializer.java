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

import com.svalyn.application.services.AccountService;

@Component
public class TestEnvironmentInitializer implements CommandLineRunner {

    private final Environment environment;

    private final AccountService accountService;

    public TestEnvironmentInitializer(Environment environment, AccountService accountService) {
        this.environment = Objects.requireNonNull(environment);
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public void run(String... args) throws Exception {
        boolean isTest = Arrays.asList(this.environment.getActiveProfiles()).contains("test");
        if (isTest) {
            this.accountService.createAccount("user", "0123456789").block();
        }
    }

}

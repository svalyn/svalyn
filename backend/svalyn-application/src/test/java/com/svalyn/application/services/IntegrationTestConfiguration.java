/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = { "com.svalyn.application.entities" })
@EnableJpaRepositories(basePackages = { "com.svalyn.application.repositories" })
@ComponentScan(basePackages = { "com.svalyn.application.services", "com.svalyn.application.graphql" })
public class IntegrationTestConfiguration {
    // Nothing on purpose
}

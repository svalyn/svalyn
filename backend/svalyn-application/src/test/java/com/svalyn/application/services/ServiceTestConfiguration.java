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

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = { "com.svalyn.application.entities" })
@ComponentScan(basePackages = { "com.svalyn.application.services", "com.svalyn.application.repositories",
        "com.svalyn.application.entities" })
public class ServiceTestConfiguration {
    // Nothing on purpose
}

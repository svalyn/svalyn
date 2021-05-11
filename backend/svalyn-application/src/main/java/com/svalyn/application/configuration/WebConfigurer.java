/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    private final Environment environment;

    public WebConfigurer(Environment environment) {
        this.environment = Objects.requireNonNull(environment);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        boolean devProfileActive = Arrays.asList(this.environment.getActiveProfiles()).contains("dev");
        if (devProfileActive) {
            // @formatter:off
            registry.addMapping("/graphql")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST");
            // @formatter:on
        }
    }
}
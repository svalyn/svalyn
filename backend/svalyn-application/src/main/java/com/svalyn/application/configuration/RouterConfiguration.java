/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import static org.springframework.web.servlet.function.RequestPredicates.POST;
import static org.springframework.web.servlet.function.RouterFunctions.resources;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import com.svalyn.application.handlers.GraphQLHandlerFunction;
import com.svalyn.application.handlers.NewAccountHandlerFunction;

@Configuration
public class RouterConfiguration {
    @Bean
    public RouterFunction<ServerResponse> router(Environment environment,
            NewAccountHandlerFunction newAccountHandlerFunction, GraphQLHandlerFunction graphQLHandlerFunction) {
        var staticResources = resources("/**", new ClassPathResource("static/")); //$NON-NLS-1$ //$NON-NLS-2$
        var newAccount = route(POST("/new/account"), newAccountHandlerFunction); //$NON-NLS-1$
        var api = route(POST("/api/graphql"), graphQLHandlerFunction); //$NON-NLS-1$
        var routerFunction = staticResources.and(newAccount).and(api);

        boolean isDevProfileActive = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        if (!isDevProfileActive) {
            var redirectToFrontend = resources(req -> Optional.of(new ClassPathResource("static/index.html")));
            routerFunction = routerFunction.and(redirectToFrontend);
        }

        return routerFunction;
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.svalyn.application.handlers.GraphQLHandlerFunction;
import com.svalyn.application.handlers.NewAccountHandlerFunction;

import reactor.core.publisher.Mono;

@Configuration
public class RouterConfiguration {
    @Bean
    public RouterFunction<ServerResponse> router(NewAccountHandlerFunction newAccountHandlerFunction,
            GraphQLHandlerFunction graphQLHandlerFunction) {
        var staticResources = resources("/**", new ClassPathResource("static/")); //$NON-NLS-1$ //$NON-NLS-2$
        var newAccount = route(POST("/new/account"), newAccountHandlerFunction); //$NON-NLS-1$
        var api = route(POST("/api/graphql"), graphQLHandlerFunction); //$NON-NLS-1$
        var redirectToFrontend = resources(req -> Mono.just(new ClassPathResource("static/index.html"))); //$NON-NLS-1$

        return staticResources.and(newAccount).and(api).and(redirectToFrontend);
    }
}

/**************************************************************
* Copyright (c) Stéphane Bégaudeau
*
* This source code is licensed under the MIT license found in
* the LICENSE file in the root directory of this source tree.
***************************************************************/
package com.svalyn.application.configuration;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class RouterConfiguration {
	@Bean
	public RouterFunction<ServerResponse> router() {
		var staticResources = resources("/**", new ClassPathResource("static/")); //$NON-NLS-1$ //$NON-NLS-2$
		var api = route(GET("/api/graphql"), req -> ok().bodyValue("API")); //$NON-NLS-1$ //$NON-NLS-2$
		var redirectToFrontend = resources(req -> Mono.just(new ClassPathResource("static/index.html"))); //$NON-NLS-1$

		return staticResources.and(api).and(redirectToFrontend);
	}
}

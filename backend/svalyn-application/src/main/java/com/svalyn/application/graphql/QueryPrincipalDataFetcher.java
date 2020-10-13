/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.services.UserDetails;
import com.svalyn.application.services.UserDetailsService;

import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Mono;

@Service
public class QueryPrincipalDataFetcher implements DataFetcher<CompletableFuture<UserDetails>> {

    private final UserDetailsService userDetailsService;

    public QueryPrincipalDataFetcher(UserDetailsService userDetailsService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
    }

    @Override
    public CompletableFuture<UserDetails> get(DataFetchingEnvironment environment) throws Exception {
        GraphQLContext context = environment.getContext();

        return Mono.justOrEmpty(this.userDetailsService.getUserDetails(context)).toFuture();
    }

}

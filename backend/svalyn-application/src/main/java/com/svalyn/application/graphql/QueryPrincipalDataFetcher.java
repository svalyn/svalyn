/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import reactor.core.publisher.Mono;

@Service
public class QueryPrincipalDataFetcher implements DataFetcher<CompletableFuture<Object>> {

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) throws Exception {
        GraphQLContext context = environment.getContext();
        Object principal = context.get("principal");
        return Mono.just(principal).toFuture();
    }

}

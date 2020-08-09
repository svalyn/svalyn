/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.handlers;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.svalyn.application.configuration.GraphQLPayload;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLContext;
import reactor.core.publisher.Mono;

@Service
public class GraphQLHandlerFunction implements HandlerFunction<ServerResponse> {

    private final Logger logger = LoggerFactory.getLogger(GraphQLHandlerFunction.class);

    private final GraphQL graphQL;

    public GraphQLHandlerFunction(GraphQL graphQL) {
        this.graphQL = Objects.requireNonNull(graphQL);
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        // @formatter:off
        var authentication = request.principal()
                .filter(UsernamePasswordAuthenticationToken.class::isInstance)
                .map(UsernamePasswordAuthenticationToken.class::cast);

        var payload = request.bodyToMono(GraphQLPayload.class);

        return Mono.zip(authentication, payload, this::payloadToExecutionInput)
                      .map(this.graphQL::execute)
                      .flatMap(this::executionResultToServerResponse);
        // @formatter:on
    }

    private ExecutionInput payloadToExecutionInput(Authentication authentication, GraphQLPayload graphQLPayload) {
        var variables = Optional.ofNullable(graphQLPayload.getVariables()).orElse(Map.of());

        // @formatter:off
        GraphQLContext context = GraphQLContext.newContext()
                .of("principal", authentication.getPrincipal())
                .build();

        return ExecutionInput.newExecutionInput()
                .query(graphQLPayload.getQuery())
                .variables(variables)
                .operationName(graphQLPayload.getOperationName())
                .context(context)
                .build();
        // @formatter:on
    }

    private Mono<ServerResponse> executionResultToServerResponse(ExecutionResult executionResult) {
        executionResult.getErrors().stream().map(Object::toString).forEach(this.logger::warn);

        return ok().bodyValue(executionResult.toSpecification());
    }
}

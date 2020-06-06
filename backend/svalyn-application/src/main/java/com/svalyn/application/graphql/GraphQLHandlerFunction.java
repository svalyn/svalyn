/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.svalyn.application.configuration.GraphQLPayload;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
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
        return request.bodyToMono(GraphQLPayload.class)
                      .map(this::payloadToExecutionInput)
                      .map(this.graphQL::execute)
                      .flatMap(this::executionResultToServerResponse);
        // @formatter:on
    }

    private ExecutionInput payloadToExecutionInput(GraphQLPayload graphQLPayload) {
        var variables = Optional.ofNullable(graphQLPayload.getVariables()).orElse(Map.of());

        // @formatter:off
        return ExecutionInput.newExecutionInput()
                .query(graphQLPayload.getQuery())
                .variables(variables)
                .operationName(graphQLPayload.getOperationName())
                .build();
        // @formatter:on
    }

    private Mono<ServerResponse> executionResultToServerResponse(ExecutionResult executionResult) {
        executionResult.getErrors().stream().map(Object::toString).forEach(this.logger::warn);

        return ok().bodyValue(executionResult.toSpecification());
    }
}

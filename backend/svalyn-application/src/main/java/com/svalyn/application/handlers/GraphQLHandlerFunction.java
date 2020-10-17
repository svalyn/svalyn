/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.handlers;

import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.svalyn.application.configuration.GraphQLPayload;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLContext;

@Service
public class GraphQLHandlerFunction implements HandlerFunction<ServerResponse> {

    private final Logger logger = LoggerFactory.getLogger(GraphQLHandlerFunction.class);

    private final GraphQL graphQL;

    public GraphQLHandlerFunction(GraphQL graphQL) {
        this.graphQL = Objects.requireNonNull(graphQL);
    }

    @Override
    public ServerResponse handle(ServerRequest request) {
        // @formatter:off
        var optionalAuthentication = request.principal()
                .filter(UsernamePasswordAuthenticationToken.class::isInstance)
                .map(UsernamePasswordAuthenticationToken.class::cast);

        var optionalGraphQLPayload = this.getGraphQLPayload(request);
        var optionalExecutionInput = optionalAuthentication.flatMap(authentication -> {
            return optionalGraphQLPayload.map(graphQLPayload -> this.payloadToExecutionInput(authentication, graphQLPayload));
        });

        return optionalExecutionInput.map(this.graphQL::execute)
                .map(this::executionResultToServerResponse)
                .orElse(null);
        // @formatter:on
    }

    private Optional<GraphQLPayload> getGraphQLPayload(ServerRequest request) {
        Optional<GraphQLPayload> optionalGraphQLPayload = Optional.empty();

        try {
            optionalGraphQLPayload = Optional.of(request.body(GraphQLPayload.class));
        } catch (ServletException | IOException exception) {
            this.logger.warn(exception.getMessage(), exception);
        }

        return optionalGraphQLPayload;
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

    private ServerResponse executionResultToServerResponse(ExecutionResult executionResult) {
        executionResult.getErrors().stream().map(Object::toString).forEach(this.logger::warn);

        return ok().body(executionResult.toSpecification());
    }
}

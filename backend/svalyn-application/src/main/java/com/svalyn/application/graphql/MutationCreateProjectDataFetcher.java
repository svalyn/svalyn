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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationCreateProjectDataFetcher implements DataFetcher<CompletableFuture<IPayload>> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final ProjectService projectService;

    public MutationCreateProjectDataFetcher(ObjectMapper objectMapper, ProjectService projectService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.projectService = Objects.requireNonNull(projectService);
    }

    @Override
    public CompletableFuture<IPayload> get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), CreateProjectInput.class);
        return this.projectService.createProject(input).toFuture();
    }

}

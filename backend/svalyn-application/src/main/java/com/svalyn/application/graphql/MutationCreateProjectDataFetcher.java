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
import com.svalyn.application.dto.output.CreateProjectSuccessPayload;
import com.svalyn.application.repositories.ProjectRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationCreateProjectDataFetcher implements DataFetcher<CompletableFuture<Object>> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final ProjectRepository projectRepository;

    public MutationCreateProjectDataFetcher(ObjectMapper objectMapper, ProjectRepository projectRepository) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), CreateProjectInput.class);
        var project = this.projectRepository.createProject(input.getLabel());
        var future = project.map(CreateProjectSuccessPayload::new).toFuture();
        return CompletableFuture.anyOf(future);
    }

}

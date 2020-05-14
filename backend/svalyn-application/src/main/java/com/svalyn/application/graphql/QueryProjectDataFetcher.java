/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.ProjectRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class QueryProjectDataFetcher implements DataFetcher<CompletableFuture<Project>> {

    private static final String PROJECT_ID = "projectId"; //$NON-NLS-1$

    private final ProjectRepository projectRepository;

    public QueryProjectDataFetcher(ProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    @Override
    public CompletableFuture<Project> get(DataFetchingEnvironment environment) throws Exception {
        UUID projectId = UUID.fromString(environment.getArgument(PROJECT_ID));
        return this.projectRepository.findById(projectId).toFuture();
    }

}

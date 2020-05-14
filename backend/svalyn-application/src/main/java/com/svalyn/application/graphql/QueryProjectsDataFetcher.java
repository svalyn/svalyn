/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.repositories.ProjectRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class QueryProjectsDataFetcher implements DataFetcher<CompletableFuture<List<Project>>> {

    private final ProjectRepository projectRepository;

    public QueryProjectsDataFetcher(ProjectRepository projectRepository) {
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    @Override
    public CompletableFuture<List<Project>> get(DataFetchingEnvironment environment) throws Exception {
        return this.projectRepository.findAll().collectList().toFuture();
    }

}

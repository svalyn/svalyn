/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.ProjectSearchService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class QueryProjectDataFetcher implements DataFetcher<Project> {

    private static final String PROJECT_ID = "projectId"; //$NON-NLS-1$

    private final ProjectSearchService projectSearchService;

    public QueryProjectDataFetcher(ProjectSearchService projectSearchService) {
        this.projectSearchService = Objects.requireNonNull(projectSearchService);
    }

    @Override
    public Project get(DataFetchingEnvironment environment) throws Exception {
        UUID projectId = UUID.fromString(environment.getArgument(PROJECT_ID));
        return this.projectSearchService.findById(projectId).orElse(null);
    }

}

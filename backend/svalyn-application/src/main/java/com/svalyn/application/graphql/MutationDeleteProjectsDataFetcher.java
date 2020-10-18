/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.DeleteProjectsInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectDeletionService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationDeleteProjectsDataFetcher implements DataFetcher<IPayload> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final ProjectDeletionService projectDeletionService;

    public MutationDeleteProjectsDataFetcher(ObjectMapper objectMapper, ProjectDeletionService projectDeletionService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.projectDeletionService = Objects.requireNonNull(projectDeletionService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), DeleteProjectsInput.class);
        return this.projectDeletionService.deleteProjects(input);
    }

}

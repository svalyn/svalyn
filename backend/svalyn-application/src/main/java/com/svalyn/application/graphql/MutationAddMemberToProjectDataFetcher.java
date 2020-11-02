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
import com.svalyn.application.dto.input.AddMemberToProjectInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectMembershipUpdateService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationAddMemberToProjectDataFetcher implements DataFetcher<IPayload> {
    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final ProjectMembershipUpdateService projectMembershipService;

    public MutationAddMemberToProjectDataFetcher(ObjectMapper objectMapper,
            ProjectMembershipUpdateService projectMembershipUpdateService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.projectMembershipService = Objects.requireNonNull(projectMembershipUpdateService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), AddMemberToProjectInput.class);
        return this.projectMembershipService.addMember(input);
    }
}

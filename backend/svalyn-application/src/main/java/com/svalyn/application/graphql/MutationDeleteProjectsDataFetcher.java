/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.input.DeleteProjectsInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectDeletionService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationDeleteProjectsDataFetcher {

    private final UserDetailsService userDetailsService;

    private final ProjectDeletionService projectDeletionService;

    public MutationDeleteProjectsDataFetcher(UserDetailsService userDetailsService,
            ProjectDeletionService projectDeletionService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.projectDeletionService = Objects.requireNonNull(projectDeletionService);
    }

    @DgsData(parentType = "Mutation", field = "deleteProjects")
    public IPayload get(@InputArgument("input") DeleteProjectsInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.projectDeletionService.deleteProjects(userDetails, input);
    }

}

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
import com.svalyn.application.dto.input.CreateProjectInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectCreationService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationCreateProjectDataFetcher {

    private final UserDetailsService userDetailsService;

    private final ProjectCreationService projectCreationService;

    public MutationCreateProjectDataFetcher(UserDetailsService userDetailsService,
            ProjectCreationService projectCreationService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.projectCreationService = Objects.requireNonNull(projectCreationService);
    }

    @DgsData(parentType = "Mutation", field = "createProject")
    public IPayload get(@InputArgument("input") CreateProjectInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.projectCreationService.createProject(userDetails.getId(), input);
    }

}

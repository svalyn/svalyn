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
import com.svalyn.application.dto.input.LeaveProjectInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.ProjectMembershipUpdateService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationLeaveProjectDataFetcher {

    private final UserDetailsService userDetailsService;

    private final ProjectMembershipUpdateService projectMembershipUpdateService;

    public MutationLeaveProjectDataFetcher(UserDetailsService userDetailsService,
            ProjectMembershipUpdateService projectMembershipUpdateService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.projectMembershipUpdateService = Objects.requireNonNull(projectMembershipUpdateService);
    }

    @DgsData(parentType = "Mutation", field = "leaveProject")
    public IPayload get(@InputArgument("input") LeaveProjectInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.projectMembershipUpdateService.leaveProject(userDetails, input);
    }

}

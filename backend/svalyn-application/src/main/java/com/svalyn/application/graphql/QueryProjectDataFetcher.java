/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.UUID;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.ProjectSearchService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class QueryProjectDataFetcher {

    private final UserDetailsService userDetailsService;

    private final ProjectSearchService projectSearchService;

    public QueryProjectDataFetcher(UserDetailsService userDetailsService, ProjectSearchService projectSearchService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.projectSearchService = Objects.requireNonNull(projectSearchService);
    }

    @DgsData(parentType = "Query", field = "project")
    public Project get(@InputArgument("projectId") UUID projectId) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.projectSearchService.findById(userDetails.getId(), projectId).orElse(null);
    }

}

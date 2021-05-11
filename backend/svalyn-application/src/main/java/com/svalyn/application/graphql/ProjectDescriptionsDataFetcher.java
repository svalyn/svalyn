/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.svalyn.application.dto.output.Description;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.DescriptionSearchService;

@DgsComponent
public class ProjectDescriptionsDataFetcher {

    private final DescriptionSearchService descriptionSearchService;

    public ProjectDescriptionsDataFetcher(DescriptionSearchService descriptionSearchService) {
        this.descriptionSearchService = Objects.requireNonNull(descriptionSearchService);
    }

    @DgsData(parentType = "Project", field = "descriptions")
    public List<Description> get(DgsDataFetchingEnvironment environment) {
        Project project = environment.getSource();
        return this.descriptionSearchService.getDescriptions(project.getId());
    }

}

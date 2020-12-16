/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Description;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.DescriptionSearchService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectDescriptionsDataFetcher implements DataFetcher<List<Description>> {

    private final DescriptionSearchService descriptionSearchService;

    public ProjectDescriptionsDataFetcher(DescriptionSearchService descriptionSearchService) {
        this.descriptionSearchService = Objects.requireNonNull(descriptionSearchService);
    }

    @Override
    public List<Description> get(DataFetchingEnvironment environment) throws Exception {
        Project project = environment.getSource();
        return this.descriptionSearchService.getDescriptions(project.getId());
    }

}

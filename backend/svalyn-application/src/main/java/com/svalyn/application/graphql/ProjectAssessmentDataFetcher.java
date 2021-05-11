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
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.AssessmentSearchService;

@DgsComponent
public class ProjectAssessmentDataFetcher {

    private final AssessmentSearchService assessmentSearchService;

    public ProjectAssessmentDataFetcher(AssessmentSearchService assessmentSearchService) {
        this.assessmentSearchService = Objects.requireNonNull(assessmentSearchService);
    }

    @DgsData(parentType = "Project", field = "assessment")
    public Assessment get(DgsDataFetchingEnvironment environment, @InputArgument("assessmentId") UUID assessmentId) {
        Project project = environment.getSource();
        return this.assessmentSearchService.findById(project.getId(), assessmentId).orElse(null);
    }

}

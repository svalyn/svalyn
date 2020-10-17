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

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.services.AssessmentService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectAssessmentDataFetcher implements DataFetcher<Assessment> {

    private static final String ASSESSMENT_ID = "assessmentId"; //$NON-NLS-1$

    private final AssessmentService assessmentService;

    public ProjectAssessmentDataFetcher(AssessmentService assessmentService) {
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public Assessment get(DataFetchingEnvironment environment) throws Exception {
        UUID assessmentId = UUID.fromString(environment.getArgument(ASSESSMENT_ID));
        return this.assessmentService.findById(assessmentId).orElse(null);
    }

}

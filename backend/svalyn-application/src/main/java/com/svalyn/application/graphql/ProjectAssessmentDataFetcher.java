/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.repositories.AssessmentRepository;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectAssessmentDataFetcher implements DataFetcher<CompletableFuture<Assessment>> {

    private static final String ASSESSMENT_ID = "assessmentId"; //$NON-NLS-1$

    private final AssessmentRepository assessmentRepository;

    public ProjectAssessmentDataFetcher(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = Objects.requireNonNull(assessmentRepository);
    }

    @Override
    public CompletableFuture<Assessment> get(DataFetchingEnvironment environment) throws Exception {
        UUID assessmentId = UUID.fromString(environment.getArgument(ASSESSMENT_ID));
        return this.assessmentRepository.findById(assessmentId).toFuture();
    }

}

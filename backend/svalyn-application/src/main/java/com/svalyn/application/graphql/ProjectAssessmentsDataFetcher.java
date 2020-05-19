/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.AssessmentService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectAssessmentsDataFetcher implements DataFetcher<CompletableFuture<List<Assessment>>> {

    private final AssessmentService assessmentService;

    public ProjectAssessmentsDataFetcher(AssessmentService assessmentService) {
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public CompletableFuture<List<Assessment>> get(DataFetchingEnvironment environment) throws Exception {
        Project project = environment.getSource();
        return this.assessmentService.findByProjectId(project.getId()).collectList().toFuture();
    }

}

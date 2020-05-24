/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.CreateAssessmentInput;
import com.svalyn.application.dto.output.CreateAssessmentSuccessPayload;
import com.svalyn.application.dto.output.ErrorPayload;
import com.svalyn.application.repositories.ProjectRepository;
import com.svalyn.application.services.AssessmentService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationCreateAssessmentDataFetcher implements DataFetcher<CompletableFuture<Object>> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final ProjectRepository projectRepository;

    private final AssessmentService assessmentService;

    public MutationCreateAssessmentDataFetcher(ObjectMapper objectMapper, ProjectRepository projectRepository,
            AssessmentService assessmentService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), CreateAssessmentInput.class);
        if (this.projectRepository.existById(input.getProjectId())) {
            var assessment = this.assessmentService.createAssessment(input.getProjectId(), input.getDescriptionId(),
                    input.getLabel());
            var future = assessment.map(CreateAssessmentSuccessPayload::new).toFuture();
            return CompletableFuture.anyOf(future);
        }
        return CompletableFuture.completedFuture(new ErrorPayload("The project does not exist"));
    }

}

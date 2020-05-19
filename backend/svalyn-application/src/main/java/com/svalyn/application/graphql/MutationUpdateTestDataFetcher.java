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
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.UpdateTestSuccessPayload;
import com.svalyn.application.services.AssessmentService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationUpdateTestDataFetcher implements DataFetcher<CompletableFuture<Object>> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final AssessmentService assessmentService;

    public MutationUpdateTestDataFetcher(ObjectMapper objectMapper, AssessmentService assessmentService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), UpdateTestInput.class);
        var future = this.assessmentService.updateTest(input.getAssessmentId(), input.getTestId(), input.getStatus())
                .map(UpdateTestSuccessPayload::new).toFuture();
        return CompletableFuture.anyOf(future);
    }

}

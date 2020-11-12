/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svalyn.application.dto.input.DeleteAssessmentsInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentDeletionService;
import com.svalyn.application.services.UserDetailsService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationDeleteAssessmentsDataFetcher implements DataFetcher<IPayload> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final UserDetailsService userDetailsService;

    private final AssessmentDeletionService assessmentDeletionService;

    public MutationDeleteAssessmentsDataFetcher(ObjectMapper objectMapper, UserDetailsService userDetailsService,
            AssessmentDeletionService assessmentDeletionService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentDeletionService = Objects.requireNonNull(assessmentDeletionService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), DeleteAssessmentsInput.class);
        var userDetails = this.userDetailsService.getUserDetails(environment.getContext());
        return this.assessmentDeletionService.deleteAssessments(userDetails.getId(), input);
    }

}

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
import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentService;
import com.svalyn.application.services.UserDetailsService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationUpdateAssessmentStatusDataFetcher implements DataFetcher<IPayload> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final UserDetailsService userDetailsService;

    private final AssessmentService assessmentService;

    public MutationUpdateAssessmentStatusDataFetcher(ObjectMapper objectMapper, UserDetailsService userDetailsService,
            AssessmentService assessmentService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentService = Objects.requireNonNull(assessmentService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), UpdateAssessmentStatusInput.class);

        var userDetails = this.userDetailsService.getUserDetails(environment.getContext());
        return this.assessmentService.updateAssessmentStatus(userDetails.getId(), input);
    }

}

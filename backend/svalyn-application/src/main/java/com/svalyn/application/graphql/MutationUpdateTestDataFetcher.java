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
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentUpdateService;
import com.svalyn.application.services.UserDetailsService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class MutationUpdateTestDataFetcher implements DataFetcher<IPayload> {

    private static final String INPUT = "input";

    private final ObjectMapper objectMapper;

    private final UserDetailsService userDetailsService;

    private final AssessmentUpdateService assessmentUpdateService;

    public MutationUpdateTestDataFetcher(ObjectMapper objectMapper, UserDetailsService userDetailsService,
            AssessmentUpdateService assessmentUpdateService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentUpdateService = Objects.requireNonNull(assessmentUpdateService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.objectMapper.convertValue(environment.getArgument(INPUT), UpdateTestInput.class);

        var userDetails = this.userDetailsService.getUserDetails(environment.getContext());
        return this.assessmentUpdateService.updateTest(userDetails.getId(), input);
    }

}

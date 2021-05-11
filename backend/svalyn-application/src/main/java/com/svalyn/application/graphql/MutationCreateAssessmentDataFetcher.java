/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import com.svalyn.application.dto.input.CreateAssessmentInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentCreationService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationCreateAssessmentDataFetcher {

    private final UserDetailsService userDetailsService;

    private final AssessmentCreationService assessmentCreationService;

    public MutationCreateAssessmentDataFetcher(UserDetailsService userDetailsService,
            AssessmentCreationService assessmentCreationService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentCreationService = Objects.requireNonNull(assessmentCreationService);
    }

    @DgsData(parentType = "Mutation", field = "createAssessment")
    public IPayload get(@InputArgument("input") CreateAssessmentInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.assessmentCreationService.createAssessment(userDetails.getId(), input);
    }

}

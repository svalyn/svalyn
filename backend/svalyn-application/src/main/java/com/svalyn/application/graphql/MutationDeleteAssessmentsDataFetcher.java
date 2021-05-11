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
import com.svalyn.application.dto.input.DeleteAssessmentsInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentDeletionService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationDeleteAssessmentsDataFetcher {

    private final UserDetailsService userDetailsService;

    private final AssessmentDeletionService assessmentDeletionService;

    public MutationDeleteAssessmentsDataFetcher(UserDetailsService userDetailsService,
            AssessmentDeletionService assessmentDeletionService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentDeletionService = Objects.requireNonNull(assessmentDeletionService);
    }

    @DgsData(parentType = "Mutation", field = "deleteAssessments")
    public IPayload get(@InputArgument("input") DeleteAssessmentsInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.assessmentDeletionService.deleteAssessments(userDetails.getId(), input);
    }

}

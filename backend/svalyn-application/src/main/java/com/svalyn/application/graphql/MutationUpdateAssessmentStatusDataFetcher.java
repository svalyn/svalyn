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
import com.svalyn.application.dto.input.UpdateAssessmentStatusInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentUpdateService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationUpdateAssessmentStatusDataFetcher {

    private final UserDetailsService userDetailsService;

    private final AssessmentUpdateService assessmentUpdateService;

    public MutationUpdateAssessmentStatusDataFetcher(UserDetailsService userDetailsService,
            AssessmentUpdateService assessmentUpdateService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentUpdateService = Objects.requireNonNull(assessmentUpdateService);
    }

    @DgsData(parentType = "Mutation", field = "updateAssessmentStatus")
    public IPayload get(@InputArgument("input") UpdateAssessmentStatusInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.assessmentUpdateService.updateAssessmentStatus(userDetails.getId(), input);
    }

}

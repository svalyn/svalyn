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
import com.svalyn.application.dto.input.UpdateTestInput;
import com.svalyn.application.dto.output.IPayload;
import com.svalyn.application.services.AssessmentUpdateService;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class MutationUpdateTestDataFetcher {

    private final UserDetailsService userDetailsService;

    private final AssessmentUpdateService assessmentUpdateService;

    public MutationUpdateTestDataFetcher(UserDetailsService userDetailsService,
            AssessmentUpdateService assessmentUpdateService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
        this.assessmentUpdateService = Objects.requireNonNull(assessmentUpdateService);
    }

    @DgsData(parentType = "Mutation", field = "updateTest")
    public IPayload get(@InputArgument("input") UpdateTestInput input) {
        var userDetails = this.userDetailsService.getUserDetails();
        return this.assessmentUpdateService.updateTest(userDetails.getId(), input);
    }

}

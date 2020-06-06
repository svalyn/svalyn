/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;

public class UpdateAssessmentStatusSuccessPayload {
    private final Assessment assessment;

    public UpdateAssessmentStatusSuccessPayload(Assessment assessment) {
        this.assessment = Objects.requireNonNull(assessment);
    }

    public Assessment getAssessment() {
        return this.assessment;
    }
}
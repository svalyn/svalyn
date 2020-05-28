/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.UUID;

import com.svalyn.application.dto.output.AssessmentStatus;

public class UpdateAssessmentStatusInput {

    private UUID assessmentId;

    private AssessmentStatus status;

    public UUID getAssessmentId() {
        return this.assessmentId;
    }

    public AssessmentStatus getStatus() {
        return this.status;
    }
}

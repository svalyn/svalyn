/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.UUID;

import com.svalyn.application.dto.output.TestStatus;

public class UpdateTestInput {
    private UUID assessmentId;

    private UUID testId;

    private TestStatus status;

    public UUID getAssessmentId() {
        return this.assessmentId;
    }

    public UUID getTestId() {
        return this.testId;
    }

    public TestStatus getStatus() {
        return this.status;
    }
}

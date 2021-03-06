/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.UUID;

public class CreateAssessmentInput {
    private UUID projectId;

    private UUID descriptionId;

    private String label;

    public UUID getProjectId() {
        return this.projectId;
    }

    public UUID getDescriptionId() {
        return this.descriptionId;
    }

    public String getLabel() {
        return this.label;
    }
}

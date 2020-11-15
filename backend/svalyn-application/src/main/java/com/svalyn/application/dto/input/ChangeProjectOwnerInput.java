/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.UUID;

public class ChangeProjectOwnerInput {
    private UUID projectId;

    private UUID newOwnerId;

    public UUID getProjectId() {
        return this.projectId;
    }

    public UUID getNewOwnerId() {
        return this.newOwnerId;
    }
}

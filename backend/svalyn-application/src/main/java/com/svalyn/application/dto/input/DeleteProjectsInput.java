/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.List;
import java.util.UUID;

public class DeleteProjectsInput {
    private List<UUID> projectIds;

    public List<UUID> getProjectIds() {
        return this.projectIds;
    }
}

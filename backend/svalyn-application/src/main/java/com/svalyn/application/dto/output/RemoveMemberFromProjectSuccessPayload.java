/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;

public class RemoveMemberFromProjectSuccessPayload implements IPayload {
    private final Project project;

    public RemoveMemberFromProjectSuccessPayload(Project project) {
        this.project = Objects.requireNonNull(project);
    }

    public Project getProject() {
        return this.project;
    }
}

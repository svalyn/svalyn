/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;
import java.util.UUID;

public class Project {
    private final UUID id;

    private final String label;

    public Project(UUID id, String label) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

}

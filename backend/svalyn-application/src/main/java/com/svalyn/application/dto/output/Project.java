/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Project {
    private final UUID id;

    private final String label;

    private final UUID createdBy;

    private final LocalDateTime createdOn;

    public Project(UUID id, String label, UUID createdBy, LocalDateTime createdOn) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.createdBy = Objects.requireNonNull(createdBy);
        this.createdOn = Objects.requireNonNull(createdOn);
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public UUID getCreatedBy() {
        return this.createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

}

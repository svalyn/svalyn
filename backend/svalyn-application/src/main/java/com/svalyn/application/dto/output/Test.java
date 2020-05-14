/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;
import java.util.UUID;

public class Test {

    private final UUID id;

    private final String label;

    private final String description;

    private final Status status;

    public Test(UUID id, String label, String description, Status status) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.description = Objects.requireNonNull(description);
        this.status = status;
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }

    public Status getStatus() {
        return this.status;
    }

}

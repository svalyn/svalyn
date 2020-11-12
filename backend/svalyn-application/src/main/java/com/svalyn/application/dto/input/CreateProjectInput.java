/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.Objects;

public class CreateProjectInput {
    private String label;

    public CreateProjectInput() {
        // Used by Jackson
    }

    public CreateProjectInput(String label) {
        this.label = Objects.requireNonNull(label);
    }

    public String getLabel() {
        return this.label;
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Test {

    private final UUID id;

    private final String label;

    private final String details;

    private final List<String> steps;

    private final TestStatus status;

    public Test(UUID id, String label, String details, List<String> steps, TestStatus status) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.details = Objects.requireNonNull(details);
        this.steps = steps;
        this.status = status;
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getDetails() {
        return this.details;
    }

    public List<String> getSteps() {
        return this.steps;
    }

    public TestStatus getStatus() {
        return this.status;
    }

}

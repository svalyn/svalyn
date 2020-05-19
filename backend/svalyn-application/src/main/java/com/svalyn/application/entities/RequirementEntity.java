/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.util.List;
import java.util.UUID;

public class RequirementEntity {
    private UUID id;

    private String label;

    private String description;

    private List<TestEntity> tests;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TestEntity> getTests() {
        return this.tests;
    }

    public void setTests(List<TestEntity> tests) {
        this.tests = tests;
    }
}

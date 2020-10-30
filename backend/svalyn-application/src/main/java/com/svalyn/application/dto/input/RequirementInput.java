/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.List;

public class RequirementInput {
    private String label;

    private String details;

    private List<TestInput> tests;

    public String getLabel() {
        return this.label;
    }

    public String getDetails() {
        return this.details;
    }

    public List<TestInput> getTests() {
        return this.tests;
    }
}

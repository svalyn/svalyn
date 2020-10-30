/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.List;

public class CategoryInput {
    private String label;

    private String details;

    private List<RequirementInput> requirements;

    public String getLabel() {
        return this.label;
    }

    public String getDetails() {
        return this.details;
    }

    public List<RequirementInput> getRequirements() {
        return this.requirements;
    }
}

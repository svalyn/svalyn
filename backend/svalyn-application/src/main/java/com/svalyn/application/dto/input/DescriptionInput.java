/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.input;

import java.util.List;

public class DescriptionInput {
    private String label;

    private List<CategoryInput> categories;

    public String getLabel() {
        return this.label;
    }

    public List<CategoryInput> getCategories() {
        return this.categories;
    }
}

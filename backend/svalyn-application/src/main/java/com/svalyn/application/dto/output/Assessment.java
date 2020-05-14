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

public class Assessment {
    private final UUID id;

    private final String label;

    private final List<Category> categories;

    public Assessment(UUID id, String label, List<Category> categories) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.categories = Objects.requireNonNull(categories);
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Assessment {
    private final UUID id;

    private final String label;

    private final UUID createdBy;

    private final LocalDateTime createdOn;

    private final UUID lastModifiedBy;

    private final LocalDateTime lastModifiedOn;

    private final List<Category> categories;

    private final int success;

    private final int failure;

    private final int testCount;

    private final AssessmentStatus status;

    public Assessment(UUID id, String label, UUID createdBy, LocalDateTime createdOn, UUID lastModifiedBy,
            LocalDateTime lastModifiedOn, List<Category> categories, int success, int failure, int testCount,
            AssessmentStatus status) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.createdBy = Objects.requireNonNull(createdBy);
        this.createdOn = Objects.requireNonNull(createdOn);
        this.lastModifiedBy = Objects.requireNonNull(lastModifiedBy);
        this.lastModifiedOn = Objects.requireNonNull(lastModifiedOn);
        this.categories = Objects.requireNonNull(categories);
        this.success = success;
        this.failure = failure;
        this.testCount = testCount;
        this.status = Objects.requireNonNull(status);
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

    public UUID getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public LocalDateTime getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public int getSuccess() {
        return this.success;
    }

    public int getFailure() {
        return this.failure;
    }

    public int getTestCount() {
        return this.testCount;
    }

    public AssessmentStatus getStatus() {
        return this.status;
    }

}

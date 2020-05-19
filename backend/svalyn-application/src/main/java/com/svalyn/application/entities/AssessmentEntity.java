/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.util.Map;
import java.util.UUID;

public class AssessmentEntity {
    private UUID id;

    private UUID descriptionId;

    private UUID projectId;

    private String label;

    private Map<UUID, StatusEntity> results;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDescriptionId() {
        return this.descriptionId;
    }

    public void setDescriptionId(UUID descriptionId) {
        this.descriptionId = descriptionId;
    }

    public UUID getProjectId() {
        return this.projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<UUID, StatusEntity> getResults() {
        return this.results;
    }

    public void setResults(Map<UUID, StatusEntity> results) {
        this.results = results;
    }
}

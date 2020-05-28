/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class AssessmentEntity {
    private UUID id;

    private UUID descriptionId;

    private UUID projectId;

    private String label;

    private Map<UUID, TestStatusEntity> results;

    private LocalDateTime createdOn;

    private LocalDateTime lastModifiedOn;

    private AssessmentStatusEntity status;

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

    public Map<UUID, TestStatusEntity> getResults() {
        return this.results;
    }

    public void setResults(Map<UUID, TestStatusEntity> results) {
        this.results = results;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public AssessmentStatusEntity getStatus() {
        return this.status;
    }

    public void setStatus(AssessmentStatusEntity status) {
        this.status = status;
    }
}

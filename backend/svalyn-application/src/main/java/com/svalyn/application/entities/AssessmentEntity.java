/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "Assessment")
@TypeDef(name = "pgsql_enum", typeClass = AssessmentStatusEntityEnumType.class)
public class AssessmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "descriptionId")
    private DescriptionEntity description;

    @ManyToOne
    @JoinColumn(name = "projectId")
    private ProjectEntity project;

    private String label;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<TestResultEntity> results;

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private AccountEntity createdBy;

    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "lastModifiedBy")
    private AccountEntity lastModifiedBy;

    private LocalDateTime lastModifiedOn;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private AssessmentStatusEntity status;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DescriptionEntity getDescription() {
        return this.description;
    }

    public void setDescription(DescriptionEntity description) {
        this.description = description;
    }

    public ProjectEntity getProject() {
        return this.project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<TestResultEntity> getResults() {
        return this.results;
    }

    public void setResults(List<TestResultEntity> results) {
        this.results = results;
    }

    public AccountEntity getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(AccountEntity createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public AccountEntity getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(AccountEntity lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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

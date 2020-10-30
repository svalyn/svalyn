/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "TestResult")
@TypeDef(name = "pgsql_enum", typeClass = TestResultStatusEntityEnumType.class)
public class TestResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assessmentId")
    private AssessmentEntity assessment;

    @ManyToOne
    @JoinColumn(name = "testId")
    private TestEntity test;

    @ManyToOne
    @JoinColumn(name = "requirementId")
    private RequirementEntity requirement;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "descriptionId")
    private DescriptionEntity description;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private TestResultStatusEntity status;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AssessmentEntity getAssessment() {
        return this.assessment;
    }

    public void setAssessment(AssessmentEntity assessment) {
        this.assessment = assessment;
    }

    public TestEntity getTest() {
        return this.test;
    }

    public void setTest(TestEntity test) {
        this.test = test;
    }

    public RequirementEntity getRequirement() {
        return this.requirement;
    }

    public void setRequirement(RequirementEntity requirement) {
        this.requirement = requirement;
    }

    public CategoryEntity getCategory() {
        return this.category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public DescriptionEntity getDescription() {
        return this.description;
    }

    public void setDescription(DescriptionEntity description) {
        this.description = description;
    }

    public TestResultStatusEntity getStatus() {
        return this.status;
    }

    public void setStatus(TestResultStatusEntity status) {
        this.status = status;
    }
}

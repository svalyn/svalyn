/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "Project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String label;

    @ManyToOne
    @JoinColumn(name = "ownedBy")
    private AccountEntity ownedBy;

    @ManyToMany
    @OrderBy("username")
    @JoinTable(name = "Project_Members", joinColumns = @JoinColumn(name = "projectId"), inverseJoinColumns = @JoinColumn(name = "accountId"))
    private List<AccountEntity> members = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "createdBy")
    private AccountEntity createdBy;

    private LocalDateTime createdOn;

    @OrderBy("label")
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<DescriptionEntity> descriptions;

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

    public AccountEntity getOwnedBy() {
        return this.ownedBy;
    }

    public void setOwnedBy(AccountEntity ownedBy) {
        this.ownedBy = ownedBy;
    }

    public List<AccountEntity> getMembers() {
        return this.members;
    }

    public void setMembers(List<AccountEntity> members) {
        this.members = members;
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

    public List<DescriptionEntity> getDescriptions() {
        return this.descriptions;
    }

    public void setDescriptions(List<DescriptionEntity> descriptions) {
        this.descriptions = descriptions;
    }

}

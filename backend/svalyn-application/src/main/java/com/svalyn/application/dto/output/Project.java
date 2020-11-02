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

public class Project {
    private final UUID id;

    private final String label;

    private final Account ownedBy;

    private final List<Account> members;

    private final Account createdBy;

    private final LocalDateTime createdOn;

    public Project(UUID id, String label, Account ownedBy, List<Account> members, Account createdBy,
            LocalDateTime createdOn) {
        this.id = Objects.requireNonNull(id);
        this.label = Objects.requireNonNull(label);
        this.ownedBy = Objects.requireNonNull(ownedBy);
        this.members = Objects.requireNonNull(members);
        this.createdBy = Objects.requireNonNull(createdBy);
        this.createdOn = Objects.requireNonNull(createdOn);
    }

    public UUID getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public Account getOwnedBy() {
        return this.ownedBy;
    }

    public List<Account> getMembers() {
        return this.members;
    }

    public Account getCreatedBy() {
        return this.createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

}

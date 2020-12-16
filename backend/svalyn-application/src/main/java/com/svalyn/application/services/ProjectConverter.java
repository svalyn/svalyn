/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Account;
import com.svalyn.application.dto.output.Project;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.entities.ProjectEntity;

@Service
public class ProjectConverter {
    public Project convert(ProjectEntity projectEntity) {
        var ownedBy = this.convert(projectEntity.getOwnedBy());
        var createdBy = this.convert(projectEntity.getCreatedBy());

        // @formatter:off
        var members = Optional.ofNullable(projectEntity.getMembers()).orElse(new ArrayList<>()).stream()
                .map(this::convert)
                .collect(Collectors.toList());
        // @formatter:on

        return new Project(projectEntity.getId(), projectEntity.getLabel(), ownedBy, members, createdBy,
                projectEntity.getCreatedOn());
    }

    private Account convert(AccountEntity accountEntity) {
        return new Account(accountEntity.getId(), accountEntity.getUsername());
    }

}

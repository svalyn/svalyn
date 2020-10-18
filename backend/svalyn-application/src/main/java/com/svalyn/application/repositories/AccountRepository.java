/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AccountEntity;

@Service
public class AccountRepository {
    private final List<AccountEntity> accountEntities = new ArrayList<>();

    public AccountEntity createAccount(String username, String password) {
        AccountEntity accountEntity = new AccountEntity(UUID.randomUUID(), username, password);
        this.accountEntities.add(accountEntity);
        return accountEntity;
    }

    public Optional<AccountEntity> findByUsername(String username) {
        // @formatter:off
        return this.accountEntities.stream()
                .filter(accountEntity -> accountEntity.getUsername().equals(username))
                .findFirst();
        // @formatter:on
    }

    public Optional<AccountEntity> findById(UUID userId) {
        // @formatter:off
        return this.accountEntities.stream()
                .filter(accountEntity -> accountEntity.getId().equals(userId))
                .findFirst();
        // @formatter:on
    }
}

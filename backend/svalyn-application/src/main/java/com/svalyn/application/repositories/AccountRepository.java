/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AccountEntity;

import reactor.core.publisher.Mono;

@Service
public class AccountRepository {
    private final List<AccountEntity> accounts = new ArrayList<>();

    public Mono<AccountEntity> createAccount(String username, String password) {
        return Mono.fromCallable(() -> {
            AccountEntity accountEntity = new AccountEntity(UUID.randomUUID(), username, password);
            this.accounts.add(accountEntity);
            return accountEntity;
        });
    }

    public Mono<AccountEntity> findByUsername(String username) {
        // @formatter:off
        var optionalAccountEntity = this.accounts.stream()
                .filter(accountEntity -> accountEntity.getUsername().equals(username))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalAccountEntity);
    }

    public Mono<AccountEntity> findById(UUID userId) {
        // @formatter:off
        var optionalAccountEntity = this.accounts.stream()
                .filter(accountEntity -> accountEntity.getId().equals(userId))
                .findFirst();
        // @formatter:on
        return Mono.justOrEmpty(optionalAccountEntity);
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.repositories.AccountRepository;

import reactor.core.publisher.Mono;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Mono<Account> createAccount(String username, String password) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode(password);

        // @formatter:off
        return this.accountRepository.findByUsername(username)
                .flatMap(accountEntity -> Mono.<AccountEntity>error(new IllegalArgumentException()))
                .switchIfEmpty(Mono.defer(() -> this.accountRepository.createAccount(username, encodedPassword)))
                .map(accountEntity -> new Account(accountEntity.getId(), accountEntity.getUsername()));
        // @formatter:on
    }

    public Mono<Account> findById(UUID userId) {
        // @formatter:off
        return this.accountRepository.findById(userId)
                .map(accountEntity -> new Account(accountEntity.getId(), accountEntity.getUsername()));
        // @formatter:on
    }
}

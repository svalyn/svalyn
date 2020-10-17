/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.repositories.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Optional<Account> createAccount(String username, String password) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode(password);

        boolean alreadyExists = this.accountRepository.findByUsername(username).isPresent();
        if (alreadyExists) {
            return Optional.empty();
        }
        AccountEntity accountEntity = this.accountRepository.createAccount(username, encodedPassword);
        return Optional.of(new Account(accountEntity.getId(), accountEntity.getUsername()));
    }

    public Optional<Account> findById(UUID userId) {
        // @formatter:off
        return this.accountRepository.findById(userId)
                .map(accountEntity -> new Account(accountEntity.getId(), accountEntity.getUsername()));
        // @formatter:on
    }
}

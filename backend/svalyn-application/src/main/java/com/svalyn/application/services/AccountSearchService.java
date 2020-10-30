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

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Account;
import com.svalyn.application.repositories.IAccountRepository;

@Service
public class AccountSearchService {

    private final IAccountRepository accountRepository;

    public AccountSearchService(IAccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Optional<Account> findById(UUID userId) {
        // @formatter:off
        return this.accountRepository.findById(userId)
                .map(accountEntity -> new Account(accountEntity.getId(), accountEntity.getUsername()));
        // @formatter:on
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Account;
import com.svalyn.application.entities.AccountEntity;
import com.svalyn.application.repositories.IAccountRepository;

@Service
public class AccountCreationService {
    private final IAccountRepository accountRepository;

    public AccountCreationService(IAccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Optional<Account> createAccount(String username, String password) {

        boolean alreadyExists = this.accountRepository.findByUsername(username).isPresent();
        if (alreadyExists) {
            return Optional.empty();
        }

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodedPassword = encoder.encode(password);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setUsername(username);
        accountEntity.setPassword(encodedPassword);

        AccountEntity savedAccountEntity = this.accountRepository.save(accountEntity);

        return Optional.of(new Account(savedAccountEntity.getId(), savedAccountEntity.getUsername()));
    }

}

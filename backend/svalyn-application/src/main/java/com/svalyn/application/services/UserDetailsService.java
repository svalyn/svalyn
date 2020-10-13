/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;

import com.svalyn.application.repositories.AccountRepository;

import graphql.GraphQLContext;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    @Override
    public Mono<org.springframework.security.core.userdetails.UserDetails> findByUsername(String username) {
        return this.accountRepository.findByUsername(username).map(accountEntity -> {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            return new UserDetails(accountEntity.getId(), accountEntity.getUsername(), accountEntity.getPassword(),
                    authorities);
        });
    }

    public Optional<UserDetails> getUserDetails(GraphQLContext context) {
        // @formatter:off
        return context.getOrEmpty("principal")
                .filter(UserDetails.class::isInstance)
                .map(UserDetails.class::cast);
        // @formatter:on
    }

}

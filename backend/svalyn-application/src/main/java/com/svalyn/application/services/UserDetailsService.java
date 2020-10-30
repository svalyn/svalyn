/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.services;

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.svalyn.application.repositories.IAccountRepository;

import graphql.GraphQLContext;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final IAccountRepository accountRepository;

    public UserDetailsService(IAccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return this.accountRepository.findByUsername(username).map(accountEntity -> {
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            return new UserDetails(accountEntity.getId(), accountEntity.getUsername(), accountEntity.getPassword(),
                    authorities);
        }).orElseThrow(() -> new UsernameNotFoundException("No account with the username " + username + " found"));
    }

    public UserDetails getUserDetails(GraphQLContext context) {
        return context.get("principal");
    }

}

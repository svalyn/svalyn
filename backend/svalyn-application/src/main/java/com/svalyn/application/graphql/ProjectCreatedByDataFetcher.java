/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Project;
import com.svalyn.application.services.Account;
import com.svalyn.application.services.AccountService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class ProjectCreatedByDataFetcher implements DataFetcher<CompletableFuture<Account>> {

    private final AccountService accountService;

    public ProjectCreatedByDataFetcher(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public CompletableFuture<Account> get(DataFetchingEnvironment environment) throws Exception {
        Project project = environment.getSource();
        return this.accountService.findById(project.getCreatedBy()).toFuture();
    }

}

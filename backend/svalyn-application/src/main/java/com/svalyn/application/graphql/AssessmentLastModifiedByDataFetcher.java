/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.services.Account;
import com.svalyn.application.services.AccountService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class AssessmentLastModifiedByDataFetcher implements DataFetcher<Account> {

    private final AccountService accountService;

    public AssessmentLastModifiedByDataFetcher(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public Account get(DataFetchingEnvironment environment) throws Exception {
        Assessment assessment = environment.getSource();
        return this.accountService.findById(assessment.getCreatedBy()).orElse(null);
    }

}

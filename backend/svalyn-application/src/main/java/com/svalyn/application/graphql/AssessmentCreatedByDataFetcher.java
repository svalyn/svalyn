/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.svalyn.application.dto.output.Account;
import com.svalyn.application.dto.output.Assessment;
import com.svalyn.application.services.AccountSearchService;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@Service
public class AssessmentCreatedByDataFetcher implements DataFetcher<Account> {

    private final AccountSearchService accountSearchService;

    public AssessmentCreatedByDataFetcher(AccountSearchService accountSearchService) {
        this.accountSearchService = Objects.requireNonNull(accountSearchService);
    }

    @Override
    public Account get(DataFetchingEnvironment environment) throws Exception {
        Assessment assessment = environment.getSource();
        return this.accountSearchService.findById(assessment.getLastModifiedBy()).orElse(null);
    }

}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.graphql;

import java.util.Objects;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.svalyn.application.services.UserDetails;
import com.svalyn.application.services.UserDetailsService;

@DgsComponent
public class QueryPrincipalDataFetcher {

    private final UserDetailsService userDetailsService;

    public QueryPrincipalDataFetcher(UserDetailsService userDetailsService) {
        this.userDetailsService = Objects.requireNonNull(userDetailsService);
    }

    @DgsData(parentType = "Query", field = "principal")
    public UserDetails get() {
        return this.userDetailsService.getUserDetails();
    }

}

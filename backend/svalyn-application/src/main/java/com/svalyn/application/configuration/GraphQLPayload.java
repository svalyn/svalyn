/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import java.util.HashMap;
import java.util.Map;

public class GraphQLPayload {
    private String query;

    private Map<String, Object> variables = new HashMap<>();

    private String operationName;

    public String getQuery() {
        return this.query;
    }

    public Map<String, Object> getVariables() {
        return this.variables;
    }

    public String getOperationName() {
        return this.operationName;
    }

}

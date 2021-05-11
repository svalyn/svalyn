/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;

import com.svalyn.application.services.UserDetails;

public class LeaveProjectSuccessPayload implements IPayload {
    private final UserDetails principal;

    public LeaveProjectSuccessPayload(UserDetails principal) {
        this.principal = Objects.requireNonNull(principal);
    }

    public UserDetails getPrincipal() {
        return this.principal;
    }
}

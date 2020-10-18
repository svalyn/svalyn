/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;
import java.util.UUID;

public class Account {
    private final UUID id;

    private final String username;

    public Account(UUID id, String username) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
    }

    public UUID getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }
}

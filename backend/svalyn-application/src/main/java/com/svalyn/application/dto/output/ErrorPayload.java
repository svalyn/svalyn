/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.dto.output;

import java.util.Objects;

public class ErrorPayload implements IPayload {
    private final String message;

    public ErrorPayload(String message) {
        this.message = Objects.requireNonNull(message);
    }

    public String getMessage() {
        return this.message;
    }
}

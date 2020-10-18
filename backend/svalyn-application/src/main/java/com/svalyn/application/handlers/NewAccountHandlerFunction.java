/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.handlers;

import static org.springframework.web.servlet.function.ServerResponse.badRequest;
import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import com.svalyn.application.services.AccountCreationService;

@Service
public class NewAccountHandlerFunction implements HandlerFunction<ServerResponse> {

    private final AccountCreationService accountCreationService;

    public NewAccountHandlerFunction(AccountCreationService accountCreationService) {
        this.accountCreationService = Objects.requireNonNull(accountCreationService);
    }

    @Override
    public ServerResponse handle(ServerRequest request) {
        String username = request.param("username").orElse("");
        String password = request.param("password").orElse("");

        if (username.length() > 0 && password.length() >= 10) {
            var optionalAccount = this.accountCreationService.createAccount(username, password);
            if (optionalAccount.isPresent()) {
                return ok().build();
            }
        }
        return badRequest().build();
    }

}

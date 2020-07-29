/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.handlers;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.svalyn.application.services.AccountService;

import reactor.core.publisher.Mono;

@Service
public class NewAccountHandlerFunction implements HandlerFunction<ServerResponse> {

    private final AccountService accountService;

    public NewAccountHandlerFunction(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService);
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.formData().flatMap((formData -> {
            String username = Optional.ofNullable(formData.getFirst("username")).orElse("");
            String password = Optional.ofNullable(formData.getFirst("password")).orElse("");

            // @formatter:off
            if (username.length() > 0 && password.length() >= 10) {
                return this.accountService.createAccount(username, password)
                        .flatMap(account -> ok().build())
                        .switchIfEmpty(badRequest().build())
                        .onErrorResume(throwable -> badRequest().build());
            }
            return badRequest().build();
            // @formatter:on
        }));
    }

}

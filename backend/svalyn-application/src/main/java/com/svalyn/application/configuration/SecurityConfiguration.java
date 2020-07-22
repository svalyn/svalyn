/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;

@EnableWebFluxSecurity
public class SecurityConfiguration {
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // @formatter:off
        var userDetails = User.withUsername("user")
                .password("0123456789")
                .passwordEncoder(encoder::encode)
                .roles("USER")
                .build();
        // @formatter:on
        return new MapReactiveUserDetailsService(userDetails);
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(Environment environment, ServerHttpSecurity http) {
        boolean isDevProfileActive = Arrays.asList(environment.getActiveProfiles()).contains("dev");
        // @formatter:off
        http
            .authorizeExchange(exchanges -> {
                exchanges.pathMatchers("/login").permitAll();
                exchanges.pathMatchers("/static/**").permitAll();
                exchanges.pathMatchers("/favicon.ico").permitAll();
                exchanges.pathMatchers("/manifest.json").permitAll();
                exchanges.pathMatchers("/logo192.png").permitAll();
                exchanges.pathMatchers("/logo512.png").permitAll();
                exchanges.pathMatchers("/api/graphql").hasAnyRole("USER");
                exchanges.anyExchange().authenticated();
            })
            .httpBasic(httpBasic -> {
                if (isDevProfileActive) {
                    httpBasic.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
                } else {
                    httpBasic.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"));
                }
            })
            .formLogin(formLogin -> {
                formLogin.loginPage("/login");
                formLogin.authenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
                if (isDevProfileActive) {
                    formLogin.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED));
                } else {
                    formLogin.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login"));
                }
            })
            .cors(CorsSpec::disable)
            .csrf(CsrfSpec::disable);
        // @formatter:on
        return http.build();
    }
}

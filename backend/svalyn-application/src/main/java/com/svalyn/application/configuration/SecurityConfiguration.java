/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 **************************************************************/
package com.svalyn.application.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
        .authorizeRequests(exchanges -> {
            exchanges.antMatchers("/login").permitAll();
            exchanges.antMatchers("/new/account").permitAll();

            exchanges.antMatchers("/static/**").permitAll();
            exchanges.antMatchers("/favicon.ico").permitAll();
            exchanges.antMatchers("/manifest.json").permitAll();
            exchanges.antMatchers("/logo192.png").permitAll();
            exchanges.antMatchers("/logo512.png").permitAll();
            exchanges.antMatchers("/api/graphql").hasAnyRole("USER");
            exchanges.anyRequest().authenticated();
        })
        .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.failureHandler(new AuthenticationEntryPointFailureHandler(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
            formLogin.successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()));
        })
        .logout(logout -> logout.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.ACCEPTED)))
        .cors(CorsConfigurer<HttpSecurity>::disable)
        .csrf(CsrfConfigurer<HttpSecurity>::disable);
        // @formatter:on
    }
}

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('New Account - /new/account', () => {
  beforeEach(() => {
    cy.visit('/new/account');
  });

  it('cannot create an account in without username and password', () => {
    cy.get('[data-testid="create-account"]').should('be.disabled');
  });

  it('can create an account in with a proper username and password', () => {
    cy.get('[data-testid="username"').type('user');
    cy.get('[data-testid="password"').type('0123456789');
    cy.get('[data-testid="create-account"]').should('not.be.disabled');
  });

  it('create an account, log the user in and redirect to the dashboard', () => {
    cy.get('[data-testid="error"]').should('not.be.visible');

    cy.get('[data-testid="username"').type('new user');
    cy.get('[data-testid="password"').type('some password');
    cy.get('[data-testid="create-account"]').click();

    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });
});

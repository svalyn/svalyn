/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Authentication', () => {
  beforeEach(() => {
    cy.login('user', '0123456789');
    cy.deleteAllProjects();
    cy.logout();
  });

  it('cannot access the dashboard without being authenticated', () => {
    cy.visit('/');
    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });

  it('can access the dashboard while being authenticated', () => {
    cy.login('user', '0123456789');

    cy.visit('/');
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  it('is redirected to the login page after logout programmatically', () => {
    cy.login('user', '0123456789');

    cy.visit('/');
    cy.url().should('eq', Cypress.config().baseUrl + '/');

    cy.logout();
    cy.reload();
    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });

  it('is redirected to the login page after manual logout', () => {
    cy.login('user', '0123456789');

    cy.visit('/');
    cy.url().should('eq', Cypress.config().baseUrl + '/');

    cy.get('[data-testid="account"').click();
    cy.get('[data-testid="logout"').click();

    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });
});

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Login - /login', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('redirects to the login page without authentication', () => {
    cy.visit('/');
    cy.url().should('eq', Cypress.config().baseUrl + '/login');

    cy.visit('/projects/randomProjectId');
    cy.url().should('eq', Cypress.config().baseUrl + '/login');

    cy.visit('/projects/randomProjectId/assessments/randomAssessmentId');
    cy.url().should('eq', Cypress.config().baseUrl + '/login');
  });

  it('cannot log the user in without username and password', () => {
    cy.get('[data-testid="login"]').should('be.disabled');
  });

  it('can log the user in with a proper username and password', () => {
    cy.get('[data-testid="username"').type('user');
    cy.get('[data-testid="password"').type('0123456789');
    cy.get('[data-testid="login"]').should('not.be.disabled');
  });

  it('shows an error if the credentials are invalid', () => {
    cy.get('[data-testid="error"]').should('not.be.visible');

    cy.get('[data-testid="username"').type('some username');
    cy.get('[data-testid="password"').type('some password');
    cy.get('[data-testid="login"]').click();

    cy.get('[data-testid="error"]').should('be.visible');
  });
});

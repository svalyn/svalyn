/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Dashboard - /', () => {
  beforeEach(() => {
    cy.deleteAllProjects();
    cy.visit('/');
  });

  it('contains a form to create a new project', () => {
    cy.get('[data-testid=label]').should('be.visible');
  });

  it('requires a label to create a new project', () => {
    cy.get('[data-testid=create-project]').should('be.disabled');
  });

  it('can create a new project', () => {
    cy.get('[data-testid=label]').type('NewProject');
    cy.get('[data-testid=create-project]').click();

    cy.get('[data-testid=NewProject').should('be.visible');
  });

  it('can delete a project', () => {
    cy.get('[data-testid=label]').type('ProjectToDelete');
    cy.get('[data-testid=create-project]').click();

    cy.get('[data-testid=ProjectToDelete').should('be.visible');
    cy.get('[data-testid="ProjectToDelete - more"').click();
    cy.get('[data-testid=delete]').click();

    cy.get('[data-testid=ProjectToDelete').should('not.be.visible');
  });
});

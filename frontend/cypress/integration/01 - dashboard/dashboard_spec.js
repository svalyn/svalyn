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

  it('cannot navigate to the next page', () => {
    cy.get('[data-testid=next]').should('have.attr', 'aria-disabled', 'true');
  });

  it('can navigate to the next page', () => {
    for (let index = 0; index < 25; index++) {
      cy.createProject(`Project ${index}`);
    }
    cy.reload();

    cy.get('[data-testid="Project 24"').should('not.be.visible');

    cy.get('[data-testid=previous]').should('have.attr', 'aria-disabled', 'true');
    cy.get('[data-testid=next]').should('have.attr', 'aria-disabled', 'false');

    cy.get('[data-testid=next]').click();

    cy.get('[data-testid="Project 24"').should('be.visible');
  });

  it('cannot navigate to the previous page', () => {
    cy.get('[data-testid=previous]').should('have.attr', 'aria-disabled', 'true');
  });

  it('can navigate to the previous page', () => {
    for (let index = 0; index < 25; index++) {
      cy.createProject(`Project ${index}`);
    }
    cy.visit('/?page=2');

    cy.get('[data-testid=previous]').should('have.attr', 'aria-disabled', 'false');
    cy.get('[data-testid=next]').should('have.attr', 'aria-disabled', 'true');
    cy.get('[data-testid="Project 1"').should('not.be.visible');

    cy.get('[data-testid=previous]').click();

    cy.get('[data-testid="Project 1"').should('be.visible');
  });
});

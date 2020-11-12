/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Dashboard - /', () => {
  beforeEach(() => {
    cy.login('user', '0123456789');
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
    cy.get('[data-testid="select-ProjectToDelete"').click();
    cy.get('[data-testid=delete]').click();

    cy.get('[data-testid=ProjectToDelete').should('not.be.visible');
  });

  it('cannot navigate to the next page', () => {
    for (let index = 0; index < 10; index++) {
      const name = `Project ${index < 10 ? '0' + index : index}`;
      cy.createProject(name);
    }
    cy.reload();

    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('have.attr', 'disabled');
  });

  it('can navigate to the next page', () => {
    for (let index = 0; index < 25; index++) {
      const name = `Project ${index < 10 ? '0' + index : index}`;
      cy.createProject(name);
    }
    cy.reload();

    cy.get('[data-testid="Project 24"').should('not.be.visible');

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('have.attr', 'disabled');
    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('not.have.attr', 'disabled');

    cy.get('[data-testid=pagination] button:nth-of-type(2)').click();

    cy.get('[data-testid="Project 24"').should('be.visible');
  });

  it('cannot navigate to the previous page', () => {
    for (let index = 0; index < 10; index++) {
      const name = `Project ${index < 10 ? '0' + index : index}`;
      cy.createProject(name);
    }
    cy.reload();

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('have.attr', 'disabled');
  });

  it('can navigate to the previous page', () => {
    for (let index = 0; index < 25; index++) {
      const name = `Project ${index < 10 ? '0' + index : index}`;
      cy.createProject(name);
    }
    cy.visit('/?page=1');

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('not.have.attr', 'disabled');
    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('have.attr', 'disabled');
    cy.get('[data-testid="Project 01"').should('not.be.visible');

    cy.get('[data-testid=pagination] button:nth-of-type(1)').click();

    cy.get('[data-testid="Project 01"').should('be.visible');
  });
});

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Project - /projects/:projectId', () => {
  beforeEach(() => {
    cy.deleteAllProjects();
    cy.createProject('NewProject').then((res) => {
      const projectId = res.body.data.createProject.project.id;
      cy.visit(`/projects/${projectId}`);
    });
  });

  it('contains a form to create a new assessment', () => {
    cy.get('[data-testid=label]').should('be.visible');
    cy.get('[data-testid=description]').should('be.visible');
  });

  it('requires a label to create a new assessment', () => {
    cy.get('[data-testid=create-assessment]').should('be.disabled');
  });

  it('can create a new assessment', () => {
    cy.get('[data-testid=label]').type('NewAssessment');
    cy.get('[data-testid=create-assessment]').click();

    cy.get('[data-testid=NewAssessment').should('be.visible');
  });

  it('can delete an assessment', () => {
    cy.get('[data-testid=label]').type('AssessmentToDelete');
    cy.get('[data-testid=create-assessment]').click();

    cy.get('[data-testid=AssessmentToDelete').should('be.visible');
    cy.get('[data-testid="AssessmentToDelete - more"').click();
    cy.get('[data-testid=delete]').click();

    cy.get('[data-testid=AssessmentToDelete').should('not.be.visible');
  });
});

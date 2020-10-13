/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Project - /projects/:projectId', () => {
  beforeEach(() => {
    cy.login('user', '0123456789');
    cy.deleteAllProjects();
    cy.createProject('NewProject').then((res) => {
      const projectId = res.body.data.createProject.project.id;
      cy.wrap(projectId).as('projectId');

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
    cy.get('[data-testid="select-AssessmentToDelete"').click();
    cy.get('[data-testid=delete]').click();

    cy.get('[data-testid=AssessmentToDelete').should('not.be.visible');
  });

  it('cannot navigate to the next page', function () {
    const projectId = this.projectId;
    cy.getDescriptions().then((res) => {
      const descriptionId = res.body.data.descriptions[0].id;
      for (let index = 0; index < 10; index++) {
        cy.createAssessment(projectId, descriptionId, `Assessment ${index}`);
      }
    });
    cy.reload();

    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('have.attr', 'disabled');
  });

  it('can navigate to the next page', function () {
    const projectId = this.projectId;
    cy.getDescriptions().then((res) => {
      const descriptionId = res.body.data.descriptions[0].id;
      for (let index = 0; index < 25; index++) {
        cy.createAssessment(projectId, descriptionId, `Assessment ${index}`);
      }
    });
    cy.reload();

    cy.get('[data-testid="Assessment 1"').should('not.be.visible');

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('have.attr', 'disabled');
    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('not.have.attr', 'disabled');

    cy.get('[data-testid=pagination] button:nth-of-type(2)').click();

    cy.get('[data-testid="Assessment 1"').should('be.visible');
  });

  it('cannot navigate to the previous page', function () {
    const projectId = this.projectId;
    cy.getDescriptions().then((res) => {
      const descriptionId = res.body.data.descriptions[0].id;
      for (let index = 0; index < 10; index++) {
        cy.createAssessment(projectId, descriptionId, `Assessment ${index}`);
      }
    });
    cy.reload();

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('have.attr', 'disabled');
  });

  it('can navigate to the previous page', function () {
    const projectId = this.projectId;
    cy.getDescriptions().then((res) => {
      const descriptionId = res.body.data.descriptions[0].id;
      for (let index = 0; index < 25; index++) {
        cy.createAssessment(projectId, descriptionId, `Assessment ${index}`);
      }
    });
    cy.visit(`/projects/${projectId}/?page=1`);

    cy.get('[data-testid=pagination] button:nth-of-type(1)').should('not.have.attr', 'disabled');
    cy.get('[data-testid=pagination] button:nth-of-type(2)').should('have.attr', 'disabled');
    cy.get('[data-testid="Assessment 24"').should('not.be.visible');

    cy.get('[data-testid=pagination] button:nth-of-type(1)').click();

    cy.get('[data-testid="Assessment 24"').should('be.visible');
  });
});

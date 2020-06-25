/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Assessment - /projects/:projectId/assessments/:assessmentId', () => {
  beforeEach(() => {
    cy.deleteAllProjects();
    cy.createProject('NewProject').then((res) => {
      const projectId = res.body.data.createProject.project.id;

      cy.getDescriptions().then((res) => {
        const descriptionId = res.body.data.descriptions[0].id;

        cy.createAssessment(projectId, descriptionId, 'NewAssessment').then((res) => {
          const assessmentId = res.body.data.createAssessment.assessment.id;
          cy.visit(`/projects/${projectId}/assessments/${assessmentId}`);
        });
      });
    });
  });

  it('Shows the name of the assessment', () => {
    cy.get('[data-testid=assessment-label').should('have.text', 'NewAssessment');
  });
});

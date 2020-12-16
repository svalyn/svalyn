/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

describe('Assessment - /projects/:projectId/assessments/:assessmentId', () => {
  beforeEach(() => {
    cy.login('user', '0123456789');
    cy.deleteAllProjects();
    cy.createProject('NewProject').then((res) => {
      const projectId = res.body.data.createProject.project.id;

      cy.getDescriptions(projectId).then((res) => {
        const descriptionId = res.body.data.project.descriptions[0].id;

        cy.createAssessment(projectId, descriptionId, 'NewAssessment').then((res) => {
          const assessmentId = res.body.data.createAssessment.assessment.id;
          cy.visit(`/projects/${projectId}/assessments/${assessmentId}`);
        });
      });
    });
  });

  it('Shows the name of the assessment', () => {
    cy.get('[data-testid=assessment-label]').should('have.text', 'NewAssessment');
  });

  it('Can complete an assessment', () => {
    cy.get('[data-testid="results"]').should('have.text', `Total ${22} · Success ${0} · Failure ${0}`);

    cy.get('[data-testid="Motor functions can be freezed-success"]').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${1} · Failure ${0}`);

    cy.get('[data-testid="The hosts should be emotionally stable"').click();
    cy.get('[data-testid="Hosts should not break from their role-success"').click();
    cy.get('[data-testid="Hosts should act as human beings-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${3} · Failure ${0}`);

    cy.get('[data-testid="The hosts should work as expected"').click();
    cy.get('[data-testid="Hosts should be able to use their body as regular human beings-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${4} · Failure ${0}`);

    cy.get('[data-testid="The hosts should be repairable"').click();
    cy.get('[data-testid="We should have spare parts-success"').click();
    cy.get('[data-testid="We should have facilities to repair hosts-success"').click();
    cy.get('[data-testid="Our staff should be trained-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${7} · Failure ${0}`);

    cy.get('[data-testid="Narrative & Design"').click();
    cy.get('[data-testid="The stories should be convincing"').click();
    cy.get('[data-testid="Guests should not doubt our stories-success"').click();
    cy.get('[data-testid="We should see good feedback on our stories-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${9} · Failure ${0}`);

    cy.get('[data-testid="The stories should be engaging"').click();
    cy.get('[data-testid="Our stories should be finishable-success"').click();
    cy.get('[data-testid="Our stories should be social events-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${11} · Failure ${0}`);

    cy.get('[data-testid="The stories should be addictive"').click();
    cy.get('[data-testid="Our guests should want to return to try new stories-success"').click();
    cy.get('[data-testid="Our guests should be ready to pay anything for our stories-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${13} · Failure ${0}`);

    cy.get('[data-testid="The environment should be coherent"').click();
    cy.get('[data-testid="Our guests should want to bring our park with them-success"').click();
    cy.get('[data-testid="Our guests should never want to leave our parks-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${15} · Failure ${0}`);

    cy.get('[data-testid="Quality Assurance"').click();
    cy.get('[data-testid="The operation should be interruptible"').click();
    cy.get('[data-testid="Our stories should be stoppable-success"').click();
    cy.get('[data-testid="Our stories should be restartable-success"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${17} · Failure ${0}`);

    cy.get('[data-testid="The guests should be retrievable"').click();
    cy.get('[data-testid="We should be able to evacuate our guests-failure"').click();
    cy.get('[data-testid="We should be able to provide medical assistance-failure"').click();
    cy.get('[data-testid="We should be able to extract guests from the park-failure"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${17} · Failure ${3}`);

    cy.get('[data-testid="The parks should be monitored"').click();
    cy.get('[data-testid="We need to be able to detect potential issues-failure"').click();
    cy.get('[data-testid="We need to be able to communicate with anybody in the park-failure"').click();
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${17} · Failure ${5}`);
  });

  it('Should mark a requirement as a success', () => {
    cy.get('[data-testid="The hosts should be emotionally stable-success"').should('not.be.visible');
    cy.get('[data-testid="The hosts should be emotionally stable-failure"').should('not.be.visible');

    cy.get('[data-testid="The hosts should be emotionally stable"').click();
    cy.get('[data-testid="Hosts should not break from their role-success"').click();

    cy.get('[data-testid="The hosts should be emotionally stable-success"').should('not.be.visible');
    cy.get('[data-testid="The hosts should be emotionally stable-failure"').should('not.be.visible');

    cy.get('[data-testid="Hosts should act as human beings-success"').click();

    cy.get('[data-testid="The hosts should be emotionally stable-success"').should('be.visible');
  });

  it('Should mark a category as a success', () => {
    cy.get('[data-testid="Quality Assurance-success"').should('not.be.visible');
    cy.get('[data-testid="Quality Assurance-failure"').should('not.be.visible');

    cy.get('[data-testid="Quality Assurance"').click();
    cy.get('[data-testid="The operation should be interruptible"').click();
    cy.get('[data-testid="Our stories should be stoppable-success"').click();
    cy.get('[data-testid="Our stories should be restartable-success"').click();

    cy.get('[data-testid="The guests should be retrievable"').click();
    cy.get('[data-testid="We should be able to evacuate our guests-success"').click();
    cy.get('[data-testid="We should be able to provide medical assistance-success"').click();
    cy.get('[data-testid="We should be able to extract guests from the park-success"').click();

    cy.get('[data-testid="The parks should be monitored"').click();
    cy.get('[data-testid="We need to be able to detect potential issues-success"').click();

    cy.get('[data-testid="Quality Assurance-success"').should('not.be.visible');
    cy.get('[data-testid="Quality Assurance-failure"').should('not.be.visible');

    cy.get('[data-testid="We need to be able to communicate with anybody in the park-success"').click();

    cy.get('[data-testid="Quality Assurance-success"').should('be.visible');
  });

  it('Should mark a requirement and a category as failures', () => {
    cy.get('[data-testid="The hosts should be emotionally stable-success"').should('not.be.visible');
    cy.get('[data-testid="The hosts should be emotionally stable-failure"').should('not.be.visible');

    cy.get('[data-testid="The hosts should be emotionally stable"').click();
    cy.get('[data-testid="Hosts should not break from their role-failure"').click();

    cy.get('[data-testid="The hosts should be emotionally stable-failure"').should('be.visible');
  });

  it('Should keep completed element when the assessment is marked as done', () => {
    cy.get('[data-testid="The hosts should be diagnosticable-success"').should('not.be.visible');
    cy.get('[data-testid="The hosts should be diagnosticable-failure"').should('not.be.visible');

    cy.get('[data-testid="Motor functions can be freezed-success"').click();

    cy.get('[data-testid="The hosts should be diagnosticable-success"').should('be.visible');

    cy.get('[data-testid="toggle-assessment-status"').click();

    cy.get('[data-testid="The hosts should be diagnosticable-success"').should('be.visible');

    cy.get('[data-testid="toggle-assessment-status"').click();

    cy.get('[data-testid="The hosts should be diagnosticable-success"').should('be.visible');
  });

  it('Show a message when trying to edit a test after logging out', () => {
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${0} · Failure ${0}`);

    cy.logout();

    cy.get('[data-testid="Motor functions can be freezed-success"').click();
    cy.get('[data-testid="snackbar"]').should('be.visible');
    cy.get('[data-testid="results"').should('have.text', `Total ${22} · Success ${0} · Failure ${0}`);
  });

  it('Show a message when trying to edit an assessment after logging out', () => {
    cy.logout();

    cy.get('[data-testid="toggle-assessment-status"').click();
    cy.get('[data-testid="snackbar"]').should('be.visible');
  });
});

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

const url = Cypress.env('baseAPIUrl') + '/api/graphql';

Cypress.Commands.add('deleteAllProjects', () => {
  const getProjectsQuery = `
  query getProjects {
    projects {
      id
    }
  }
  `;
  cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: { query: getProjectsQuery },
  }).then((res) => {
    const projectIds = res.body.data.projects.map((project) => project.id);

    const deleteProjectQuery = `
    mutation deleteProject($input: DeleteProjectInput!) {
      deleteProject(input: $input) {
        __typename
      }
    }
    `;
    projectIds.forEach((projectId) => {
      const variables = {
        input: { projectId },
      };
      cy.request({
        method: 'POST',
        mode: 'cors',
        url,
        body: { query: deleteProjectQuery, variables },
      });
    });
  });
});

Cypress.Commands.add('createProject', (label) => {
  const createProjectMutation = `
  mutation createProject($input: CreateProjectInput!) {
    createProject(input: $input) {
      __typename
      ... on CreateProjectSuccessPayload {
        project {
          id
        }
      }
    }
  }`;

  const variables = {
    input: {
      label,
    },
  };

  return cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: { query: createProjectMutation, variables },
  });
});

Cypress.Commands.add('getDescriptions', () => {
  const getDescriptionsQuery = `
  query getDescriptions {
    descriptions {
      id
      label
    }
  }`;

  return cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: { query: getDescriptionsQuery },
  });
});

Cypress.Commands.add('createAssessment', (projectId, descriptionId, label) => {
  const createAssessmentMutation = `
  mutation createAssessment($input: CreateAssessmentInput!) {
    createAssessment(input: $input) {
      __typename
      ... on CreateAssessmentSuccessPayload {
        assessment {
          id
        }
      }
    }
  }
  `;

  const variables = {
    input: {
      projectId,
      descriptionId,
      label,
    },
  };

  return cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: {
      query: createAssessmentMutation,
      variables,
    },
  });
});

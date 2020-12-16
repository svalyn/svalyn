/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

const url = Cypress.env('baseAPIUrl') + '/api/graphql';

Cypress.Commands.add('login', (username, password) => {
  cy.request({
    method: 'POST',
    mode: 'cors',
    url: Cypress.env('baseAPIUrl') + '/login',
    body: `username=${username}&password=${password}`,
    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
    followRedirect: false,
  });
});

Cypress.Commands.add('logout', () => {
  cy.request({
    method: 'POST',
    mode: 'cors',
    url: Cypress.env('baseAPIUrl') + '/logout',
    followRedirect: false,
  });
});

Cypress.Commands.add('deleteAllProjects', () => {
  const getProjectsQuery = `
  query getProjects {
    firstPage: projects(page: 0) {
      edges {
        node {
          id
        }
      }
    }
    secondPage: projects(page: 1) {
      edges {
        node {
          id
        }
      }
    }
  }
  `;
  cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: { query: getProjectsQuery },
  }).then((res) => {
    const firstPageProjectIds = res.body.data.firstPage.edges.map((edge) => edge.node.id);
    const secondPageProjectIds = res.body.data.secondPage.edges.map((edge) => edge.node.id);

    const deleteProjectsQuery = `
    mutation deleteProjects($input: DeleteProjectsInput!) {
      deleteProjects(input: $input) {
        __typename
      }
    }
    `;
    const projectIds = [...firstPageProjectIds, ...secondPageProjectIds];
    const variables = {
      input: { projectIds },
    };
    cy.request({
      method: 'POST',
      mode: 'cors',
      url,
      body: { query: deleteProjectsQuery, variables },
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

Cypress.Commands.add('getDescriptions', (projectId) => {
  const getDescriptionsQuery = `
  query getDescriptions($projectId: ID!) {
    project(projectId: $projectId) {
      descriptions {
        id
        label
      }
    }
  }`;

  const variables = {
    projectId,
  };

  return cy.request({
    method: 'POST',
    mode: 'cors',
    url,
    body: { query: getDescriptionsQuery, variables },
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

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';

import styles from './ProjectView.module.css';

const {
  loc: {
    source: { body: getAssessmentsQuery },
  },
} = gql`
  query getAssessments($projectId: ID!) {
    project(projectId: $projectId) {
      label
      assessments {
        id
        label
      }
    }
  }
`;

const {
  loc: {
    source: { body: createAssessmentMutation },
  },
} = gql`
  mutation createAssessment($input: CreateAssessmentInput!) {
    createAssessment(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
      ... on CreateAssessmentSuccessPayload {
        assessment {
          id
        }
      }
    }
  }
`;

export const ProjectView = () => {
  const { projectId } = useParams();
  const initialState = {
    loading: true,
    label: null,
    assessments: [],
  };
  const [{ label, assessments }, setState] = useState(initialState);

  useEffect(() => {
    const fetchAssissments = async () => {
      const body = JSON.stringify({
        query: getAssessmentsQuery,
        variables: {
          projectId,
        },
      });
      const response = await fetch('http://localhost:8080/api/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body,
      });
      const json = await response.json();
      const {
        data: {
          project: { label, assessments },
        },
      } = json;
      setState({ loading: false, label, assessments });
    };

    fetchAssissments();
  }, [projectId]);

  const onNewAssessmentClick = () => {
    const createAssessment = async () => {
      const createAssessmentResponse = await fetch('http://localhost:8080/api/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          query: createAssessmentMutation,
          variables: {
            input: {
              projectId,
              label: '',
            },
          },
        }),
      });
      const createAssessmentJson = await createAssessmentResponse.json();
      const {
        data: { createAssessment },
      } = createAssessmentJson;
      if (createAssessment.__typename === 'CreateAssessmentSuccessPayload') {
        const fetchAssessmentsResponse = await fetch('http://localhost:8080/api/graphql', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            query: getAssessmentsQuery,
            variables: {
              projectId,
            },
          }),
        });
        const fetchAssessmentsJson = await fetchAssessmentsResponse.json();
        const {
          data: {
            project: { label, assessments },
          },
        } = fetchAssessmentsJson;
        setState({ loading: false, label, assessments });
      }
    };

    createAssessment();
  };

  return (
    <div className={styles.projectView}>
      <h1>{label}</h1>
      <button onClick={onNewAssessmentClick}>New assessment</button>
      <ul>
        {assessments.map((assessment) => (
          <li key={assessment.id}>
            <Link to={`/projects/${projectId}/assessments/${assessment.id}`}>{assessment.label}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

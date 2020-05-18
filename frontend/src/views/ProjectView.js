/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import FolderIcon from '@material-ui/icons/Folder';
import HomeIcon from '@material-ui/icons/Home';
import Link from '@material-ui/core/Link';
import List from '@material-ui/core/List';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';

import { ListItemLink } from '../core/ListItemLink';

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

const useStyles = makeStyles((theme) => ({
  projectView: {
    paddingTop: '24px',
    paddingBottom: '24px',
  },
  breadcrumb: {
    marginBottom: '24px',
  },
  breadcrumbItem: {
    display: 'flex',
    flexDirection: 'row',
  },
  icon: {
    marginRight: theme.spacing(0.5),
    width: 20,
    height: 20,
  },
}));

export const ProjectView = () => {
  const classes = useStyles();
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
    <div className={classes.projectView}>
      <Container>
        <Typography variant="h1" gutterBottom>
          {label}
        </Typography>
        <Breadcrumbs className={classes.breadcrumb} aria-label="breadcrumb">
          <Link color="inherit" component={RouterLink} to="/" className={classes.breadcrumbItem}>
            <HomeIcon className={classes.icon} /> Dashboard
          </Link>
          <Typography color="textPrimary" className={classes.breadcrumbItem}>
            <FolderIcon className={classes.icon} /> {label}
          </Typography>
        </Breadcrumbs>
        <Button variant="contained" color="primary" onClick={onNewAssessmentClick}>
          New assessment
        </Button>
        <Paper>
          <List>
            {assessments.map((assessment) => {
              return (
                <ListItemLink
                  key={assessment.id}
                  to={`/projects/${projectId}/assessments/${assessment.id}`}
                  primary={assessment.label}
                  icon={<AssignmentIcon />}
                />
              );
            })}
          </List>
        </Paper>
      </Container>
    </div>
  );
};

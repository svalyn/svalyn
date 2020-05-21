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
import FormGroup from '@material-ui/core/FormGroup';
import HomeIcon from '@material-ui/icons/Home';
import Link from '@material-ui/core/Link';
import List from '@material-ui/core/List';
import Paper from '@material-ui/core/Paper';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';
import { empty } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { concatMap } from 'rxjs/operators';

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
const getAssessments = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getAssessmentsQuery, variables }),
  });

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
const createAssessment = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: createAssessmentMutation, variables }),
  });

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
    newAssessmentLabel: '',
    assessments: [],
  };
  const [{ label, newAssessmentLabel, assessments }, setState] = useState(initialState);

  useEffect(() => {
    const variables = { projectId };
    const subscription = getAssessments(variables).subscribe((ajaxResponse) => {
      const {
        response: {
          data: {
            project: { label, assessments },
          },
        },
      } = ajaxResponse;
      setState({ loading: false, newAssessmentLabel: '', label, assessments });
    });

    return () => subscription.unsubscribe();
  }, [projectId]);

  const onNewAssessmentLabel = (event) => {
    const { value } = event.target;
    setState((prevState) => {
      return { ...prevState, newAssessmentLabel: value };
    });
  };

  const onNewAssessmentClick = () => {
    const createAssessmentVariables = {
      input: {
        projectId,
        label: newAssessmentLabel,
      },
    };
    const subscription = createAssessment(createAssessmentVariables)
      .pipe(
        concatMap((createAssessmentAjaxResponse) => {
          const { response } = createAssessmentAjaxResponse;
          if (response?.data?.createAssessment.__typename === 'CreateAssessmentSuccessPayload') {
            return getAssessments({ projectId });
          }
          return empty();
        })
      )
      .subscribe((getAssessmentsAjaxResponse) => {
        const {
          response: {
            data: {
              project: { label, assessments },
            },
          },
        } = getAssessmentsAjaxResponse;
        setState({ loading: false, newAssessmentLabel: '', label, assessments });
      });

    return () => subscription.unsubscribe();
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
        <FormGroup row>
          <TextField
            label="Assessment label"
            variant="outlined"
            size="small"
            value={newAssessmentLabel}
            onChange={onNewAssessmentLabel}
            required
          />
          <Button variant="contained" color="primary" onClick={onNewAssessmentClick}>
            Create
          </Button>
        </FormGroup>
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

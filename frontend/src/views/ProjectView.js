/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { Fragment, useEffect } from 'react';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import FolderIcon from '@material-ui/icons/Folder';
import FormControl from '@material-ui/core/FormControl';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import InputLabel from '@material-ui/core/InputLabel';
import Link from '@material-ui/core/Link';
import List from '@material-ui/core/List';
import MenuItem from '@material-ui/core/MenuItem';
import Paper from '@material-ui/core/Paper';
import Select from '@material-ui/core/Select';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';
import { ajax } from 'rxjs/ajax';
import { concatMap } from 'rxjs/operators';
import { useMachine } from '@xstate/react';

import { ListItemLink } from '../core/ListItemLink';
import { newAssessmentFormMachine, projectViewMachine } from './ProjectViewMachine';

const {
  loc: {
    source: { body: getProjectQuery },
  },
} = gql`
  query getProject($projectId: ID!) {
    descriptions {
      id
      label
    }
    project(projectId: $projectId) {
      label
      assessments {
        id
        label
        createdOn
        lastModifiedOn
        success
        failure
        testCount
        status
      }
    }
  }
`;
const getProject = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getProjectQuery, variables }),
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

const useProjectViewStyles = makeStyles((theme) => ({
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
  const classes = useProjectViewStyles();
  const { projectId } = useParams();

  const [{ context }, dispatch] = useMachine(projectViewMachine);
  const { label, assessments, descriptions } = context;

  useEffect(() => {
    dispatch('FETCH_PROJECT');
    const variables = { projectId };
    const subscription = getProject(variables).subscribe(({ response }) =>
      dispatch({ type: 'HANDLE_PROJECT_RESPONSE', response })
    );

    return () => subscription.unsubscribe();
  }, [projectId, dispatch]);

  const onNewAssessmentClick = (label, descriptionId) => {
    const createAssessmentVariables = {
      input: {
        projectId,
        descriptionId,
        label,
      },
    };
    dispatch('CREATE_ASSESSMENT');
    createAssessment(createAssessmentVariables)
      .pipe(concatMap(() => getProject({ projectId })))
      .subscribe(({ response }) => dispatch({ type: 'HANDLE_PROJECT_RESPONSE', response }));
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
        <Grid container spacing={4}>
          <Grid item xs={3}>
            <NewAssessmentForm descriptions={descriptions} onNewAssessmentClick={onNewAssessmentClick} />
          </Grid>
          <Grid item xs={9}>
            <Assessments projectId={projectId} assessments={assessments} />
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

const useNewAssessmentFormStyles = makeStyles((theme) => ({
  form: {
    display: 'flex',
    flexDirection: 'column',
    paddingTop: '0.5rem',
    paddingLeft: '1rem',
    paddingRight: '1rem',
    '& > *': {
      marginBottom: theme.spacing(2),
    },
  },
}));

const NewAssessmentForm = ({ descriptions, onNewAssessmentClick }) => {
  const classes = useNewAssessmentFormStyles();

  const [{ value, context }, dispatch] = useMachine(newAssessmentFormMachine);
  const { label, descriptionId } = context;

  useEffect(() => {
    const newDescriptionId = descriptions[0].id;
    if (descriptionId === '' && descriptionId !== newDescriptionId) {
      dispatch({ type: 'UPDATE_DESCRIPTION', descriptionId: newDescriptionId });
    }
  }, [descriptionId, descriptions, dispatch]);

  const onChangeLabel = (event) => {
    const { value } = event.target;
    dispatch({ type: 'UPDATE_LABEL', label: value });
  };
  const onChangeDescriptionId = (event) => {
    const { value } = event.target;
    dispatch({ type: 'UPDATE_DESCRIPTION', descriptionId: value });
  };

  const onSubmit = (event) => {
    event.preventDefault();

    dispatch('CREATE_ASSESSMENT');
    onNewAssessmentClick(label, descriptionId);
  };
  return (
    <Paper>
      <form onSubmit={onSubmit} className={classes.form}>
        <TextField label="Label" value={label} onChange={onChangeLabel} required />
        <FormControl>
          <InputLabel id="description-label">Description</InputLabel>
          <Select
            labelId="description-label"
            id="description"
            value={descriptionId}
            onChange={onChangeDescriptionId}
            label="Description"
            required>
            {descriptions.map((description) => (
              <MenuItem value={description.id} key={description.id}>
                {description.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Button type="submit" variant="contained" color="primary" disabled={value !== 'valid'}>
          Create assessment
        </Button>
      </form>
    </Paper>
  );
};

const useAssessmentsStyles = makeStyles((theme) => ({
  title: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: '0.5rem',
    '& > *:first-child': {
      marginRight: '1rem',
    },
  },
}));

const Assessments = ({ projectId, assessments }) => {
  const classes = useAssessmentsStyles();
  const size = assessments.length;
  return (
    <Paper>
      <List>
        {assessments.map((assessment, index) => {
          const primary = (
            <div className={classes.title}>
              <Typography variant="h4">{assessment.label}</Typography>
              <Typography variant="subtitle2">{assessment.status}</Typography>
            </div>
          );
          const secondary = (
            <>
              <Typography variant="subtitle2">{`Total ${assessment.testCount} · Success ${assessment.success} · Failure ${assessment.failure}`}</Typography>
              <Typography variant="caption">{`Created on ${assessment.createdOn} · Last modified on ${assessment.lastModifiedOn}`}</Typography>
            </>
          );

          return (
            <Fragment key={assessment.id}>
              <ListItemLink
                to={`/projects/${projectId}/assessments/${assessment.id}`}
                primary={primary}
                secondary={secondary}
                icon={<AssignmentIcon />}
                disableTypography
              />
              {index <= size - 2 ? <Divider /> : null}
            </Fragment>
          );
        })}
      </List>
    </Paper>
  );
};

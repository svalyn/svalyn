/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import Container from '@material-ui/core/Container';
import FolderIcon from '@material-ui/icons/Folder';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import List from '@material-ui/core/List';
import Paper from '@material-ui/core/Paper';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { gql } from 'graphql.macro';
import { of } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { concatMap, catchError } from 'rxjs/operators';
import { useMachine } from '@xstate/react';

import { ListItemLink } from '../core/ListItemLink';
import { newProjectFormMachine, dashboardViewMachine } from './DashboardViewMachine';

const {
  loc: {
    source: { body: getProjectsQuery },
  },
} = gql`
  query getProjects {
    projects {
      id
      label
    }
  }
`;

const getProjects = () =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getProjectsQuery }),
  });

const {
  loc: {
    source: { body: createProjectQuery },
  },
} = gql`
  mutation createProject($input: CreateProjectInput!) {
    createProject(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
      ... on CreateProjectSuccessPayload {
        project {
          id
        }
      }
    }
  }
`;
const createProject = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: createProjectQuery, variables }),
  });

const useStyles = makeStyles((theme) => ({
  dashboardView: {
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

export const DashboardView = () => {
  const classes = useStyles();

  const [{ value, context }, dispatch] = useMachine(dashboardViewMachine);
  const { projects } = context;

  useEffect(() => {
    dispatch('FETCH');
    const subscription = getProjects()
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
    return () => subscription.unsubscribe();
  }, [dispatch]);

  const onNewProjectClick = (label) => {
    const variables = {
      input: {
        label,
      },
    };
    dispatch('CREATE_PROJECT');
    createProject(variables)
      .pipe(concatMap(() => getProjects()))
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  let rightElement = null;
  switch (value) {
    case 'success':
      rightElement = <Projects projects={projects} />;
      break;
    case 'empty':
      rightElement = <Message content="You do not have any projects for the moment, start by creating one" />;
      break;
    case 'error':
      rightElement = <Message content="An error has occurred while retrieving the projects" />;
      break;
    default:
      rightElement = <Message content="An error has occurred while retrieving the projects" />;
      break;
  }

  return (
    <div className={classes.dashboardView}>
      <Container>
        <Typography variant="h1" gutterBottom>
          Dashboard
        </Typography>
        <Breadcrumbs className={classes.breadcrumb} aria-label="breadcrumb">
          <Typography color="textPrimary" className={classes.breadcrumbItem}>
            <HomeIcon className={classes.icon} /> Dashboard
          </Typography>
        </Breadcrumbs>
        <Grid container spacing={4}>
          <Grid item xs={3}>
            <NewProjectForm onNewProjectClick={onNewProjectClick} />
          </Grid>
          <Grid item xs={9}>
            {rightElement}
          </Grid>
        </Grid>
      </Container>
    </div>
  );
};

const useNewProjectFormStyles = makeStyles((theme) => ({
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

const NewProjectForm = ({ onNewProjectClick }) => {
  const classes = useNewProjectFormStyles();

  const [{ value, context }, dispatch] = useMachine(newProjectFormMachine);
  const { label } = context;

  const onChangeLabel = (event) => {
    const { value } = event.target;
    dispatch({ type: 'UPDATE_LABEL', label: value });
  };

  const onSubmit = (event) => {
    event.preventDefault();

    dispatch('CREATE_PROJECT');
    onNewProjectClick(label);
  };

  return (
    <Paper>
      <form onSubmit={onSubmit} className={classes.form}>
        <TextField label="Label" value={label} onChange={onChangeLabel} required />
        <Button type="submit" variant="contained" color="primary" disabled={value !== 'valid'}>
          Create project
        </Button>
      </form>
    </Paper>
  );
};

const Projects = ({ projects }) => {
  return (
    <Paper>
      <List>
        {projects.map((project) => {
          return (
            <ListItemLink
              key={project.id}
              to={`/projects/${project.id}`}
              primary={project.label}
              icon={<FolderIcon />}
            />
          );
        })}
      </List>
    </Paper>
  );
};

const useMessageStyles = makeStyles((theme) => ({
  message: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '300px',
  },
}));
const Message = ({ content }) => {
  const classes = useMessageStyles();
  return (
    <div className={classes.message}>
      <Typography variant="h4">{content}</Typography>
    </div>
  );
};

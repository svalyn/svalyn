/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import CloseIcon from '@material-ui/icons/Close';
import Container from '@material-ui/core/Container';
import FolderIcon from '@material-ui/icons/Folder';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MoreVertIcon from '@material-ui/icons/MoreVert';
import Paper from '@material-ui/core/Paper';
import Snackbar from '@material-ui/core/Snackbar';
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
    source: { body: createProjectMutation },
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
    body: JSON.stringify({ query: createProjectMutation, variables }),
  });

const {
  loc: {
    source: { body: deleteProjectMutation },
  },
} = gql`
  mutation deleteProject($input: DeleteProjectInput!) {
    deleteProject(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const deleteProject = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: deleteProjectMutation, variables }),
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
  const { dashboardView, toast } = value;
  const { projects, anchorElement, projectId, message } = context;

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
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.createProject.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.createProject.message });
          }
          return getProjects();
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onMoreClick = (event, project) => dispatch({ type: 'OPEN_MENU', anchorElement: event.target, project });
  const onMenuClose = () => dispatch({ type: 'CLOSE_MENU' });
  const onDelete = () => {
    const variables = {
      input: {
        projectId,
      },
    };
    dispatch('DELETE_PROJECT');
    deleteProject(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.deleteProject.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.deleteProject.message });
          }
          return getProjects();
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  let rightElement = <Projects projects={projects} onMoreClick={onMoreClick} />;
  if (dashboardView === 'empty') {
    rightElement = <Message content="You do not have any projects for the moment, start by creating one" />;
  } else if (dashboardView === 'error') {
    rightElement = <Message content="An error has occurred, please refresh the page" />;
  }

  return (
    <>
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

      <Menu id="simple-menu" anchorEl={anchorElement} keepMounted open={Boolean(anchorElement)} onClose={onMenuClose}>
        <MenuItem onClick={onDelete} data-testid="delete">
          Delete
        </MenuItem>
      </Menu>

      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        open={toast === 'visible'}
        autoHideDuration={3000}
        onClose={() => dispatch({ type: 'HIDE_TOAST' })}
        message={message}
        action={
          <IconButton size="small" aria-label="close" color="inherit" onClick={() => dispatch({ type: 'HIDE_TOAST' })}>
            <CloseIcon fontSize="small" />
          </IconButton>
        }
      />
    </>
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
        <TextField label="Label" value={label} onChange={onChangeLabel} autoFocus required data-testid="label" />
        <Button
          type="submit"
          variant="contained"
          color="primary"
          disabled={value !== 'valid'}
          data-testid="create-project">
          Create project
        </Button>
      </form>
    </Paper>
  );
};

const Projects = ({ projects, onMoreClick }) => {
  return (
    <Paper>
      <List>
        {projects.map((project) => {
          const onClick = (event) => onMoreClick(event, project);
          return (
            <ListItemLink
              key={project.id}
              to={`/projects/${project.id}`}
              primary={project.label}
              icon={<FolderIcon />}
              action={
                <ListItemSecondaryAction>
                  <IconButton
                    aria-label="more"
                    aria-controls="long-menu"
                    aria-haspopup="true"
                    onClick={onClick}
                    data-testid={`${project.label} - more`}>
                    <MoreVertIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              }
              data-testid={project.label}
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

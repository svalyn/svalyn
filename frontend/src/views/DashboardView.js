/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import { Link as RouterLink, useLocation, useHistory, Redirect } from 'react-router-dom';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import CloseIcon from '@material-ui/icons/Close';
import Container from '@material-ui/core/Container';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import IconButton from '@material-ui/core/IconButton';
import Link from '@material-ui/core/Link';
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

import { AuthenticatedHeader } from '../headers/AuthenticatedHeader';
import { EnhancedTable } from '../table/Table';
import { newProjectFormMachine, dashboardViewMachine } from './DashboardViewMachine';

const {
  loc: {
    source: { body: getProjectsQuery },
  },
} = gql`
  query getProjects($page: Int!) {
    projects(page: $page) {
      edges {
        node {
          id
          label
          ownedBy {
            id
            username
          }
          createdBy {
            username
          }
          createdOn
        }
      }
      pageInfo {
        count
      }
    }
  }
`;

const getProjects = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getProjectsQuery, variables }),
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
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: createProjectMutation, variables }),
  });

const {
  loc: {
    source: { body: deleteProjectsMutation },
  },
} = gql`
  mutation deleteProjects($input: DeleteProjectsInput!) {
    deleteProjects(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const deleteProjects = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: deleteProjectsMutation, variables }),
  });

const useStyles = makeStyles((theme) => ({
  view: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content 1fr',
    minHeight: '100vh',
  },
  dashboardView: {
    paddingTop: '1.5rem',
    paddingBottom: '1.5rem',
  },
  breadcrumb: {
    marginBottom: '1.5rem',
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
  const history = useHistory();
  const { search } = useLocation();
  const pageFromURL = parseInt(new URLSearchParams(search).get('page') ?? 0);

  const [{ value, context }, dispatch] = useMachine(dashboardViewMachine, {
    context: {
      page: pageFromURL,
    },
  });

  const { dashboardView, toast } = value;
  const { page, projects, selectedProjectIds, count, message } = context;

  useEffect(() => {
    if (pageFromURL !== page) {
      if (page === 0) {
        history.push('/');
      } else {
        history.push(`/?page=${page}`);
      }
    }
  }, [history, page, pageFromURL]);

  useEffect(() => {
    dispatch('FETCH');
    const subscription = getProjects({ page })
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
    return () => subscription.unsubscribe();
  }, [dispatch, page]);

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
          return getProjects({ page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onDelete = () => {
    const variables = {
      input: {
        projectIds: selectedProjectIds,
      },
    };
    dispatch('DELETE_PROJECTS');
    deleteProjects(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.deleteProjects.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.deleteProjects.message });
          }
          return getProjects({ page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  let rightElement = null;
  if (dashboardView === 'empty') {
    if (count > 0 && page * 20 > count) {
      rightElement = <Message content={`You are trying to view the page n°${page} but it does not exist`} />;
    } else {
      rightElement = <Message content="You do not have any projects for the moment, start by creating one" />;
    }
  } else if (dashboardView === 'error') {
    rightElement = <Message content="An error has occurred, please refresh the page" />;
  } else if (dashboardView === 'unauthorized') {
    return <Redirect to="/login" />;
  } else if (dashboardView === 'success') {
    const onSelectProject = (_, projectId) => dispatch({ type: 'SELECT_PROJECT', projectId });
    const onSelectAllProjects = (event) => dispatch({ type: 'SELECT_ALL_PROJECTS', target: event.target });
    const onChangePage = (_, page) => dispatch({ type: 'CHANGE_PAGE', page });

    rightElement = (
      <Projects
        projects={projects}
        selectedProjectIds={selectedProjectIds}
        count={count}
        page={page}
        onSelectProject={onSelectProject}
        onSelectAllProjects={onSelectAllProjects}
        onChangePage={onChangePage}
        onDelete={onDelete}
      />
    );
  }

  return (
    <>
      <div className={classes.view}>
        <AuthenticatedHeader />
        <div className={classes.dashboardView}>
          <Container maxWidth="xl">
            <Typography variant="h1" gutterBottom>
              Dashboard
            </Typography>
            <Breadcrumbs className={classes.breadcrumb} aria-label="breadcrumb">
              <Typography color="textPrimary" className={classes.breadcrumbItem}>
                <HomeIcon className={classes.icon} /> Dashboard
              </Typography>
            </Breadcrumbs>
            <Grid container spacing={4}>
              <Grid item xs={2}>
                <NewProjectForm onNewProjectClick={onNewProjectClick} />
              </Grid>
              <Grid item xs={10}>
                {rightElement}
              </Grid>
            </Grid>
          </Container>
        </div>
      </div>

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

const Projects = ({
  projects,
  selectedProjectIds,
  count,
  page,
  onSelectProject,
  onSelectAllProjects,
  onChangePage,
  onDelete,
}) => {
  const headers = [{ label: 'Name' }, { label: 'Owned by' }, { label: 'Created by' }, { label: 'Created on' }];
  const itemPropertyAccessor = (project, index) => {
    if (index === 0) {
      return (
        <Link color="inherit" component={RouterLink} to={`/projects/${project.id}`} data-testid={project.label}>
          {project.label}
        </Link>
      );
    } else if (index === 1) {
      return project.ownedBy.username;
    } else if (index === 2) {
      return project.createdBy.username;
    } else if (index === 3) {
      return project.createdOn;
    }
    return null;
  };
  const itemDataTestidProvider = (item) => item.label;

  return (
    <div>
      <EnhancedTable
        title="Projects"
        headers={headers}
        items={projects}
        selectedItemIds={selectedProjectIds}
        itemPropertyAccessor={itemPropertyAccessor}
        itemDataTestidProvider={itemDataTestidProvider}
        totalItemsCount={count}
        onSelect={onSelectProject}
        onSelectAll={onSelectAllProjects}
        page={page}
        itemsPerPage={20}
        onChangePage={onChangePage}
        onDelete={onDelete}
      />
    </div>
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

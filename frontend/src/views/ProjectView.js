/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { Fragment, useEffect } from 'react';
import { Link as RouterLink, Redirect, useLocation, useParams } from 'react-router-dom';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import CloseIcon from '@material-ui/icons/Close';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import FolderIcon from '@material-ui/icons/Folder';
import FormControl from '@material-ui/core/FormControl';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import Link from '@material-ui/core/Link';
import List from '@material-ui/core/List';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MoreVertIcon from '@material-ui/icons/MoreVert';
import Paper from '@material-ui/core/Paper';
import Select from '@material-ui/core/Select';
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
import { AuthenticatedHeader } from '../headers/AuthenticatedHeader';
import { newAssessmentFormMachine, projectViewMachine } from './ProjectViewMachine';

const {
  loc: {
    source: { body: getProjectQuery },
  },
} = gql`
  query getProject($projectId: ID!, $page: Int!) {
    descriptions {
      id
      label
    }
    project(projectId: $projectId) {
      label
      assessments(page: $page) {
        edges {
          node {
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
        pageInfo {
          hasPreviousPage
          hasNextPage
          pageCount
        }
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

const {
  loc: {
    source: { body: deleteAssessmentMutation },
  },
} = gql`
  mutation deleteAssessment($input: DeleteAssessmentInput!) {
    deleteAssessment(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const deleteAssessment = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: deleteAssessmentMutation, variables }),
  });

const useProjectViewStyles = makeStyles((theme) => ({
  view: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content 1fr',
    minHeight: '100vh',
  },
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
  paginationButtonsContainer: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'flex-end',
    marginBottom: '0.5rem',
  },
}));

export const ProjectView = () => {
  const classes = useProjectViewStyles();
  const { projectId } = useParams();
  const { search } = useLocation();
  const page = parseInt(new URLSearchParams(search).get('page') ?? 1);

  const [{ value, context }, dispatch] = useMachine(projectViewMachine);
  const { projectView, toast } = value;
  const {
    label,
    assessments,
    pageCount,
    hasPreviousPage,
    hasNextPage,
    descriptions,
    anchorElement,
    assessmentId,
    message,
  } = context;

  useEffect(() => {
    dispatch('FETCH_PROJECT');
    const subscription = getProject({ projectId, page })
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));

    return () => subscription.unsubscribe();
  }, [projectId, page, dispatch]);

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
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.createAssessment.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.createAssessment.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onMoreClick = (event, assessment) => dispatch({ type: 'OPEN_MENU', anchorElement: event.target, assessment });
  const onMenuClose = () => dispatch({ type: 'CLOSE_MENU' });
  const onDelete = () => {
    const variables = {
      input: {
        assessmentId,
      },
    };
    dispatch('DELETE_ASSESSMENT');
    deleteAssessment(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.deleteAssessment.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.deleteAssessment.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  let rightElement = <Assessments projectId={projectId} assessments={assessments} onMoreClick={onMoreClick} />;
  if (projectView === 'error') {
    rightElement = <Message content="An error has occurred, please refresh the page" />;
  } else if (projectView === 'missing') {
    rightElement = <Message content={`No project found with the id ${projectId}`} />;
  } else if (projectView === 'empty') {
    if (pageCount > 0 && page > pageCount) {
      rightElement = (
        <Message content={`You are trying to view the page n°${page} but there are only ${pageCount} pages`} />
      );
    } else {
      rightElement = <Message content="You do not have any assessments for the moment, start by creating one" />;
    }
  } else if (projectView === 'unauthorized') {
    return <Redirect to="/login" />;
  }

  return (
    <>
      <div className={classes.view}>
        <AuthenticatedHeader />
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
            <div className={classes.paginationButtonsContainer}>
              <Button
                component={RouterLink}
                to={`/projects/${projectId}/${page === 2 ? '' : `?page=${page - 1}`}`}
                disabled={!hasPreviousPage}
                data-testid="previous">
                Previous
              </Button>
              <Button
                component={RouterLink}
                to={`/projects/${projectId}/?page=${page + 1}`}
                disabled={!hasNextPage}
                data-testid="next">
                Next
              </Button>
            </div>
            <Grid container spacing={4}>
              <Grid item xs={3}>
                <NewAssessmentForm descriptions={descriptions} onNewAssessmentClick={onNewAssessmentClick} />
              </Grid>
              <Grid item xs={9}>
                {rightElement}
              </Grid>
            </Grid>
          </Container>
        </div>
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
        <TextField label="Label" value={label} onChange={onChangeLabel} autoFocus required data-testid="label" />
        <FormControl>
          <InputLabel id="description-label">Description</InputLabel>
          <Select
            labelId="description-label"
            id="description"
            value={descriptionId}
            onChange={onChangeDescriptionId}
            label="Description"
            required
            data-testid="description">
            {descriptions.map((description) => (
              <MenuItem value={description.id} key={description.id}>
                {description.label}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          disabled={value !== 'valid'}
          data-testid="create-assessment">
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
    alignItems: 'baseline',
    marginBottom: '0.5rem',
    '& > *:first-child': {
      marginRight: '1rem',
    },
  },
}));

const Assessments = ({ projectId, assessments, onMoreClick }) => {
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

          const onClick = (event) => onMoreClick(event, assessment);
          const action = (
            <ListItemSecondaryAction>
              <IconButton
                aria-label="more"
                aria-controls="long-menu"
                aria-haspopup="true"
                onClick={onClick}
                data-testid={`${assessment.label} - more`}>
                <MoreVertIcon />
              </IconButton>
            </ListItemSecondaryAction>
          );

          return (
            <Fragment key={assessment.id}>
              <ListItemLink
                to={`/projects/${projectId}/assessments/${assessment.id}`}
                primary={primary}
                secondary={secondary}
                icon={<AssignmentIcon />}
                action={action}
                disableTypography
                data-testid={assessment.label}
              />
              {index <= size - 2 ? <Divider /> : null}
            </Fragment>
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

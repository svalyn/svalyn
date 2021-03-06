/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import { Link as RouterLink, Redirect, useLocation, useHistory, useParams } from 'react-router-dom';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import CancelIcon from '@material-ui/icons/Cancel';
import CloseIcon from '@material-ui/icons/Close';
import Chip from '@material-ui/core/Chip';
import Container from '@material-ui/core/Container';
import FolderIcon from '@material-ui/icons/Folder';
import FormControl from '@material-ui/core/FormControl';
import Grid from '@material-ui/core/Grid';
import HomeIcon from '@material-ui/icons/Home';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import Link from '@material-ui/core/Link';
import MenuItem from '@material-ui/core/MenuItem';
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

import { AuthenticatedHeader } from '../headers/AuthenticatedHeader';
import { EnhancedTable } from '../table/Table';
import {
  newAssessmentFormMachine,
  ownerFormMachine,
  membersFormMachine,
  projectViewMachine,
} from './ProjectViewMachine';

const {
  loc: {
    source: { body: getProjectQuery },
  },
} = gql`
  query getProject($projectId: ID!, $page: Int!) {
    principal {
      id
      username
    }
    project(projectId: $projectId) {
      label
      ownedBy {
        id
        username
      }
      members {
        id
        username
      }
      descriptions {
        id
        label
      }
      assessments(page: $page) {
        edges {
          node {
            id
            label
            createdOn
            createdBy {
              username
            }
            lastModifiedOn
            lastModifiedBy {
              username
            }
            success
            failure
            testCount
            status
          }
        }
        pageInfo {
          count
        }
      }
    }
  }
`;
const getProject = (variables) =>
  ajax({
    url: '/graphql',
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
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: createAssessmentMutation, variables }),
  });

const {
  loc: {
    source: { body: deleteAssessmentsMutation },
  },
} = gql`
  mutation deleteAssessments($input: DeleteAssessmentsInput!) {
    deleteAssessments(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const deleteAssessments = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: deleteAssessmentsMutation, variables }),
  });

const {
  loc: {
    source: { body: addMemberMutation },
  },
} = gql`
  mutation addMemberToProject($input: AddMemberToProjectInput!) {
    addMemberToProject(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const addMember = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: addMemberMutation, variables }),
  });

const {
  loc: {
    source: { body: removeMemberMutation },
  },
} = gql`
  mutation removeMemberFromProject($input: RemoveMemberFromProjectInput!) {
    removeMemberFromProject(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const removeMember = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: removeMemberMutation, variables }),
  });

const {
  loc: {
    source: { body: leaveMemberMutation },
  },
} = gql`
  mutation leaveProject($input: LeaveProjectInput!) {
    leaveProject(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const leaveProject = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: leaveMemberMutation, variables }),
  });

const {
  loc: {
    source: { body: changeProjectOwnerMutation },
  },
} = gql`
  mutation changeProjectOwner($input: ChangeProjectOwnerInput!) {
    changeProjectOwner(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
const changeProjectOwner = (variables) =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: changeProjectOwnerMutation, variables }),
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
}));

export const ProjectView = () => {
  const classes = useProjectViewStyles();
  const { projectId } = useParams();
  const history = useHistory();
  const { search } = useLocation();
  const pageFromURL = parseInt(new URLSearchParams(search).get('page') ?? 0);

  const [{ value, context }, dispatch] = useMachine(projectViewMachine, {
    context: {
      page: pageFromURL,
    },
  });
  const { projectView, toast } = value;
  const {
    page,
    principal,
    label,
    ownedBy,
    members,
    assessments,
    selectedAssessmentIds,
    count,
    descriptions,
    message,
  } = context;

  const isOwner = principal?.id === ownedBy?.id;

  useEffect(() => {
    if (pageFromURL !== page) {
      if (page === 0) {
        history.push(`/projects/${projectId}`);
      } else {
        history.push(`/projects/${projectId}?page=${page}`);
      }
    }
  }, [history, projectId, page, pageFromURL]);

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

  const onDelete = () => {
    const variables = {
      input: {
        projectId,
        assessmentIds: selectedAssessmentIds,
      },
    };
    dispatch('DELETE_ASSESSMENTS');
    deleteAssessments(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.deleteAssessments.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.deleteAssessments.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onAddMember = (username) => {
    const variables = {
      input: {
        projectId,
        username,
      },
    };
    dispatch('ADD_MEMBER');
    addMember(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.addMemberToProject.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.addMemberToProject.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onRemoveMember = (username) => {
    const variables = {
      input: {
        projectId,
        username,
      },
    };
    dispatch('REMOVE_MEMBER');
    removeMember(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.removeMemberFromProject.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.removeMemberFromProject.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  const onLeaveProject = () => {
    const variables = {
      input: {
        projectId,
      },
    };
    leaveProject(variables)
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_LEAVE_PROJECT_RESPONSE', ajaxResponse }));
  };

  const onChangeProjectOwner = (newOwnerId) => {
    const variables = {
      input: {
        projectId,
        newOwnerId,
      },
    };
    dispatch('CHANGE_PROJECT_OWNER');
    changeProjectOwner(variables)
      .pipe(
        concatMap((ajaxResponse) => {
          const { data, errors } = ajaxResponse.response;
          if (errors || ajaxResponse.status !== 200) {
            dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
          } else if (data.changeProjectOwner.__typename === 'ErrorPayload') {
            dispatch({ type: 'SHOW_TOAST', message: data.changeProjectOwner.message });
          }
          return getProject({ projectId, page });
        })
      )
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  let centerElement = null;
  if (projectView === 'leftProject') {
    return <Redirect to="/" />;
  } else if (projectView === 'error') {
    centerElement = <Message content="An error has occurred, please refresh the page" />;
  } else if (projectView === 'missing') {
    centerElement = <Message content={`No project found with the id ${projectId}`} />;
  } else if (projectView === 'empty') {
    if (count > 0 && page * 20 > count) {
      centerElement = <Message content={`You are trying to view the page n°${page} but it does not exist`} />;
    } else {
      centerElement = <Message content="You do not have any assessments for the moment, start by creating one" />;
    }
  } else if (projectView === 'unauthorized') {
    return <Redirect to="/login" />;
  } else if (projectView === 'success') {
    const onSelectAssessment = (_, assessmentId) => dispatch({ type: 'SELECT_ASSESSMENT', assessmentId });
    const onSelectAllAssessments = (event) => dispatch({ type: 'SELECT_ALL_ASSESSMENTS', target: event.target });
    const onChangePage = (_, page) => dispatch({ type: 'CHANGE_PAGE', page });

    centerElement = (
      <Assessments
        projectId={projectId}
        assessments={assessments}
        selectedAssessmentIds={selectedAssessmentIds}
        count={count}
        page={page}
        onSelectAssessment={onSelectAssessment}
        onSelectAllAssessments={onSelectAllAssessments}
        onChangePage={onChangePage}
        onDelete={onDelete}
      />
    );
  }

  return (
    <>
      <div className={classes.view}>
        <AuthenticatedHeader />
        <div className={classes.projectView}>
          <Container maxWidth="xl">
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
              <Grid item xs={2}>
                <NewAssessmentForm descriptions={descriptions} onNewAssessmentClick={onNewAssessmentClick} />
              </Grid>
              <Grid item xs={8}>
                {centerElement}
              </Grid>
              <Grid item xs={2}>
                {ownedBy ? (
                  <MembersForm
                    isOwner={isOwner}
                    owner={ownedBy}
                    members={members}
                    onAddMember={onAddMember}
                    onRemoveMember={onRemoveMember}
                    onLeaveProject={onLeaveProject}
                    onChangeProjectOwner={onChangeProjectOwner}
                  />
                ) : null}
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

const Assessments = ({
  projectId,
  assessments,
  selectedAssessmentIds,
  count,
  page,
  onSelectAssessment,
  onSelectAllAssessments,
  onChangePage,
  onDelete,
}) => {
  const headers = [
    { label: 'Name' },
    { label: 'Created by' },
    { label: 'Created on' },
    { label: 'Last modified by' },
    { label: 'Last modified on' },
    { label: 'Success' },
    { label: 'Failure' },
    { label: 'Status' },
  ];
  const itemPropertyAccessor = (assessment, index) => {
    if (index === 0) {
      return (
        <Link
          color="inherit"
          component={RouterLink}
          to={`/projects/${projectId}/assessments/${assessment.id}`}
          data-testid={assessment.label}>
          {assessment.label}
        </Link>
      );
    } else if (index === 1) {
      return assessment.createdBy.username;
    } else if (index === 2) {
      return assessment.createdOn;
    } else if (index === 3) {
      return assessment.lastModifiedBy.username;
    } else if (index === 4) {
      return assessment.lastModifiedOn;
    } else if (index === 5) {
      return assessment.success;
    } else if (index === 6) {
      return assessment.failure;
    } else if (index === 7) {
      return assessment.status;
    }

    return null;
  };
  const itemDataTestidProvider = (item) => item.label;

  return (
    <div>
      <EnhancedTable
        title="Assessments"
        headers={headers}
        items={assessments}
        selectedItemIds={selectedAssessmentIds}
        itemPropertyAccessor={itemPropertyAccessor}
        itemDataTestidProvider={itemDataTestidProvider}
        totalItemsCount={count}
        onSelect={onSelectAssessment}
        onSelectAll={onSelectAllAssessments}
        page={page}
        itemsPerPage={20}
        onChangePage={onChangePage}
        onDelete={onDelete}
      />
    </div>
  );
};

const useMembersFormStyles = makeStyles((theme) => ({
  membership: {
    display: 'flex',
    flexDirection: 'column',
  },
  ownerFormArea: {
    marginBottom: theme.spacing(4),
  },
  memberFormArea: {
    marginBottom: theme.spacing(4),
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    paddingTop: theme.spacing(1),
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(2),
    '& > *': {
      marginBottom: theme.spacing(2),
    },
  },
  members: {
    display: 'flex',
    flexDirection: 'column',
    padding: theme.spacing(2),
  },
  title: {
    paddingBottom: theme.spacing(1),
  },
  memberList: {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    '& > *': {
      marginRight: theme.spacing(1),
      marginTop: theme.spacing(1),
    },
  },
  leaveProject: {
    marginTop: theme.spacing(2),
  },
}));

const MembersForm = ({
  isOwner,
  owner,
  members,
  onAddMember,
  onRemoveMember,
  onLeaveProject,
  onChangeProjectOwner,
}) => {
  const classes = useMembersFormStyles();

  const [
    {
      value: memberFormValue,
      context: { username },
    },
    dispatchMemberForm,
  ] = useMachine(membersFormMachine);

  const [
    {
      value: ownerFormValue,
      context: { selectedOwnerId },
    },
    dispatchOwnerForm,
  ] = useMachine(ownerFormMachine, {
    context: {
      ownerId: owner.id,
      selectedOwnerId: owner.id,
    },
  });

  const onChangeUsername = (event) => {
    const { value } = event.target;
    dispatchMemberForm({ type: 'UPDATE_USERNAME', username: value });
  };

  const onSubmitAddMember = (event) => {
    event.preventDefault();

    dispatchMemberForm('ADD_MEMBER');
    onAddMember(username);
  };

  const onDelete = (username) => {
    onRemoveMember(username);
  };

  const onChangeOwnerId = (event) => {
    const {
      target: { value },
    } = event;
    dispatchOwnerForm({ type: 'UPDATE_OWNER', selectedOwnerId: value });
  };

  const onSubmitChangerOwner = (event) => {
    event.preventDefault();

    onChangeProjectOwner(selectedOwnerId);
  };

  return (
    <div className={classes.membership}>
      {isOwner ? (
        <Paper className={classes.ownerFormArea}>
          <form onSubmit={onSubmitChangerOwner} className={classes.form}>
            <FormControl>
              <InputLabel id="owner-label">Owner</InputLabel>
              <Select
                labelId="owner-label"
                id="owner"
                value={selectedOwnerId}
                onChange={onChangeOwnerId}
                label="Owner"
                required
                data-testid="owner">
                {[owner].concat(members).map((member) => (
                  <MenuItem value={member.id} key={member.id} data-testid={member.username}>
                    {member.username}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={ownerFormValue !== 'valid'}
              data-testid="change-owner">
              Change owner
            </Button>
          </form>
        </Paper>
      ) : null}
      {isOwner ? (
        <Paper className={classes.memberFormArea}>
          <form onSubmit={onSubmitAddMember} className={classes.form}>
            <TextField label="Username" value={username} onChange={onChangeUsername} required data-testid="username" />
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={memberFormValue !== 'valid'}
              data-testid="add-member">
              Add member
            </Button>
          </form>
        </Paper>
      ) : null}
      {members.length > 0 ? (
        <Paper>
          <div className={classes.members}>
            <Typography variant="h5" className={classes.title}>
              Members
            </Typography>
            <div className={classes.memberList} data-testid="members">
              {members.map((member) => (
                <Chip
                  size="small"
                  label={member.username}
                  key={member.id}
                  onDelete={isOwner ? () => onDelete(member.username) : null}
                  deleteIcon={<CancelIcon data-testid={`remove-member-${member.username}`} />}
                />
              ))}
            </div>
            {isOwner ? null : (
              <Button
                className={classes.leaveProject}
                variant="outlined"
                onClick={onLeaveProject}
                data-testid="leave-project">
                Leave project
              </Button>
            )}
          </div>
        </Paper>
      ) : null}
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

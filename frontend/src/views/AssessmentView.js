/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Button from '@material-ui/core/Button';
import CloseIcon from '@material-ui/icons/Close';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import FolderIcon from '@material-ui/icons/Folder';
import HomeIcon from '@material-ui/icons/Home';
import IconButton from '@material-ui/core/IconButton';
import LinearProgress from '@material-ui/core/LinearProgress';
import Link from '@material-ui/core/Link';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Paper from '@material-ui/core/Paper';
import Snackbar from '@material-ui/core/Snackbar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';
import { of } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { catchError } from 'rxjs/operators';
import { useMachine } from '@xstate/react';

import { Description } from '../description/Description';
import { Requirements } from '../requirements/Requirements';
import { assessmentViewMachine } from './AssessmentViewMachine';

const {
  loc: {
    source: { body: getAssessmentQuery },
  },
} = gql`
  query getAssessment($projectId: ID!, $assessmentId: ID!) {
    project(projectId: $projectId) {
      label
      assessment(assessmentId: $assessmentId) {
        id
        label
        createdOn
        lastModifiedOn
        success
        failure
        testCount
        status
        categories {
          id
          label
          description
          requirements {
            id
            label
            description
            tests {
              id
              label
              description
              steps
              status
            }
          }
        }
      }
    }
  }
`;

const getAssessment = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getAssessmentQuery, variables }),
  });

const {
  loc: {
    source: { body: updateAssessmentStatusQuery },
  },
} = gql`
  mutation updateAssessmentStatus($input: UpdateAssessmentStatusInput!) {
    updateAssessment: updateAssessmentStatus(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
      ... on UpdateAssessmentStatusSuccessPayload {
        assessment {
          id
          label
          createdOn
          lastModifiedOn
          success
          failure
          testCount
          status
          categories {
            id
            label
            description
            requirements {
              id
              label
              description
              tests {
                id
                label
                description
                steps
                status
              }
            }
          }
        }
      }
    }
  }
`;

const updateAssessmentStatus = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: updateAssessmentStatusQuery, variables }),
  });

const useAssessmentViewStyles = makeStyles((theme) => ({
  assessmentView: {
    display: 'flex',
    flexDirection: 'row',
  },
  message: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '300px',
  },
}));

export const AssessmentView = () => {
  const classes = useAssessmentViewStyles();
  const { projectId, assessmentId } = useParams();
  const [{ value, context }, dispatch] = useMachine(assessmentViewMachine);
  const { assessmentView, toast } = value;
  const { label, assessment, selectedCategoryId, message } = context;

  useEffect(() => {
    dispatch('FETCH');
    const variables = { projectId, assessmentId };
    const subscription = getAssessment(variables)
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
    return () => subscription.unsubscribe();
  }, [projectId, assessmentId, dispatch]);

  const onAssessmentUpdated = (ajaxResponse) => {
    const { data, errors } = ajaxResponse.response;
    if (errors || ajaxResponse.status !== 200) {
      dispatch({ type: 'SHOW_TOAST', message: 'An unexpected error has occurred, please refresh the page' });
    } else if (data.updateAssessment.__typename === 'ErrorPayload') {
      dispatch({ type: 'SHOW_TOAST', message: data.updateAssessment.message });
    } else if (data.updateAssessment.assessment) {
      dispatch({ type: 'REFRESH_ASSESSMENT', assessment: data.updateAssessment.assessment });
    }
  };
  const onCategoryClick = (selectedCategory) => dispatch({ type: 'SELECT_CATEGORY', selectedCategory });

  let content = null;
  switch (assessmentView) {
    case 'error':
      content = 'An error has occurred while retrieving the assessment';
      break;
    case 'missing':
      content = `No assessment found with the id ${assessmentId}`;
      break;
    case 'empty':
      content = 'The assessment does not have any content';
      break;
    default:
      break;
  }
  if (content) {
    return (
      <Container>
        <div className={classes.message}>
          <Typography variant="h4">{content}</Typography>
        </div>
      </Container>
    );
  }

  const selectedCategory = assessment?.categories.filter((category) => category.id === selectedCategoryId)[0];
  return (
    <>
      <div className={classes.assessmentView}>
        <Categories
          categories={assessment?.categories ?? []}
          selectedCategoryId={selectedCategoryId}
          onCategoryClick={onCategoryClick}
        />
        {selectedCategory ? (
          <MainArea
            projectId={projectId}
            projectLabel={label}
            assessment={assessment}
            category={selectedCategory}
            onAssessmentUpdated={onAssessmentUpdated}
          />
        ) : null}
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

const drawerWidth = 240;
const useCategoriesStyles = makeStyles((theme) => ({
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
}));

const Categories = ({ categories, selectedCategoryId, onCategoryClick }) => {
  const classes = useCategoriesStyles();
  return (
    <Drawer
      className={classes.drawer}
      variant="permanent"
      classes={{
        paper: classes.drawerPaper,
      }}>
      <Toolbar />
      <List>
        {categories.map((category) => {
          return (
            <ListItem
              button
              onClick={() => onCategoryClick(category)}
              selected={category.id === selectedCategoryId}
              key={category.id}
              data-testid={category.label}>
              <ListItemText primary={category.label} />
            </ListItem>
          );
        })}
      </List>
    </Drawer>
  );
};

const useMainAreaStyles = makeStyles((theme) => ({
  container: {
    display: 'flex',
  },
  content: {
    flexGrow: 1,
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content min-content 1fr',
    paddingTop: '24px',
    paddingBottom: '24px',
  },
  linearProgressColorPrimary: {
    backgroundColor: theme.palette.grey[theme.palette.type === 'dark' ? 700 : 200],
  },
  successLinearProgress: {
    backgroundColor: theme.palette.success[theme.palette.type],
  },
  errorLinearProgress: {
    backgroundColor: theme.palette.error[theme.palette.type],
  },
  requirements: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content min-content 1fr',
  },
}));

const findLinearProgressClasses = (classes, assessment) => {
  return {
    colorPrimary: classes.linearProgressColorPrimary,
    barColorPrimary: assessment.failure > 0 ? classes.errorLinearProgress : classes.successLinearProgress,
  };
};

const MainArea = ({ projectId, projectLabel, assessment, category, onAssessmentUpdated }) => {
  const classes = useMainAreaStyles();
  return (
    <Container maxWidth="xl" className={classes.container}>
      <div className={classes.content}>
        <Header
          projectId={projectId}
          projectLabel={projectLabel}
          assessment={assessment}
          onAssessmentUpdated={onAssessmentUpdated}
        />
        <LinearProgress
          variant="determinate"
          value={((assessment.success + assessment.failure) / assessment.testCount) * 100}
          classes={findLinearProgressClasses(classes, assessment)}
        />
        <Paper className={classes.requirements} square>
          <Description category={category} />
          <Divider />
          <Requirements
            assessmentId={assessment.id}
            assessmentStatus={assessment.status}
            requirements={category.requirements}
            onAssessmentUpdated={onAssessmentUpdated}
          />
        </Paper>
      </div>
    </Container>
  );
};

const useHeaderStyles = makeStyles((theme) => ({
  header: {
    display: 'flex',
    flexDirection: 'column',
  },
  titleArea: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: '0.4rem',
  },
  title: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'baseline',
  },
  titleLabel: {
    marginRight: '1rem',
  },
  descriptionArea: {
    marginBottom: '1rem',
  },
  breadcrumb: {
    marginBottom: '1.2rem',
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

const Header = ({ projectId, projectLabel, assessment, onAssessmentUpdated }) => {
  const classes = useHeaderStyles();
  const {
    id: assessmentId,
    label: assessmentLabel,
    status,
    createdOn,
    lastModifiedOn,
    success,
    failure,
    testCount,
  } = assessment;

  const toggleAssessmentStatus = () => {
    const newStatus = status === 'OPEN' ? 'CLOSED' : 'OPEN';
    const variables = {
      input: {
        assessmentId,
        status: newStatus,
      },
    };
    updateAssessmentStatus(variables).subscribe((ajaxResponse) => onAssessmentUpdated(ajaxResponse));
  };

  return (
    <div className={classes.header}>
      <div className={classes.titleArea}>
        <div className={classes.title}>
          <Typography variant="h1" className={classes.titleLabel} data-testid="assessment-label">
            {assessmentLabel}
          </Typography>
          <Typography variant="subtitle2">{`${status}`}</Typography>
        </div>
        <div>
          <Button variant="outlined" size="small" onClick={toggleAssessmentStatus}>
            {status === 'OPEN' ? 'Mark as closed' : 'Reopen'}
          </Button>
        </div>
      </div>
      <div className={classes.descriptionArea}>
        <Typography
          variant="subtitle1"
          data-testid="results">{`Total ${testCount} · Success ${success} · Failure ${failure}`}</Typography>
        <Typography variant="caption">{`Created on ${createdOn} · Last modified on ${lastModifiedOn}`}</Typography>
      </div>
      <Breadcrumbs className={classes.breadcrumb} aria-label="breadcrumb">
        <Link color="inherit" component={RouterLink} to="/" className={classes.breadcrumbItem}>
          <HomeIcon className={classes.icon} />
          Dashboard
        </Link>
        <Link color="inherit" component={RouterLink} to={`/projects/${projectId}`} className={classes.breadcrumbItem}>
          <FolderIcon className={classes.icon} />
          {projectLabel}
        </Link>
        <Typography color="textPrimary" className={classes.breadcrumbItem}>
          <AssignmentIcon className={classes.icon} />
          {assessmentLabel}
        </Typography>
      </Breadcrumbs>
    </div>
  );
};

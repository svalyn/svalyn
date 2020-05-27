/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Badge from '@material-ui/core/Badge';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import CancelIcon from '@material-ui/icons/Cancel';
import CheckCircleIcon from '@material-ui/icons/CheckCircle';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import FolderIcon from '@material-ui/icons/Folder';
import HomeIcon from '@material-ui/icons/Home';
import LinearProgress from '@material-ui/core/LinearProgress';
import Link from '@material-ui/core/Link';
import Paper from '@material-ui/core/Paper';
import Toolbar from '@material-ui/core/Toolbar';
import Tooltip from '@material-ui/core/Tooltip';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';
import { ajax } from 'rxjs/ajax';
import { useMachine } from '@xstate/react';

import { Categories } from '../categories/Categories';
import { Description } from '../description/Description';
import { Requirements } from '../requirements/Requirements';
import { assessmentViewMachine } from './AssessmentViewMachine';

const {
  loc: {
    source: { body: query },
  },
} = gql`
  query getAssessment($projectId: ID!, $assessmentId: ID!) {
    project(projectId: $projectId) {
      label
      assessment(assessmentId: $assessmentId) {
        id
        label
        success
        failure
        testCount
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
    body: JSON.stringify({ query, variables }),
  });

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  assessmentView: {
    display: 'flex',
    flexDirection: 'row',
  },
  header: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: '0.4rem',
  },
  headerTitle: {
    marginRight: '1rem',
  },
  headerIcon: {
    marginRight: '0.5rem',
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
  },
  drawerPaper: {
    width: drawerWidth,
  },
  drawerContainer: {
    overflow: 'auto',
  },
  container: {
    display: 'flex',
  },
  content: {
    flexGrow: 1,
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content min-content min-content 1fr',
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
  linearProgressColorPrimary: {
    backgroundColor: theme.palette.grey[theme.palette.type === 'dark' ? 700 : 200],
  },
  successLinearProgress: {
    backgroundColor: theme.palette.success.dark,
  },
  errorLinearProgress: {
    backgroundColor: theme.palette.error.dark,
  },
  requirements: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content min-content 1fr',
  },
}));

export const AssessmentView = () => {
  const classes = useStyles();
  const { projectId, assessmentId } = useParams();
  const [{ context }, dispatch] = useMachine(assessmentViewMachine);
  const { label, assessment, selectedCategoryId } = context;

  useEffect(() => {
    dispatch('FETCH');
    const variables = { projectId, assessmentId };
    const subscription = getAssessment(variables).subscribe(({ response }) =>
      dispatch({ type: 'HANDLE_RESPONSE', response })
    );
    return () => subscription.unsubscribe();
  }, [projectId, assessmentId, dispatch]);

  const onTestUpdated = (assessment) => dispatch({ type: 'REFRESH_ASSESSMENT', assessment });
  const onCategoryClick = (selectedCategory) => dispatch({ type: 'SELECT_CATEGORY', selectedCategory });

  const selectedCategory = assessment?.categories.filter((category) => category.id === selectedCategoryId)[0];
  return (
    <div className={classes.assessmentView}>
      <LeftPanel
        categories={assessment?.categories ?? []}
        selectedCategoryId={selectedCategoryId}
        onCategoryClick={onCategoryClick}
      />
      {selectedCategory ? (
        <RightPanel
          projectId={projectId}
          projectLabel={label}
          assessment={assessment}
          category={selectedCategory}
          onTestUpdated={onTestUpdated}
        />
      ) : (
        <EmptyRightPanel />
      )}
    </div>
  );
};

const LeftPanel = ({ categories, selectedCategoryId, onCategoryClick }) => {
  const classes = useStyles();
  return (
    <Drawer
      className={classes.drawer}
      variant="permanent"
      classes={{
        paper: classes.drawerPaper,
      }}>
      <Toolbar />
      <Categories categories={categories} selectedCategoryId={selectedCategoryId} onCategoryClick={onCategoryClick} />
    </Drawer>
  );
};

const findLinearProgressClasses = (classes, assessment) => {
  return {
    colorPrimary: classes.linearProgressColorPrimary,
    barColorPrimary: assessment.failure > 0 ? classes.errorLinearProgress : classes.successLinearProgress,
  };
};

const RightPanel = ({ projectId, projectLabel, assessment, category, onTestUpdated }) => {
  const classes = useStyles();
  return (
    <Container maxWidth="xl" className={classes.container}>
      <div className={classes.content}>
        <div className={classes.header}>
          <Typography variant="h1" className={classes.headerTitle}>
            {assessment.label}
          </Typography>
          <Tooltip title={`${assessment.success} success`} placement="top" className={classes.headerIcon}>
            <Badge badgeContent={assessment.success} showZero>
              <CheckCircleIcon />
            </Badge>
          </Tooltip>
          <Tooltip title={`${assessment.failure} failure`} placement="top" className={classes.headerIcon}>
            <Badge badgeContent={assessment.failure} showZero>
              <CancelIcon />
            </Badge>
          </Tooltip>
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
            <AssignmentIcon />
            {assessment.label}
          </Typography>
        </Breadcrumbs>
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
            requirements={category.requirements}
            onTestUpdated={onTestUpdated}
          />
        </Paper>
      </div>
    </Container>
  );
};

const EmptyRightPanel = () => {
  const classes = useStyles();
  return <div className={classes.content}>Please select a category</div>;
};

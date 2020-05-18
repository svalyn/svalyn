/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import AssignmentIcon from '@material-ui/icons/Assignment';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Container from '@material-ui/core/Container';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import FolderIcon from '@material-ui/icons/Folder';
import HomeIcon from '@material-ui/icons/Home';
import Link from '@material-ui/core/Link';
import Paper from '@material-ui/core/Paper';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';

import { Categories } from '../categories/Categories';
import { Description } from '../description/Description';
import { Requirements } from '../requirements/Requirements';

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
              status
            }
          }
        }
      }
    }
  }
`;

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  assessmentView: {
    display: 'flex',
    flexDirection: 'row',
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
    gridTemplateRows: 'min-content min-content 1fr',
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
  requirements: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content min-content 1fr',
  },
}));

export const AssessmentView = () => {
  const classes = useStyles();
  const { projectId, assessmentId } = useParams();

  const initialState = {
    loading: true,
    label: null,
    assessment: null,
    selectedCategory: null,
  };
  const [{ label, assessment, selectedCategory }, setState] = useState(initialState);

  useEffect(() => {
    const fetchProjects = async () => {
      const body = JSON.stringify({
        query,
        variables: {
          projectId,
          assessmentId,
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
          project: { label, assessment },
        },
      } = json;
      setState({ loading: false, label, assessment, selectedCategory: assessment?.categories[0] });
    };

    fetchProjects();
  }, [projectId, assessmentId]);

  const onCategoryClick = (selectedCategory) => setState((prevState) => ({ ...prevState, selectedCategory }));
  return (
    <div className={classes.assessmentView}>
      <LeftPanel categories={assessment?.categories ?? []} onCategoryClick={onCategoryClick} />
      {selectedCategory ? (
        <RightPanel
          projectId={projectId}
          projectLabel={label}
          assessmentLabel={assessment?.label}
          category={selectedCategory}
        />
      ) : (
        <EmptyRightPanel />
      )}
    </div>
  );
};

const LeftPanel = ({ categories, onCategoryClick }) => {
  const classes = useStyles();
  return (
    <Drawer
      className={classes.drawer}
      variant="permanent"
      classes={{
        paper: classes.drawerPaper,
      }}>
      <Toolbar />
      <Categories categories={categories} onCategoryClick={onCategoryClick} />
    </Drawer>
  );
};

const RightPanel = ({ projectId, projectLabel, assessmentLabel, category }) => {
  const classes = useStyles();
  return (
    <Container maxWidth="xl" className={classes.container}>
      <div className={classes.content}>
        <Typography variant="h1" gutterBottom>
          {assessmentLabel}
        </Typography>
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
            {assessmentLabel}
          </Typography>
        </Breadcrumbs>
        <Paper className={classes.requirements}>
          <Description category={category} />
          <Divider />
          <Requirements requirements={category.requirements} />
        </Paper>
      </div>
    </Container>
  );
};

const EmptyRightPanel = () => {
  const classes = useStyles();
  return <div className={classes.content}>Please select a category</div>;
};

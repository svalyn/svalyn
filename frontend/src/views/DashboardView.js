/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import Breadcrumbs from '@material-ui/core/Breadcrumbs';
import Container from '@material-ui/core/Container';
import FolderIcon from '@material-ui/icons/Folder';
import HomeIcon from '@material-ui/icons/Home';
import List from '@material-ui/core/List';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { gql } from 'graphql.macro';
import { ajax } from 'rxjs/ajax';

import { ListItemLink } from '../core/ListItemLink';

const {
  loc: {
    source: { body: query },
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
    body: JSON.stringify({ query }),
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
  const initialState = { loading: true, projects: [] };
  const [{ projects }, setState] = useState(initialState);

  useEffect(() => {
    const subscription = getProjects().subscribe((ajaxResponse) => {
      const {
        response: {
          data: { projects },
        },
      } = ajaxResponse;
      setState({ loading: false, projects });
    });
    return () => subscription.unsubscribe();
  }, []);

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
      </Container>
    </div>
  );
};

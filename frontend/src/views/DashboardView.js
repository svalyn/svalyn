/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
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
import { useMachine } from '@xstate/react';

import { ListItemLink } from '../core/ListItemLink';
import { dashboardViewMachine } from './DashboardViewMachine';

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

  const [{ context }, dispatch] = useMachine(dashboardViewMachine);
  const { projects } = context;

  useEffect(() => {
    dispatch('FETCH');
    const subscription = getProjects().subscribe(({ response }) => dispatch({ type: 'HANDLE_RESPONSE', response }));
    return () => subscription.unsubscribe();
  }, [dispatch]);

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

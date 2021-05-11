/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useEffect } from 'react';
import { Redirect, Link as RouterLink } from 'react-router-dom';
import AccountCircle from '@material-ui/icons/AccountCircle';
import AppBar from '@material-ui/core/AppBar';
import IconButton from '@material-ui/core/IconButton';
import Link from '@material-ui/core/Link';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { gql } from 'graphql.macro';
import { of } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { catchError } from 'rxjs/operators';
import { useMachine } from '@xstate/react';

import { authenticatedHeaderMachine } from './AuthenticatedHeaderMachine';

const {
  loc: {
    source: { body: getPrincipalQuery },
  },
} = gql`
  query getPrincipal {
    principal {
      username
    }
  }
`;
const getPrincipal = () =>
  ajax({
    url: '/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query: getPrincipalQuery }),
  });

const logout = () =>
  ajax({
    url: '/logout',
    method: 'POST',
  });

const useStyles = makeStyles((theme) => ({
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
  title: {
    flexGrow: 1,
  },
  user: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
  username: {
    marginRight: theme.spacing(2),
  },
}));

export const AuthenticatedHeader = () => {
  const classes = useStyles();
  const [{ value, context }, dispatch] = useMachine(authenticatedHeaderMachine);
  const { principal, anchorElement } = context;

  useEffect(() => {
    dispatch('FETCH');
    const subscription = getPrincipal()
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));

    return () => subscription.unsubscribe();
  }, [dispatch]);

  const onAccountClick = (event) => dispatch({ type: 'OPEN_MENU', anchorElement: event.target });
  const onMenuClose = () => dispatch({ type: 'CLOSE_MENU' });
  const onLogout = () => {
    logout()
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'LOGGED_OUT', ajaxResponse }));
  };

  if (value === 'loggedOut') {
    return <Redirect to="/login" />;
  }

  return (
    <>
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <Link color="inherit" component={RouterLink} to="/" className={classes.title}>
            <Typography variant="h4">Svalyn</Typography>
          </Link>
          {principal ? (
            <div className={classes.user}>
              <Typography variant="h4" className={classes.username}>
                {principal.username}
              </Typography>
              <IconButton
                edge="end"
                aria-label="account of current user"
                aria-controls="account-menu"
                aria-haspopup="true"
                onClick={onAccountClick}
                color="inherit"
                data-testid="account">
                <AccountCircle />
              </IconButton>
            </div>
          ) : null}
        </Toolbar>
      </AppBar>
      <Toolbar />

      <Menu id="simple-menu" anchorEl={anchorElement} keepMounted open={Boolean(anchorElement)} onClose={onMenuClose}>
        <MenuItem onClick={onLogout} data-testid="logout">
          Logout
        </MenuItem>
      </Menu>
    </>
  );
};

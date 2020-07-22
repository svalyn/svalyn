/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import { Redirect } from 'react-router-dom';
import Button from '@material-ui/core/Button';
import CloseIcon from '@material-ui/icons/Close';
import Container from '@material-ui/core/Container';
import IconButton from '@material-ui/core/IconButton';
import Paper from '@material-ui/core/Paper';
import Snackbar from '@material-ui/core/Snackbar';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import { of } from 'rxjs';
import { ajax } from 'rxjs/ajax';
import { catchError } from 'rxjs/operators';
import { useMachine } from '@xstate/react';

import { loginViewMachine } from './LoginViewMachine';

const login = (username, password) => {
  const body = `username=${username}&password=${password}`;

  return ajax({
    url: '/login',
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
    body,
  });
};

const useStyles = makeStyles((theme) => ({
  loginView: {
    paddingTop: '4rem',
    paddingBottom: '1.5rem',
  },
  title: {
    textAlign: 'center',
    paddingBottom: '2rem',
  },
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
export const LoginView = () => {
  const classes = useStyles();
  const [{ value, context }, dispatch] = useMachine(loginViewMachine);
  const { username, password, message } = context;

  const onUsernameChange = (event) => {
    const { value } = event.target;
    dispatch({ type: 'UPDATE_USERNAME', username: value });
  };

  const onPasswordChange = (event) => {
    const { value } = event.target;
    dispatch({ type: 'UPDATE_PASSWORD', password: value });
  };

  const onSubmit = (event) => {
    event.preventDefault();
    login(username, password)
      .pipe(catchError((error) => of(error)))
      .subscribe((ajaxResponse) => dispatch({ type: 'HANDLE_RESPONSE', ajaxResponse }));
  };

  if (value === 'authenticated') {
    return <Redirect to="/" />;
  }

  return (
    <>
      <div className={classes.loginView}>
        <Container maxWidth="sm">
          <Typography variant="h1" gutterBottom className={classes.title}>
            Sign in to Svalyn
          </Typography>
          <Paper>
            <form onSubmit={onSubmit} className={classes.form}>
              <TextField
                label="Username"
                value={username}
                onChange={onUsernameChange}
                autoComplete="username"
                autoFocus
                required
                data-testid="username"
              />
              <TextField
                label="Password"
                type="password"
                value={password}
                onChange={onPasswordChange}
                autoComplete="current-password"
                required
                data-testid="password"
              />
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={value !== 'valid'}
                data-testid="login">
                Login
              </Button>
            </form>
          </Paper>
        </Container>
      </div>
      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        open={!!message}
        autoHideDuration={3000}
        onClose={() => dispatch({ type: 'CLEAR_MESSAGE' })}
        message={message}
        action={
          <IconButton size="small" aria-label="close" color="inherit" onClick={() => dispatch({ type: 'HIDE_TOAST' })}>
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        data-testid="error"
      />
    </>
  );
};

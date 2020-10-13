/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import CssBaseline from '@material-ui/core/CssBaseline';
import blue from '@material-ui/core/colors/blue';
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';
import { BrowserRouter } from 'react-router-dom';

import { App } from './app/App';

const theme = createMuiTheme({
  palette: {
    type: 'dark',
    primary: blue,
  },
  typography: {
    h1: {
      fontSize: '2rem',
      fontWeight: '500',
      letterSpacing: '0.6px',
    },
    h2: {
      fontSize: '1.625rem',
      fontWeight: '500',
      letterSpacing: '0.4px',
    },
    h3: {
      fontSize: '1.5rem',
      fontWeight: 'normal',
      letterSpacing: '0.23px',
    },
    h4: {
      fontSize: '1.25rem',
      fontWeight: 'normal',
      letterSpacing: '0.23px',
    },
    h5: {
      fontSize: '1rem',
      fontWeight: '500',
      letterSpacing: '0.20px',
    },
    caption: {
      fontSize: '0.875rem',
    },
  },
});

ReactDOM.render(
  <BrowserRouter>
    <ThemeProvider theme={theme}>
      <StrictMode>
        <CssBaseline />
        <App />
      </StrictMode>
    </ThemeProvider>
  </BrowserRouter>,
  document.getElementById('root')
);

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { StrictMode } from 'react';
import ReactDOM from 'react-dom';
import CssBaseline from '@material-ui/core/CssBaseline';
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles';
import { BrowserRouter } from 'react-router-dom';

import { App } from './app/App';

const theme = createMuiTheme({
  palette: {
    type: 'dark',
  },
  typography: {
    h1: {
      fontSize: '2.5rem',
      fontWeight: 'normal',
      letterSpacing: 'normal',
    },
    h2: {
      fontSize: '1.875rem',
      fontWeight: 'normal',
      letterSpacing: '0.23px',
    },
    h3: {
      fontSize: '1.5rem',
      fontWeight: 'normal',
      letterSpacing: '0.23px',
    },
    h4: {
      fontSize: '1.3rem',
      fontWeight: 'normal',
      letterSpacing: '0.23px',
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

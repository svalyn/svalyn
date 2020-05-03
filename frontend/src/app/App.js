/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';

import { Header } from './Header';
import { Main } from './Main';

import styles from './App.module.css';

export const App = () => {
  return (
    <div className={styles.app}>
      <Header />
      <Main />
    </div>
  );
};

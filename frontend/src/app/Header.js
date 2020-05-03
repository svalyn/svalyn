/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import { Link } from 'react-router-dom';

import styles from './Header.module.css';

export const Header = () => {
  return (
    <div className={styles.header}>
      <Link to="/">Svalyn</Link>
    </div>
  );
};

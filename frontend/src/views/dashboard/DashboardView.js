/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import { Link } from 'react-router-dom';

import styles from './DashboardView.module.css';

export const DashboardView = () => {
  return (
    <div className={styles.dashboardView}>
      <ul className={styles.projects}>
        <li>
          <Link to="/projects/svalyn">S.V.A.L.Y.N</Link>
        </li>
        <li>
          <Link to="/projects/homer">H.O.M.E.R</Link>
        </li>
        <li>
          <Link to="/projects/plato">P.L.A.T.O</Link>
        </li>
        <li>
          <Link to="/projects/virgil">V.I.R.G.I.L</Link>
        </li>
        <li>
          <Link to="/projects/jokasta">J.O.K.A.S.T.A</Link>
        </li>
        <li>
          <Link to="/projects/friday">F.R.I.D.A.Y</Link>
        </li>
        <li>
          <Link to="/projects/jarvis">J.A.R.V.I.S</Link>
        </li>
        <li>
          <Link to="/projects/helen">H.E.L.E.N</Link>
        </li>
      </ul>
    </div>
  );
};

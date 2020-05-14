/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { gql } from 'graphql.macro';

import styles from './DashboardView.module.css';

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

export const DashboardView = () => {
  const initialState = { loading: true, projects: [] };
  const [{ projects }, setState] = useState(initialState);

  useEffect(() => {
    const fetchProjects = async () => {
      const body = JSON.stringify({
        query,
      });
      const response = await fetch('http://localhost:8080/api/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body,
      });
      const json = await response.json();
      const {
        data: { projects },
      } = json;
      setState({ loading: false, projects });
    };

    fetchProjects();
  }, []);

  return (
    <div className={styles.dashboardView}>
      <ul className={styles.projects}>
        {projects.map((project) => {
          return (
            <li key={project.id}>
              <Link to={`/projects/${project.id}`}>{project.label}</Link>
            </li>
          );
        })}
      </ul>
    </div>
  );
};

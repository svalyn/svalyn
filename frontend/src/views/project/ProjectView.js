/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import styles from './ProjectView.module.css';

export const ProjectView = () => {
  const { projectId } = useParams();
  const initialState = {
    id: projectId,
    label: projectId,
    assessments: [],
  };
  const [{ id, label, assessments }, setState] = useState(initialState);

  const onNewAssessmentClick = () =>
    setState((prevState) => {
      const value = Date.now().toString();
      const newAssessment = { id: value, label: value };
      return { ...prevState, assessments: [...prevState.assessments, newAssessment] };
    });

  return (
    <div className={styles.projectView}>
      <h1>
        {id} - {label}
      </h1>
      <button onClick={onNewAssessmentClick}>New assessment</button>
      <ul>
        {assessments.map((assessment) => (
          <li key={assessment.id}>
            <Link to={`/projects/${projectId}/assessments/${assessment.id}`}>{assessment.label}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

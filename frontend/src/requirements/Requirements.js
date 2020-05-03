/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState } from 'react';
import PropTypes from 'prop-types';

import { requirementPropTypes } from '../propTypes/propTypes';
import { Tests } from '../tests/Tests';

import styles from './Requirements.module.css';

const propTypes = {
  requirements: PropTypes.arrayOf(requirementPropTypes).isRequired,
};
export const Requirements = ({ requirements }) => {
  const initialState = {
    selectedRequirement: null,
  };
  const [state, setState] = useState(initialState);

  const onRequirementclick = (selectedRequirement) => setState({ selectedRequirement });
  const { selectedRequirement } = state;
  return (
    <div className={styles.requirements}>
      <LeftPanel requirements={requirements} onRequirementclick={onRequirementclick} />
      {selectedRequirement ? <RightPanel requirement={selectedRequirement} /> : <EmptyRightPanel />}
    </div>
  );
};
Requirements.propTypes = propTypes;

const LeftPanel = ({ requirements, onRequirementclick }) => {
  return (
    <ul className={styles.master}>
      {requirements.map((requirement) => (
        <li className={styles.entry} key={requirement.id} onClick={() => onRequirementclick(requirement)}>
          {requirement.label}
        </li>
      ))}
    </ul>
  );
};

const RightPanel = ({ requirement }) => {
  return (
    <div className={styles.details}>
      {requirement.description}
      <Tests tests={requirement.tests} />
    </div>
  );
};

const EmptyRightPanel = () => {
  return <div>Please select a requirement</div>;
};

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import PropTypes from 'prop-types';

import { testPropTypes } from '../propTypes/propTypes';

const Test = ({ test }) => {
  const { id, label, description } = test;
  return (
    <div>
      <h3>{label}</h3>
      <p>{description}</p>
      <div>
        <input type="radio" id={`${id}-success`} name={`${id}-status`} value="success" />
        <label htmlFor={`${id}-success`}>Success</label>
        <input type="radio" id={`${id}-failure`} name={`${id}-status`} value="failure" />
        <label htmlFor={`${id}-failure`}>Failure</label>
      </div>
    </div>
  );
};

const propTypes = {
  tests: PropTypes.arrayOf(testPropTypes).isRequired,
};
export const Tests = ({ tests }) => {
  return (
    <ul>
      {tests.map((test) => (
        <li key={test.id}>
          <Test test={test} />
        </li>
      ))}
    </ul>
  );
};
Tests.propTypes = propTypes;

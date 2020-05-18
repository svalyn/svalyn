/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import PropTypes from 'prop-types';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import Typography from '@material-ui/core/Typography';
import { testPropTypes } from '../propTypes/propTypes';

const Test = ({ test }) => {
  const { label, description } = test;
  return (
    <div>
      <Typography variant="h4" gutterBottom>
        {label}
      </Typography>
      <Typography>{description}</Typography>
      <FormGroup row>
        <RadioGroup aria-label="status" name="status">
          <FormControlLabel value="success" control={<Radio />} label="Success" />
          <FormControlLabel value="failure" control={<Radio />} label="Failure" />
        </RadioGroup>
      </FormGroup>
    </div>
  );
};

const propTypes = {
  tests: PropTypes.arrayOf(testPropTypes).isRequired,
};
export const Tests = ({ tests }) => {
  return (
    <List>
      {tests.map((test) => (
        <ListItem key={test.id}>
          <Test test={test} />
        </ListItem>
      ))}
    </List>
  );
};
Tests.propTypes = propTypes;

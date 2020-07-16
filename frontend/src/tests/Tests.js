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
import { makeStyles, withStyles } from '@material-ui/core/styles';
import { gql } from 'graphql.macro';
import { ajax } from 'rxjs/ajax';

import { testPropTypes } from '../propTypes/propTypes';

const {
  loc: {
    source: { body: query },
  },
} = gql`
  mutation updateTest($input: UpdateTestInput!) {
    updateAssessment: updateTest(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
      ... on UpdateTestSuccessPayload {
        assessment {
          id
          label
          createdOn
          lastModifiedOn
          success
          failure
          testCount
          status
          categories {
            id
            label
            description
            requirements {
              id
              label
              description
              tests {
                id
                label
                description
                status
              }
            }
          }
        }
      }
    }
  }
`;

const useStyles = makeStyles((theme) => ({
  steps: {
    marginTop: '1rem',
    marginBottom: '1rem',
  },
}));

const updateTest = (variables) =>
  ajax({
    url: '/api/graphql',
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ query, variables }),
  });

const SuccessRadio = withStyles((theme) => ({
  root: {
    color: theme.palette.grey[400],
    '&$checked': {
      color: theme.palette.success[theme.palette.type],
    },
  },
  checked: {},
}))((props) => <Radio color="default" {...props} />);

const FailureRadio = withStyles((theme) => ({
  root: {
    color: theme.palette.grey[400],
    '&$checked': {
      color: theme.palette.error[theme.palette.type],
    },
  },
  checked: {},
}))((props) => <Radio color="default" {...props} />);

const testComponentPropTypes = {
  assessmentId: PropTypes.string.isRequired,
  test: testPropTypes.isRequired,
  onAssessmentUpdated: PropTypes.func.isRequired,
};
const Test = ({ assessmentId, assessmentStatus, test, onAssessmentUpdated }) => {
  const classes = useStyles();
  const { id, label, description, steps, status } = test;

  const onChange = async (event) => {
    const { value } = event.target;

    const variables = {
      input: {
        assessmentId,
        testId: id,
        status: value,
      },
    };
    updateTest(variables).subscribe((ajaxResponse) => onAssessmentUpdated(ajaxResponse));
  };

  let stepsElement = null;
  if (steps) {
    stepsElement = (
      <ul className={classes.steps}>
        {steps.map((step, index) => (
          <li key={index}>
            <Typography>{step}</Typography>
          </li>
        ))}
      </ul>
    );
  }

  return (
    <div>
      <Typography variant="h4" gutterBottom>
        {label}
      </Typography>
      <Typography>{description}</Typography>
      {stepsElement}
      <FormGroup row>
        <RadioGroup aria-label="status" name={`status-${id}`} value={status} onChange={onChange}>
          <FormControlLabel
            value="SUCCESS"
            control={<SuccessRadio data-testid={`${label}-success`} />}
            label="Success"
            disabled={assessmentStatus === 'CLOSED'}
          />
          <FormControlLabel
            value="FAILURE"
            control={<FailureRadio data-testid={`${label}-failure`} />}
            label="Failure"
            disabled={assessmentStatus === 'CLOSED'}
          />
        </RadioGroup>
      </FormGroup>
    </div>
  );
};
Test.propTypes = testComponentPropTypes;

const testsComponentPropTypes = {
  assessmentId: PropTypes.string.isRequired,
  tests: PropTypes.arrayOf(testPropTypes).isRequired,
  onAssessmentUpdated: PropTypes.func.isRequired,
};
export const Tests = ({ assessmentId, assessmentStatus, tests, onAssessmentUpdated }) => {
  return (
    <List>
      {tests.map((test) => (
        <ListItem key={test.id}>
          <Test
            assessmentId={assessmentId}
            assessmentStatus={assessmentStatus}
            test={test}
            onAssessmentUpdated={onAssessmentUpdated}
          />
        </ListItem>
      ))}
    </List>
  );
};
Tests.propTypes = testsComponentPropTypes;

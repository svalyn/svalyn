/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

import { requirementPropTypes } from '../propTypes/propTypes';
import { Tests } from '../tests/Tests';

const useStyles = makeStyles((theme) => ({
  requirements: {
    display: 'grid',
    gridTemplateColumns: '30% 70%',
    gridTemplateRows: '1fr',
  },
  details: {
    padding: '16px',
  },
  rightPanel: {
    borderLeft: '1px solid rgba(255, 255, 255, 0.12)',
  },
}));

const propTypes = {
  requirements: PropTypes.arrayOf(requirementPropTypes).isRequired,
};
export const Requirements = ({ requirements }) => {
  const classes = useStyles();
  const initialState = {
    selectedRequirement: null,
  };
  const [{ selectedRequirement }, setState] = useState(initialState);

  useEffect(() => {
    const selectedRequirement = requirements[0];
    setState({ selectedRequirement });
  }, [requirements]);

  const onRequirementclick = (selectedRequirement) => setState({ selectedRequirement });
  return (
    <div className={classes.requirements}>
      <LeftPanel requirements={requirements} onRequirementclick={onRequirementclick} />
      {selectedRequirement ? <RightPanel requirement={selectedRequirement} /> : <EmptyRightPanel />}
    </div>
  );
};
Requirements.propTypes = propTypes;

const LeftPanel = ({ requirements, onRequirementclick }) => {
  return (
    <List>
      {requirements.map((requirement) => (
        <ListItem button onClick={() => onRequirementclick(requirement)} key={requirement.id}>
          <ListItemText primary={requirement.label} />
        </ListItem>
      ))}
    </List>
  );
};

const RightPanel = ({ requirement }) => {
  const classes = useStyles();
  return (
    <div className={classes.rightPanel}>
      <div className={classes.details}>
        <Typography variant="h3" gutterBottom>
          {requirement.label}
        </Typography>
        <Typography>{requirement.description}</Typography>
      </div>
      <Tests tests={requirement.tests} />
    </div>
  );
};

const EmptyRightPanel = () => {
  return <div>Please select a requirement</div>;
};

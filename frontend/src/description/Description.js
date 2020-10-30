/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

import { categoryPropTypes } from '../propTypes/propTypes';

const useStyles = makeStyles((theme) => ({
  description: {
    padding: '24px 16px',
  },
}));

const propTypes = {
  category: categoryPropTypes.isRequired,
};
export const Description = ({ category }) => {
  const classes = useStyles();
  return (
    <div className={classes.description}>
      <Typography variant="h2" gutterBottom>
        {category.label}
      </Typography>
      <Typography gutterBottom>{category.details}</Typography>
    </div>
  );
};
Description.propTypes = propTypes;

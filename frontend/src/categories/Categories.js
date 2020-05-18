/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import PropTypes from 'prop-types';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';

import { categoryPropTypes } from '../propTypes/propTypes';

const propTypes = {
  categories: PropTypes.arrayOf(categoryPropTypes).isRequired,
  onCategoryClick: PropTypes.func.isRequired,
};
export const Categories = ({ categories, onCategoryClick }) => {
  return (
    <List>
      {categories.map((category) => {
        return (
          <ListItem button onClick={() => onCategoryClick(category)} key={category.id}>
            <ListItemText primary={category.label} />
          </ListItem>
        );
      })}
    </List>
  );
};
Categories.propTypes = propTypes;

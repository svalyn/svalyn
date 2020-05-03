/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import PropTypes from 'prop-types';

import { categoryPropTypes } from '../propTypes/propTypes';

import styles from './Categories.module.css';

const propTypes = {
  categories: PropTypes.arrayOf(categoryPropTypes).isRequired,
  onCategoryClick: PropTypes.func.isRequired,
};
export const Categories = ({ categories, onCategoryClick }) => {
  return (
    <ul className={styles.categories}>
      {categories.map((category) => (
        <li className={styles.category} key={category.id} onClick={() => onCategoryClick(category)}>
          {category.label}
        </li>
      ))}
    </ul>
  );
};
Categories.propTypes = propTypes;

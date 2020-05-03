/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';

import { categoryPropTypes } from '../propTypes/propTypes';

const propTypes = {
  category: categoryPropTypes.isRequired,
};
export const Description = ({ category }) => {
  return <div>{category.description}</div>;
};
Description.propTypes = propTypes;

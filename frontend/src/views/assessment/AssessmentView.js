/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState } from 'react';

import { Categories } from '../../categories/Categories';
import { Description } from '../../description/Description';
import { Requirements } from '../../requirements/Requirements';

import { assessment } from '../../data/data';

import styles from './AssessmentView.module.css';

export const AssessmentView = () => {
  const initialState = {
    selectedCategory: null,
  };
  const [state, setState] = useState(initialState);

  const onCategoryClick = (selectedCategory) => setState({ selectedCategory });
  const { selectedCategory } = state;
  return (
    <div className={styles.assessmentView}>
      <LeftPanel categories={assessment.categories} onCategoryClick={onCategoryClick} />
      {selectedCategory ? <RightPanel category={selectedCategory} /> : <EmptyRightPanel />}
    </div>
  );
};

const LeftPanel = ({ categories, onCategoryClick }) => {
  return (
    <div className={styles.leftPanel}>
      <Categories categories={categories} onCategoryClick={onCategoryClick} />
    </div>
  );
};

const RightPanel = ({ category }) => {
  return (
    <div className={styles.rightPanel}>
      <div className={styles.description}>
        <Description category={category} />
      </div>
      <div className={styles.requirements}>
        <Requirements requirements={category.requirements} />
      </div>
    </div>
  );
};

const EmptyRightPanel = () => {
  return <div>Please select a category</div>;
};

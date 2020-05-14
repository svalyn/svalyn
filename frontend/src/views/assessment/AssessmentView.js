/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { gql } from 'graphql.macro';

import { Categories } from '../../categories/Categories';
import { Description } from '../../description/Description';
import { Requirements } from '../../requirements/Requirements';

import styles from './AssessmentView.module.css';

const {
  loc: {
    source: { body: query },
  },
} = gql`
  query getAssessment($projectId: ID!, $assessmentId: ID!) {
    project(projectId: $projectId) {
      assessment(assessmentId: $assessmentId) {
        id
        label
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
`;

export const AssessmentView = () => {
  const { projectId, assessmentId } = useParams();

  const initialState = {
    loading: true,
    assessment: null,
    selectedCategory: null,
  };
  const [{ assessment, selectedCategory }, setState] = useState(initialState);

  useEffect(() => {
    const fetchProjects = async () => {
      const body = JSON.stringify({
        query,
        variables: {
          projectId,
          assessmentId,
        },
      });
      const response = await fetch('http://localhost:8080/api/graphql', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body,
      });
      const json = await response.json();
      const {
        data: {
          project: { assessment },
        },
      } = json;
      setState({ loading: false, assessment, selectedCategory: assessment?.categories[0] });
    };

    fetchProjects();
  }, [projectId, assessmentId]);

  const onCategoryClick = (selectedCategory) => setState((prevState) => ({ ...prevState, selectedCategory }));
  return (
    <div className={styles.assessmentView}>
      <LeftPanel categories={assessment?.categories ?? []} onCategoryClick={onCategoryClick} />
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

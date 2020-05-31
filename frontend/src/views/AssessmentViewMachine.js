/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const assessmentViewMachine = Machine(
  {
    id: 'AssessmentView',
    initial: 'idle',
    context: {
      label: null,
      assessment: null,
      selectedCategoryId: null,
    },
    states: {
      idle: {
        on: {
          FETCH: 'loading',
        },
      },
      loading: {
        on: {
          HANDLE_RESPONSE: [
            {
              cond: 'isError',
              target: 'error',
            },
            {
              cond: 'isMissing',
              target: 'missing',
            },
            {
              cond: 'isEmpty',
              target: 'empty',
              actions: ['updateAssessment'],
            },
            {
              target: 'success',
              actions: ['updateAssessment'],
            },
          ],
        },
      },
      missing: {
        type: 'final',
      },
      empty: {
        type: 'final',
      },
      error: {
        type: 'final',
      },
      success: {
        on: {
          REFRESH_ASSESSMENT: {
            target: 'success',
            actions: ['refreshAssessment'],
          },
          SELECT_CATEGORY: {
            target: 'success',
            actions: ['selectCategory'],
          },
        },
      },
    },
  },
  {
    guards: {
      isError: (_, event) => {
        const {
          ajaxResponse: { response, status },
        } = event;
        return status !== 200 || response.errors;
      },
      isMissing: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return !response.data.project?.assessment;
      },
      isEmpty: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return response.data.project.assessment.categories.length === 0;
      },
    },
    actions: {
      updateAssessment: assign((_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        const {
          project: { label, assessment },
        } = response.data;
        return {
          label,
          assessment,
          selectedCategoryId: assessment?.categories[0]?.id,
        };
      }),
      refreshAssessment: assign((_, event) => {
        const { assessment } = event;
        return { assessment };
      }),
      selectCategory: assign((_, event) => {
        const { selectedCategory } = event;
        return { selectedCategoryId: selectedCategory.id };
      }),
    },
  }
);

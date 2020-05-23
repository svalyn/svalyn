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
              target: 'success',
              actions: ['updateAssessment'],
            },
          ],
        },
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
      error: {
        type: 'final',
      },
    },
  },
  {
    guards: {
      isError: (_, event) => {
        const { response } = event;
        return !!response?.error || !response?.data?.project?.assessment;
      },
    },
    actions: {
      updateAssessment: assign((_, event) => {
        const { response } = event;
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

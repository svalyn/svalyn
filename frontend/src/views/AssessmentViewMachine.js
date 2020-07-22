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
    type: 'parallel',
    context: {
      label: null,
      assessment: null,
      selectedCategoryId: null,
      message: null,
    },
    states: {
      toast: {
        initial: 'hidden',
        states: {
          hidden: {
            on: {
              SHOW_TOAST: {
                target: 'visible',
                actions: 'setMessage',
              },
            },
          },
          visible: {
            on: {
              HIDE_TOAST: {
                target: 'hidden',
                actions: 'clearMessage',
              },
            },
          },
        },
      },
      assessmentView: {
        initial: 'idle',
        states: {
          idle: {
            on: {
              FETCH: 'loading',
            },
          },
          unauthorized: {},
          loading: {
            on: {
              HANDLE_RESPONSE: [
                {
                  cond: 'isUnauthorized',
                  target: 'unauthorized',
                },
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
    },
  },
  {
    guards: {
      isUnauthorized: (_, event) => {
        const {
          ajaxResponse: { status },
        } = event;
        return status === 401;
      },
      isError: (_, event) => {
        const {
          ajaxResponse: { response, status },
        } = event;
        return status !== 200 || response?.errors;
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
      setMessage: assign((_, event) => {
        const { message } = event;
        return { message };
      }),
      clearMessage: assign((_, event) => {
        return { message: null };
      }),
    },
  }
);

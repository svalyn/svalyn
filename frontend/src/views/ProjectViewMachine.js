/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

const newAssessmentFormMachine = {
  initial: 'pristine',
  states: {
    pristine: {
      on: {
        UPDATE_LABEL: [
          {
            cond: 'isNewAssessmentLabelInvalid',
            target: 'invalid',
            actions: ['updateNewAssessmentLabel'],
          },
          {
            target: 'valid',
            actions: ['updateNewAssessmentLabel'],
          },
        ],
      },
    },
    invalid: {
      on: {
        UPDATE_LABEL: [
          {
            cond: 'isNewAssessmentLabelInvalid',
            target: 'invalid',
            actions: ['updateNewAssessmentLabel'],
          },
          {
            target: 'valid',
            actions: ['updateNewAssessmentLabel'],
          },
        ],
      },
    },
    valid: {
      on: {
        UPDATE_LABEL: [
          {
            cond: 'isNewAssessmentLabelInvalid',
            target: 'invalid',
            actions: ['updateNewAssessmentLabel'],
          },
          {
            target: 'valid',
            actions: ['updateNewAssessmentLabel'],
          },
        ],
      },
    },
  },
};

export const projectViewMachine = Machine(
  {
    id: 'ProjectView',
    initial: 'idle',
    context: {
      label: '',
      assessments: [],
      newAssessmentLabel: '',
    },
    states: {
      idle: {
        on: {
          FETCH_PROJECT: 'fetchingProject',
        },
      },
      fetchingProject: {
        on: {
          HANDLE_PROJECT_RESPONSE: [
            {
              cond: 'isProjectFetchingError',
              target: 'projectFetchedError',
            },
            {
              cond: 'isProjectFetchingEmpty',
              target: 'projectEmpty',
              actions: ['updateProject'],
            },
            {
              target: 'projectFetchedSuccess',
              actions: ['updateProject'],
            },
          ],
        },
      },
      projectEmpty: {
        ...newAssessmentFormMachine,
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
            actions: ['clearAssessmentCreationForm'],
          },
        },
      },
      projectFetchedSuccess: {
        ...newAssessmentFormMachine,
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
            actions: ['clearAssessmentCreationForm'],
          },
        },
      },
      projectFetchedError: {
        ...newAssessmentFormMachine,
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
            actions: ['clearAssessmentCreationForm'],
          },
        },
      },
    },
  },
  {
    guards: {
      isNewAssessmentLabelInvalid: (_, event) => {
        const { label } = event;
        return (label?.length ?? 0) === 0;
      },
      isProjectFetchingError: (_, event) => {
        const { response } = event;
        return !!response?.error;
      },
      isProjectFetchingEmpty: (_, event) => {
        const { response } = event;
        return (response?.data?.project?.assessments?.length ?? 0) === 0;
      },
    },
    actions: {
      updateNewAssessmentLabel: assign((_, event) => {
        const { newAssessmentLabel } = event;
        return { newAssessmentLabel };
      }),
      clearAssessmentCreationForm: assign((_, event) => {
        return { newAssessmentLabel: '' };
      }),
      updateProject: assign((_, event) => {
        const { response } = event;
        const { label, assessments } = response.data.project;
        return {
          label,
          assessments,
        };
      }),
    },
  }
);

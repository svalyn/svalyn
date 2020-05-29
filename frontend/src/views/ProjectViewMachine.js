/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const newAssessmentFormMachine = Machine(
  {
    initial: 'pristine',
    context: {
      label: '',
      descriptionId: '',
    },
    states: {
      pristine: {
        on: {
          UPDATE_DESCRIPTION: {
            target: 'invalid',
            actions: ['updateDescription'],
          },
          UPDATE_LABEL: [
            {
              cond: 'isLabelInvalid',
              target: 'invalid',
              actions: ['updateLabel'],
            },
            {
              target: 'valid',
              actions: ['updateLabel'],
            },
          ],
        },
      },
      invalid: {
        on: {
          UPDATE_DESCRIPTION: {
            target: 'invalid',
            actions: ['updateDescription'],
          },
          UPDATE_LABEL: [
            {
              cond: 'isLabelInvalid',
              target: 'invalid',
              actions: ['updateLabel'],
            },
            {
              target: 'valid',
              actions: ['updateLabel'],
            },
          ],
        },
      },
      valid: {
        on: {
          UPDATE_DESCRIPTION: {
            target: 'valid',
            actions: ['updateDescription'],
          },
          UPDATE_LABEL: [
            {
              cond: 'isLabelInvalid',
              target: 'invalid',
              actions: ['updateLabel'],
            },
            {
              target: 'valid',
              actions: ['updateLabel'],
            },
          ],
          on: {
            CREATE_ASSESSMENT: {
              target: 'pristine',
              actions: ['clearForm'],
            },
          },
        },
      },
    },
  },
  {
    guards: {
      isLabelInvalid: (_, event) => {
        const { label } = event;
        return (label?.length ?? 0) === 0;
      },
    },
    actions: {
      updateDescription: assign((_, event) => {
        const { descriptionId } = event;
        return { descriptionId };
      }),
      updateLabel: assign((_, event) => {
        const { label } = event;
        return { label };
      }),
      clearForm: assign((_, event) => {
        return { label: '' };
      }),
    },
  }
);

export const projectViewMachine = Machine(
  {
    id: 'ProjectView',
    initial: 'idle',
    context: {
      label: '',
      assessments: [],
      descriptions: [{ id: '', label: '' }],
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
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
      projectFetchedSuccess: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
      projectFetchedError: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
    },
  },
  {
    guards: {
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
      updateProject: assign((_, event) => {
        const { response } = event;
        const { descriptions, project } = response.data;
        const { label, assessments } = project;
        return {
          descriptions,
          label,
          assessments,
        };
      }),
    },
  }
);

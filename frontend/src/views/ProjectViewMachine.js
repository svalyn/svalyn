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
          CREATE_ASSESSMENT: {
            target: 'pristine',
            actions: ['clearForm'],
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
      anchorElement: null,
      assessmentId: null,
    },
    states: {
      idle: {
        on: {
          FETCH_PROJECT: 'fetchingProject',
        },
      },
      fetchingProject: {
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
              actions: ['updateProject'],
            },
            {
              target: 'success',
              actions: ['updateProject'],
            },
          ],
        },
      },
      error: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
      missing: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
      empty: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
        },
      },
      success: {
        on: {
          CREATE_ASSESSMENT: {
            target: 'fetchingProject',
          },
          OPEN_MENU: {
            target: 'menuOpened',
            actions: ['openMenu'],
          },
        },
      },
      menuOpened: {
        on: {
          CLOSE_MENU: {
            target: 'success',
            actions: ['closeMenu'],
          },
          DELETE_ASSESSMENT: {
            target: 'fetchingProject',
            actions: ['closeMenu'],
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
        return !response.data.project;
      },
      isEmpty: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return (response.data.project?.assessments?.length ?? 0) === 0;
      },
    },
    actions: {
      updateProject: assign((_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        const { descriptions, project } = response.data;
        const { label, assessments } = project;
        return {
          descriptions,
          label,
          assessments,
        };
      }),
      openMenu: assign((_, event) => {
        const {
          anchorElement,
          assessment: { id },
        } = event;
        return { anchorElement, assessmentId: id };
      }),
      closeMenu: assign((_, event) => {
        return { anchorElement: null, assessmentId: null };
      }),
    },
  }
);

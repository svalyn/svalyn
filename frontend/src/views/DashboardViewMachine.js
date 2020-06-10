/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const newProjectFormMachine = Machine(
  {
    initial: 'pristine',
    context: {
      label: '',
    },
    states: {
      pristine: {
        on: {
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
          CREATE_PROJECT: {
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

export const dashboardViewMachine = Machine(
  {
    id: 'DashboardView',
    initial: 'idle',
    context: {
      projects: [],
      anchorElement: null,
      projectId: null,
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
              cond: 'isEmpty',
              target: 'empty',
            },
            {
              target: 'success',
              actions: ['updateProjects'],
            },
          ],
        },
      },
      empty: {
        on: {
          CREATE_PROJECT: {
            target: 'loading',
          },
        },
      },
      success: {
        on: {
          CREATE_PROJECT: {
            target: 'loading',
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
          DELETE_PROJECT: {
            target: 'loading',
            actions: ['closeMenu'],
          },
        },
      },
      error: {
        on: {
          CREATE_PROJECT: {
            target: 'loading',
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
      isEmpty: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return (response?.data?.projects?.length ?? 0) === 0;
      },
    },
    actions: {
      updateProjects: assign((_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        const { projects } = response.data;
        return {
          projects,
        };
      }),
      openMenu: assign((_, event) => {
        const {
          anchorElement,
          project: { id },
        } = event;
        return { anchorElement, projectId: id };
      }),
      closeMenu: assign(() => {
        return { anchorElement: null, projectId: null };
      }),
    },
  }
);

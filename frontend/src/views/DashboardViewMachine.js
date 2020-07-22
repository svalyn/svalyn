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
    type: 'parallel',
    context: {
      projects: [],
      pageCount: 0,
      hasPreviousPage: false,
      hasNextPage: false,
      anchorElement: null,
      projectId: null,
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
      dashboardView: {
        initial: 'idle',
        states: {
          idle: {
            on: {
              FETCH: 'fetchingProjects',
            },
          },
          unauthorized: {},
          fetchingProjects: {
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
                  cond: 'isEmpty',
                  target: 'empty',
                  actions: ['updateProjects'],
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
                target: 'fetchingProjects',
              },
            },
          },
          success: {
            on: {
              CREATE_PROJECT: {
                target: 'fetchingProjects',
              },
              OPEN_MENU: {
                target: 'menuOpened',
                actions: ['openMenu'],
              },
              FETCH: 'fetchingProjects',
            },
          },
          menuOpened: {
            on: {
              CLOSE_MENU: {
                target: 'success',
                actions: ['closeMenu'],
              },
              DELETE_PROJECT: {
                target: 'fetchingProjects',
                actions: ['closeMenu'],
              },
            },
          },
          error: {
            on: {
              CREATE_PROJECT: {
                target: 'fetchingProjects',
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
      isEmpty: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return (response?.data?.projects?.edges.length ?? 0) === 0;
      },
    },
    actions: {
      updateProjects: assign((_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        const { projects } = response.data;
        const { pageCount, hasPreviousPage, hasNextPage } = projects.pageInfo;
        return {
          projects: projects.edges.map((edge) => edge.node),
          pageCount,
          hasPreviousPage,
          hasNextPage,
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

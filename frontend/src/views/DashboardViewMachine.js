/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const dashboardViewMachine = Machine(
  {
    id: 'DashboardView',
    initial: 'idle',
    context: {
      projects: [],
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
        type: 'final',
      },
      success: {
        type: 'final',
      },
      error: {
        type: 'final',
      },
    },
  },
  {
    guards: {
      isEmpty: (_, event) => {
        const { response } = event;
        return (response?.data?.projects?.length ?? 0) === 0;
      },
      isError: (_, event) => {
        const { response } = event;
        return !!response?.error;
      },
    },
    actions: {
      updateProjects: assign((_, event) => {
        const { response } = event;
        const { projects } = response.data;
        return {
          projects,
        };
      }),
    },
  }
);

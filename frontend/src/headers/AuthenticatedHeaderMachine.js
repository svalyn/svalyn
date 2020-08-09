/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const authenticatedHeaderMachine = Machine(
  {
    initial: 'idle',
    context: {
      principal: null,
      anchorElement: null,
    },
    states: {
      idle: {
        on: {
          FETCH: 'fetching',
        },
      },
      fetching: {
        on: {
          HANDLE_RESPONSE: [
            {
              cond: 'isError',
              target: 'error',
            },
            {
              target: 'success',
              actions: 'updatePrincipal',
            },
          ],
        },
      },
      error: {},
      success: {
        on: {
          OPEN_MENU: {
            target: 'menuOpened',
            actions: 'openMenu',
          },
        },
      },
      menuOpened: {
        on: {
          CLOSE_MENU: {
            target: 'success',
            actions: 'closeMenu',
          },
          LOGGED_OUT: {
            target: 'loggedOut',
          },
        },
      },
      loggedOut: {},
    },
  },
  {
    guards: {
      isError: (_, event) => {
        const {
          ajaxResponse: { response, status },
        } = event;
        return status !== 200 || response?.errors;
      },
    },
    actions: {
      updatePrincipal: assign((_, event) => {
        const {
          ajaxResponse: {
            response: { data },
          },
        } = event;
        const { principal } = data;
        return { principal };
      }),
      openMenu: assign((_, event) => {
        const { anchorElement } = event;
        return { anchorElement };
      }),
      closeMenu: assign((_, event) => {
        return { anchorElement: null };
      }),
    },
  }
);

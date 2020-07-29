/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import { assign, Machine } from 'xstate';

export const newAccountViewMachine = Machine(
  {
    initial: 'pristine',
    context: {
      username: '',
      password: '',
      message: null,
    },
    states: {
      pristine: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updateUsername',
            },
            {
              target: 'valid',
              actions: 'updateUsername',
            },
          ],
          UPDATE_PASSWORD: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updatePassword',
            },
            {
              target: 'valid',
              actions: 'updatePassword',
            },
          ],
        },
      },
      invalid: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updateUsername',
            },
            {
              target: 'valid',
              actions: 'updateUsername',
            },
          ],
          UPDATE_PASSWORD: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updatePassword',
            },
            {
              target: 'valid',
              actions: 'updatePassword',
            },
          ],
        },
      },
      valid: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updateUsername',
            },
            {
              target: 'valid',
              actions: 'updateUsername',
            },
          ],
          UPDATE_PASSWORD: [
            {
              cond: 'areCredentialsInvalid',
              target: 'invalid',
              actions: 'updatePassword',
            },
            {
              target: 'valid',
              actions: 'updatePassword',
            },
          ],
          HANDLE_RESPONSE: [
            {
              cond: 'isUnauthorized',
              target: 'valid',
              actions: 'displayError',
            },
            {
              cond: 'isError',
              target: 'valid',
              actions: 'displayError',
            },
            {
              target: 'authenticated',
            },
          ],
          CLEAR_ERROR: [
            {
              actions: 'clearError',
            },
          ],
        },
      },
      authenticated: {},
    },
  },
  {
    guards: {
      areCredentialsInvalid: (context, event) => {
        let valid = true;
        if (event.type === 'UPDATE_USERNAME') {
          valid = valid && event.username.length > 0;
          valid = valid && context.password.length >= 10;
        } else if (event.type === 'UPDATE_PASSWORD') {
          valid = valid && context.username.length > 0;
          valid = valid && event.password.length >= 10;
        }
        return !valid;
      },
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
    },
    actions: {
      updateUsername: assign((_, event) => {
        const { username } = event;
        return { username, message: null };
      }),
      updatePassword: assign((_, event) => {
        const { password } = event;
        return { password, message: null };
      }),
      displayError: assign(() => {
        return { message: 'Username already taken' };
      }),
      clearError: assign(() => {
        return { message: null };
      }),
    },
  }
);

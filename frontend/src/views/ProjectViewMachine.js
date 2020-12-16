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

export const ownerFormMachine = Machine(
  {
    initial: 'pristine',
    context: {
      ownerId: null,
      selectedOwnerId: '',
    },
    states: {
      pristine: {
        on: {
          UPDATE_OWNER: [
            {
              cond: 'isNewOwner',
              target: 'valid',
              actions: ['updateOwner'],
            },
            {
              target: 'pristine',
              actions: ['updateOwner'],
            },
          ],
        },
      },
      valid: {
        on: {
          UPDATE_OWNER: [
            {
              cond: 'isNewOwner',
              target: 'valid',
              actions: ['updateOwner'],
            },
            {
              target: 'pristine',
              actions: ['updateOwner'],
            },
          ],
        },
      },
    },
  },
  {
    guards: {
      isNewOwner: (context, event) => {
        const { ownerId } = context;
        const { selectedOwnerId } = event;
        return ownerId !== selectedOwnerId;
      },
    },
    actions: {
      updateOwner: assign((_, event) => {
        const { selectedOwnerId } = event;
        return { selectedOwnerId };
      }),
    },
  }
);

export const membersFormMachine = Machine(
  {
    initial: 'pristine',
    context: {
      username: '',
    },
    states: {
      pristine: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'isUsernameInvalid',
              target: 'invalid',
              actions: ['updateUsername'],
            },
            {
              target: 'valid',
              actions: ['updateUsername'],
            },
          ],
        },
      },
      invalid: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'isUsernameInvalid',
              target: 'invalid',
              actions: ['updateUsername'],
            },
            {
              target: 'valid',
              actions: ['updateUsername'],
            },
          ],
        },
      },
      valid: {
        on: {
          UPDATE_USERNAME: [
            {
              cond: 'isUsernameInvalid',
              target: 'invalid',
              actions: ['updateUsername'],
            },
            {
              target: 'valid',
              actions: ['updateUsername'],
            },
          ],
          ADD_MEMBER: {
            target: 'pristine',
            actions: ['clearForm'],
          },
        },
      },
    },
  },
  {
    guards: {
      isUsernameInvalid: (_, event) => {
        const { username } = event;
        return (username?.length ?? 0) === 0;
      },
    },
    actions: {
      updateUsername: assign((_, event) => {
        const { username } = event;
        return { username };
      }),
      clearForm: assign((_, event) => {
        return { username: '' };
      }),
    },
  }
);

export const projectViewMachine = Machine(
  {
    id: 'ProjectView',
    type: 'parallel',
    context: {
      page: 0,
      principal: null,
      label: '',
      ownedBy: null,
      members: [],
      assessments: [],
      selectedAssessmentIds: [],
      count: 0,
      descriptions: [{ id: '', label: '' }],
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
      projectView: {
        initial: 'idle',
        states: {
          idle: {
            on: {
              FETCH_PROJECT: 'fetchingProject',
            },
          },
          unauthorized: {},
          fetchingProject: {
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
              ADD_MEMBER: {
                target: 'fetchingProject',
              },
              REMOVE_MEMBER: {
                target: 'fetchingProject',
              },
              CHANGE_PROJECT_OWNER: {
                target: 'fetchingProject',
              },
            },
          },
          missing: {
            on: {
              CREATE_ASSESSMENT: {
                target: 'fetchingProject',
              },
              ADD_MEMBER: {
                target: 'fetchingProject',
              },
              REMOVE_MEMBER: {
                target: 'fetchingProject',
              },
              CHANGE_PROJECT_OWNER: {
                target: 'fetchingProject',
              },
            },
          },
          empty: {
            on: {
              CREATE_ASSESSMENT: {
                target: 'fetchingProject',
              },
              ADD_MEMBER: {
                target: 'fetchingProject',
              },
              REMOVE_MEMBER: {
                target: 'fetchingProject',
              },
              HANDLE_LEAVE_PROJECT_RESPONSE: {
                target: 'leftProject',
              },
              CHANGE_PROJECT_OWNER: {
                target: 'fetchingProject',
              },
            },
          },
          success: {
            on: {
              CREATE_ASSESSMENT: {
                target: 'fetchingProject',
              },
              ADD_MEMBER: {
                target: 'fetchingProject',
              },
              REMOVE_MEMBER: {
                target: 'fetchingProject',
              },
              FETCH_PROJECT: 'fetchingProject',
              CHANGE_PAGE: [
                {
                  target: 'fetchingProject',
                  actions: ['changePage'],
                },
              ],
              DELETE_ASSESSMENTS: {
                target: 'fetchingProject',
              },
              SELECT_ASSESSMENT: {
                actions: ['selectAssessment'],
              },
              SELECT_ALL_ASSESSMENTS: {
                actions: ['selectAllAssessments'],
              },
              HANDLE_LEAVE_PROJECT_RESPONSE: {
                target: 'leftProject',
              },
              CHANGE_PROJECT_OWNER: {
                target: 'fetchingProject',
              },
            },
          },
          leftProject: {},
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
        return !response.data.project;
      },
      isEmpty: (_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        return (response.data.project?.assessments?.edges.length ?? 0) === 0;
      },
    },
    actions: {
      updateProject: assign((_, event) => {
        const {
          ajaxResponse: { response },
        } = event;
        const { principal, project } = response.data;
        const { label, ownedBy, members, descriptions, assessments } = project;
        const { count } = assessments.pageInfo;
        return {
          descriptions,
          principal,
          label,
          ownedBy,
          members,
          assessments: assessments.edges.map((edge) => edge.node),
          selectedAssessmentIds: [],
          count,
        };
      }),
      changePage: assign((_, event) => {
        const { page } = event;
        return { page };
      }),
      setMessage: assign((_, event) => {
        const { message } = event;
        return { message };
      }),
      clearMessage: assign((_, event) => {
        return { message: null };
      }),
      selectAssessment: assign((context, event) => {
        const { selectedAssessmentIds } = context;
        const { assessmentId } = event;

        const index = selectedAssessmentIds.indexOf(assessmentId);
        if (index === -1) {
          return { selectedAssessmentIds: [...selectedAssessmentIds, assessmentId] };
        }
        return { selectedAssessmentIds: selectedAssessmentIds.filter((itemId) => itemId !== assessmentId) };
      }),
      selectAllAssessments: assign((context, event) => {
        const { target } = event;
        if (target.checked) {
          const { assessments } = context;
          return { selectedAssessmentIds: assessments.map((assessment) => assessment.id) };
        }
        return { selectedAssessmentIds: [] };
      }),
    },
  }
);

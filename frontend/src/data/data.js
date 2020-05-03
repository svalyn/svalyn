/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/

const requirements = (label) => [
  {
    id: 'work',
    label: label + ' should work',
    description: 'Everything should work all the time',
    tests: [
      { id: 'work-first', label: 'First', description: 'First test to perform' },
      { id: 'work-second', label: 'Second', description: 'Second test to perform' },
    ],
  },
  {
    id: 'fast',
    label: label + ' should be fast',
    description: 'It should feel fast, always',
    tests: [
      { id: 'fast-first', label: 'First', description: 'Yet another test to run' },
      { id: 'fast-second', label: 'Second', description: 'This one should be executed too' },
    ],
  },
  {
    id: 'understandable',
    label: label + ' should be understandable',
    description: 'The user interface should be easy to understand',
    tests: [
      { id: 'understandable-first', label: 'First', description: 'This one should be done' },
      { id: 'understandable-second', label: 'Second', description: 'We should not forget about that one too' },
    ],
  },
];

export const assessment = {
  id: '',
  label: '',
  categories: [
    {
      id: 'administration',
      label: 'Administration',
      description: 'Administration category description',
      requirements: requirements('Administration'),
    },
    {
      id: 'authentication',
      label: 'Authentication',
      description: 'Authentication category description',
      requirements: requirements('Authentication'),
    },
    {
      id: 'project',
      label: 'Project',
      description: 'Project category description',
      requirements: requirements('Project'),
    },
    {
      id: 'settings',
      label: 'Settings',
      description: 'Settings category description',
      requirements: requirements('Settings'),
    },
    { id: 'user', label: 'User', description: 'User category description', requirements: requirements('User') },
  ],
};

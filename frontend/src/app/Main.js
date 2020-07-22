/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import { Switch, Route, Redirect } from 'react-router-dom';

import { AssessmentView } from '../views/AssessmentView';
import { DashboardView } from '../views/DashboardView';
import { LoginView } from '../views/LoginView';
import { ProjectView } from '../views/ProjectView';

export const Main = () => {
  return (
    <Switch>
      <Route exact path="/" component={DashboardView} />
      <Route exact path="/login" component={LoginView} />
      <Route exact path="/projects/:projectId" component={ProjectView} />
      <Route exact path="/projects/:projectId/assessments/:assessmentId" component={AssessmentView} />
      <Redirect to="/" />
    </Switch>
  );
};

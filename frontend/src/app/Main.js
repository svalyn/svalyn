/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import { Switch, Route, Redirect } from 'react-router-dom';

import { AssessmentView } from '../views/assessment/AssessmentView';
import { DashboardView } from '../views/dashboard/DashboardView';
import { ProjectView } from '../views/project/ProjectView';

export const Main = () => {
  return (
    <Switch>
      <Route exact path="/" component={DashboardView} />
      <Route exact path="/projects/:projectId" component={ProjectView} />
      <Route exact path="/projects/:projectId/assessments/:assessmentId" component={AssessmentView} />
      <Redirect to="/" />
    </Switch>
  );
};

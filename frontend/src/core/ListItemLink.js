/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React, { useMemo, forwardRef } from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { Link as RouterLink } from 'react-router-dom';

export const ListItemLink = ({ icon, primary, secondary, to, disableTypography }) => {
  const renderLink = useMemo(() => {
    return forwardRef((itemProps, ref) => <RouterLink to={to} ref={ref} {...itemProps} />);
  }, [to]);

  return (
    <li>
      <ListItem button component={renderLink}>
        <ListItemIcon>{icon}</ListItemIcon>
        <ListItemText primary={primary} secondary={secondary} disableTypography={disableTypography} />
      </ListItem>
    </li>
  );
};

/**************************************************************
 * Copyright (c) Stéphane Bégaudeau
 *
 * This source code is licensed under the MIT license found in
 * the LICENSE file in the root directory of this source tree.
 ***************************************************************/
import React from 'react';
import Checkbox from '@material-ui/core/Checkbox';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Paper from '@material-ui/core/Paper';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TablePagination from '@material-ui/core/TablePagination';
import TableRow from '@material-ui/core/TableRow';
import Toolbar from '@material-ui/core/Toolbar';
import Tooltip from '@material-ui/core/Tooltip';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';

const useToolbarStyles = makeStyles((theme) => ({
  root: {
    paddingLeft: theme.spacing(2),
    paddingRight: theme.spacing(1),
  },
  title: {
    flex: '1 1 100%',
  },
}));

const EnhancedTableToolbar = ({ title, selectedItemsCount, onDelete }) => {
  const classes = useToolbarStyles();
  return (
    <Toolbar className={classes.root}>
      {selectedItemsCount > 0 ? (
        <Typography className={classes.title}>{selectedItemsCount} selected</Typography>
      ) : (
        <Typography className={classes.title} variant="h5">
          {title}
        </Typography>
      )}

      {selectedItemsCount > 0 ? (
        <Tooltip title="Delete">
          <IconButton aria-label="delete" data-testid="delete" onClick={onDelete}>
            <DeleteIcon />
          </IconButton>
        </Tooltip>
      ) : null}
    </Toolbar>
  );
};

const EnhancedTableHead = ({ headers, itemsCount, selectedItemsCount, onSelectAll }) => {
  return (
    <TableHead>
      <TableRow>
        <TableCell padding="checkbox">
          <Checkbox
            indeterminate={selectedItemsCount > 0 && selectedItemsCount < itemsCount}
            checked={itemsCount > 0 && selectedItemsCount === itemsCount}
            onChange={onSelectAll}
          />
        </TableCell>
        {headers.map((header) => (
          <TableCell key={header.label}>{header.label}</TableCell>
        ))}
      </TableRow>
    </TableHead>
  );
};

export const EnhancedTable = ({
  title,
  headers,
  items,
  selectedItemIds,
  itemPropertyAccessor,
  itemDataTestidProvider,
  totalItemsCount,
  onSelect,
  onSelectAll,
  page,
  itemsPerPage,
  onChangePage,
  onDelete,
}) => {
  return (
    <Paper>
      <EnhancedTableToolbar title={title} selectedItemsCount={selectedItemIds.length} onDelete={onDelete} />
      <TableContainer>
        <Table>
          <EnhancedTableHead
            headers={headers}
            itemsCount={items.length}
            selectedItemsCount={selectedItemIds.length}
            onSelectAll={onSelectAll}
          />
          <TableBody>
            {items.map((item) => {
              return (
                <TableRow hover role="checkbox" key={item.id}>
                  <TableCell padding="checkbox">
                    <Checkbox
                      onClick={(event) => onSelect(event, item.id)}
                      checked={selectedItemIds.indexOf(item.id) !== -1}
                      data-testid={`select-${itemDataTestidProvider(item)}`}
                    />
                  </TableCell>
                  {Array(headers.length)
                    .fill(null)
                    .map((_, index) => {
                      const property = itemPropertyAccessor(item, index);
                      return <TableCell key={index}>{property}</TableCell>;
                    })}
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        data-testid="pagination"
        component="div"
        rowsPerPageOptions={[]}
        rowsPerPage={itemsPerPage}
        count={totalItemsCount}
        page={page}
        onChangePage={onChangePage}
      />
    </Paper>
  );
};

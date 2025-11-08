import React from 'react';
import { Translate, translate } from 'react-jhipster';
import MenuItem from "app/shared/layout/menus/menu-item";
import { NavDropdown } from "app/shared/layout/menus/menu-components";


const ValidatorsMenu = ({isAuthenticated = false}) => {

  const MenuItems = () => (
      <>
        <MenuItem icon="box-archive" to="/validator/search">
          <Translate contentKey="global.menu.validator.search">Search Validators</Translate>
        </MenuItem>
        <MenuItem icon="box-archive" to="/validator/new">
          <Translate contentKey="global.menu.validator.add">Add Validator</Translate>
        </MenuItem>
      </>
  );

  return (
      <>
        { isAuthenticated && MenuItems() }
      </>
  );
}
export default ValidatorsMenu;
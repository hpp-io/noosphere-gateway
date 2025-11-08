import React from 'react';
import { Translate, translate } from 'react-jhipster';
import MenuItem from "app/shared/layout/menus/menu-item";
import { NavDropdown } from "app/shared/layout/menus/menu-components";


const ContainersMenu = ({isAuthenticated = false}) => {

  const MenuItems = () => (
      <>
        <MenuItem icon="box-archive" to="/container/search">
          <Translate contentKey="global.menu.container.search">Search Containers</Translate>
        </MenuItem>
        <MenuItem icon="box-archive" to="/container/new">
          <Translate contentKey="global.menu.container.add">Add Container</Translate>
        </MenuItem>
      </>
  );

  return (
      <>
        { isAuthenticated && MenuItems() }
      </>
  );
}
export default ContainersMenu;
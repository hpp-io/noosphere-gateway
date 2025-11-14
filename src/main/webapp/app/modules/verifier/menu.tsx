import React from 'react';
import { Translate, translate } from 'react-jhipster';
import MenuItem from "app/shared/layout/menus/menu-item";


const VerifiersMenu = ({isAuthenticated = false}) => {

  const MenuItems = () => (
      <>
        <MenuItem icon="box-archive" to="/verifier/search">
          <Translate contentKey="global.menu.verifier.search">Search Verifiers</Translate>
        </MenuItem>
        <MenuItem icon="box-archive" to="/verifier/new">
          <Translate contentKey="global.menu.verifier.add">Add Verifier</Translate>
        </MenuItem>
      </>
  );

  return (
      <>
        { isAuthenticated && MenuItems() }
      </>
  );
}
export default VerifiersMenu;
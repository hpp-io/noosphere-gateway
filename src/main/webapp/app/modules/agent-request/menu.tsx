import React from 'react';
import { Translate, translate } from 'react-jhipster';
import MenuItem from "app/shared/layout/menus/menu-item";
import { NavDropdown } from "app/shared/layout/menus/menu-components";


const AgentRequestsMenu = ({isAuthenticated = false}) => {

  const MenuItems = () => (
      <>
        <MenuItem icon="box-archive" to="/agent-request/search">
          <Translate contentKey="global.menu.agentRequest.search">Search Agent Requests</Translate>
        </MenuItem>
        <MenuItem icon="box-archive" to="/agent-request/new">
          <Translate contentKey="global.menu.agentRequest.add">Add Agent Request</Translate>
        </MenuItem>
      </>
  );

  return (
      <>
        { isAuthenticated && MenuItems() }
      </>
  );
}
export default AgentRequestsMenu;
import React, { Suspense } from 'react';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';
import MenuItem from "app/shared/layout/menus/menu-item";
import { faBoxArchive } from "@fortawesome/free-solid-svg-icons";

const EntitiesMenuItems = React.lazy(() =>
    import('app/entities/menu').catch(() => import('app/shared/error/error-loading')));

const entitiesMenuItems = () => (
    <>
      <MenuItem icon="box-archive" to="/container">
        <Translate contentKey="global.menu.container">Container</Translate>
      </MenuItem>
    </>
);


export const EntitiesMenu = ({ isAuthenticated = false }) => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <Suspense fallback={<div>loading...</div>}>
      <EntitiesMenuItems />
    </Suspense>
    {isAuthenticated && entitiesMenuItems()}
  </NavDropdown>
);

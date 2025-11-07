import React, { Suspense } from 'react';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

const EntitiesMenuItems = React.lazy(() =>
    import('app/entities/menu').catch(() => import('app/shared/error/error-loading')));

export const EntitiesMenu = ({ isAuthenticated = false }) => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.products.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <Suspense fallback={<div>loading...</div>}>
      <EntitiesMenuItems />
    </Suspense>
  </NavDropdown>
);

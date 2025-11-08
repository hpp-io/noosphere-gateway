import React, { Suspense } from 'react';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

const ContainersMenuItems = React.lazy(() =>
    import('app/modules/container/menu').catch(() => import('app/shared/error/error-loading')));

export const ContainersMenu = ({ isAuthenticated = false }) => (
    <NavDropdown
        icon="th-list"
        name={translate('global.menu.container.title')}
        id="container-menu"
        data-cy="entity"
        style={{ maxHeight: '80vh', overflow: 'auto' }}
    >
      <Suspense fallback={<div>loading...</div>}>
        <ContainersMenuItems isAuthenticated={isAuthenticated}/>
      </Suspense>
    </NavDropdown>
);

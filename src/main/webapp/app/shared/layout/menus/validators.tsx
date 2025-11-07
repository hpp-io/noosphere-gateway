import React, { Suspense } from 'react';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

const ValidatorsMenuItems = React.lazy(() =>
    import('app/modules/validator/menu').catch(() => import('app/shared/error/error-loading')));

export const ValidatorsMenu = ({ isAuthenticated = false }) => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.validator.title')}
    id="validator-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <Suspense fallback={<div>loading...</div>}>
      <ValidatorsMenuItems isAuthenticated={isAuthenticated}/>
    </Suspense>
  </NavDropdown>
);

import React, { Suspense } from 'react';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

const VerifiersMenuItems = React.lazy(() =>
    import('app/modules/verifier/menu').catch(() => import('app/shared/error/error-loading')));

export const VerifiersMenu = ({ isAuthenticated = false }) => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.verifier.title')}
    id="verifier-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <Suspense fallback={<div>loading...</div>}>
      <VerifiersMenuItems isAuthenticated={isAuthenticated}/>
    </Suspense>
  </NavDropdown>
);

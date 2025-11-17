import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import KeystoreGenerator from './keystore-generator/keystore-generator';

const SettingRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route path="keystore-generator" element={<KeystoreGenerator />} />
    </ErrorBoundaryRoutes>
  </div>
);

export default SettingRoutes;

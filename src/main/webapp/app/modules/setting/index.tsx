import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import KeystoreGenerator from './keystore-generator/keystore-generator';
import Profile from "app/modules/setting/profile";

const SettingRoutes = () => (
    <div>
      <ErrorBoundaryRoutes>
        <Route path="keystore-generator" element={ <KeystoreGenerator/> }/>
        <Route path="profile" element={ <Profile/> }/>
      </ErrorBoundaryRoutes>
    </div>
);

export default SettingRoutes;

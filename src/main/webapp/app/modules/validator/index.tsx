import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Validator from "app/modules/validator/validator";

const ValidatorRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          {/* <Route path={`${match.url}/new`} element={<FacilityUpdate/>} />*/ }
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <Validator/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default ValidatorRoutes;
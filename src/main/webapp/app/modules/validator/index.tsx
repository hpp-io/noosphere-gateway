import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Validator from "app/modules/validator/validator";
import CreateValidator from "app/modules/validator/create-validator";

const ValidatorRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          <Route path={`/new`} element={<CreateValidator/>} />
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <Validator/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default ValidatorRoutes;
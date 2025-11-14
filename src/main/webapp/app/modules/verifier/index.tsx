import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Verifier from "app/modules/verifier/verifier";
import CreateVerifier from "app/modules/verifier/create-verifier";

const VerifierRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          <Route path={`/new`} element={<CreateVerifier/>} />
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <Verifier/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default VerifierRoutes;
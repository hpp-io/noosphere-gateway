import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Container from "app/modules/container/container";

const ContainerRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          {/* <Route path={`${match.url}/new`} element={<FacilityUpdate/>} />*/ }
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '' } element={ <Container/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default ContainerRoutes;
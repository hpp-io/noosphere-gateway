import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Container from "app/modules/container/container";
import CreateContainer from "app/modules/container/create-container";

const ContainerRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          <Route path={`/new`} element={<CreateContainer/>} />
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <Container/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default ContainerRoutes;
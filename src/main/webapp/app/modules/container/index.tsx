import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Container from "app/modules/container/container";
import AddContainer from "app/modules/container/add-container";

const ContainerRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          <Route path={`/new`} element={<AddContainer/>} />
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <Container/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default ContainerRoutes;
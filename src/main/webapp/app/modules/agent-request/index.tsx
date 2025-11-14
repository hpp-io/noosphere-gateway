import React from 'react';
import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import AgentRequest from "app/modules/agent-request/agent-request";
import CreateAgentRequest from "app/modules/agent-request/create-agent-request";

const AgentRequestRoutes = () => {
  return (
      <div>
        <ErrorBoundaryRoutes>
          <Route path={`/new`} element={<CreateAgentRequest/>} />
          {/* <Route path={`${match.url}/:id/edit`} element={<FacilityUpdate />} />*/ }
          <Route path={ '/search' } element={ <AgentRequest/> }/>
        </ErrorBoundaryRoutes>
      </div>
  );
};

export default AgentRequestRoutes;
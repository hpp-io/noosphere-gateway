import React from 'react';

import { Route } from 'react-router';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Logs from './utils/logs';
import Health from './health/health';
import Metrics from './metrics/metrics';
import Configuration from './configuration/configuration';
import Docs from './docs/docs';
import Gateway from './gateway/gateway';
import RateLimiting from './rate-limiting/rate-limiting';
import UsageStatistics from './usage-statistics/usage-statistics'; // Import the new component

const AdministrationRoutes = () => (
  <div>
    <ErrorBoundaryRoutes>
      <Route path="gateway" element={<Gateway />} />
      <Route path="health" element={<Health />} />
      <Route path="metrics" element={<Metrics />} />
      <Route path="configuration" element={<Configuration />} />
      <Route path="logs" element={<Logs />} />
      <Route path="docs" element={<Docs />} />
      <Route path="rate-limiting" element={<RateLimiting />} />
      <Route path="usage-statistics" element={<UsageStatistics />} /> {/* Add the new route */}
    </ErrorBoundaryRoutes>
  </div>
);

export default AdministrationRoutes;

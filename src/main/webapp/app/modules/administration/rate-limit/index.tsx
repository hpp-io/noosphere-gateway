import React from 'react';
import { Route, Routes } from 'react-router-dom';

import RateLimit from './rate-limit';
import RateLimitDetail from './rate-limit-detail';
import RateLimitUpdate from './rate-limit-update';
import RateLimitDeleteDialog from './rate-limit-delete-dialog';

const RateLimitRoutes = () => (
  <Routes>
    <Route index element={<RateLimit />} />
    <Route path="new" element={<RateLimitUpdate />} />
    <Route path=":id">
      <Route index element={<RateLimitDetail />} />
      <Route path="edit" element={<RateLimitUpdate />} />
      <Route path="delete" element={<RateLimitDeleteDialog />} />
    </Route>
  </Routes>
);

export default RateLimitRoutes;

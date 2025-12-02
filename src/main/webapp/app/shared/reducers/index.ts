import { ReducersMapObject } from '@reduxjs/toolkit';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import administration from 'app/modules/administration/administration.reducer';
import keystoreGenerator from 'app/modules/setting/keystore-generator/keystore-generator.reducer';
import container from 'app/modules/container/container.reducer';
import verifier from 'app/modules/verifier/verifier.reducer';
import agentRequest from 'app/modules/agent-request/agent-request.reducer';
import rateLimit from 'app/modules/administration/rate-limit/rate-limit.reducer';
import usageStatistics from 'app/modules/administration/usage-statistics/usage-statistics.reducer'; // Import the new reducer
import locale from './locale';
import authentication from './authentication';
import userManagement from './user-management';
import applicationProfile from './application-profile';

/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer: ReducersMapObject = {
  authentication,
  locale,
  applicationProfile,
  userManagement,
  administration,
  keystoreGenerator,
  loadingBar,
  container,
  verifier,
  agentRequest,
  rateLimit,
  usageStatistics, // Add the new reducer
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default rootReducer;

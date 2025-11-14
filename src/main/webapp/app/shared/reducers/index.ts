import { ReducersMapObject } from '@reduxjs/toolkit';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import administration from 'app/modules/administration/administration.reducer';
import container from 'app/modules/container/container.reducer';
import verifier from 'app/modules/verifier/verifier.reducer';
import agentRequest from 'app/modules/agent-request/agent-request.reducer';
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
  loadingBar,
  container,
  verifier,
  agentRequest,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default rootReducer;

import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs';

import React, { useEffect } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import Header from 'app/shared/layout/header/header';
import Footer from 'app/shared/layout/footer/footer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';
import AppRoutes from 'app/routes';
// import AppkitProvider from 'app/app-kit';

const baseHref = document.querySelector('base').getAttribute('href').replace(/\/$/, '');

export const App = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
  }, []);

  const currentLocale = useAppSelector(state => state.locale.currentLocale);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  const paddingTop = '60px';
  // prettier-ignore
  return (
      <BrowserRouter basename={ baseHref }>
        <div className="app-container" style={ {paddingTop} }>
          <ToastContainer position="top-left" className="toastify-container" toastClassName="toastify-toast"/>
          <ErrorBoundary>
            {/* Header Bar */ }
            <Header
                isAuthenticated={ isAuthenticated }
                isAdmin={ isAdmin }
                currentLocale={ currentLocale }
                isInProduction={ isInProduction }
                isOpenAPIEnabled={ isOpenAPIEnabled }
            />

            {/* Application Content */ }
            <div className="app-content" role="main">
              <AppRoutes/>
            </div>

            {/* Footer */ }
            <Footer/>
          </ErrorBoundary>
        </div>

      </BrowserRouter>
  );
};

export default App;
import './home.scss';

import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row } from 'reactstrap';

import { getLoginUrl, REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const walletAddress = useAppSelector(state => state.authentication.account?.walletAddress);
  const isFetching = useAppSelector(state => state.userManagement.loading);
  const pageLocation = useLocation();
  const navigate = useNavigate();

  const dispatch = useAppDispatch();

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${ location.origin }${ redirectURL }`;
    }
  });


  return (
      <Row>
        <Col md="9">
          <h1 className="display-4">
            <Translate contentKey="home.title" interpolate={ {name: account.firstName} }/>
          </h1>
          <p className="lead">
            <Translate contentKey="home.subtitle">This is your homepage</Translate>
          </p>
          { account?.login ? (
              <div>
                <Alert color="success">
                  <Translate contentKey="home.logged.message" interpolate={ {username: account.login} }>
                    You are logged in as user { account.login }.
                  </Translate>
                </Alert>
                <Alert color="success">
                  <a
                      className="alert-link"
                      onClick={ () =>
                          navigate('/profile', {
                            state: {from: pageLocation},
                          })
                      }
                  >
                    <Translate contentKey="global.menu.account.profile">Profile</Translate>
                  </a>
                </Alert>
                <Alert color="success">
                  <a
                      className="alert-link"
                      onClick={ () =>
                          navigate('/container', {
                            state: {from: pageLocation},
                          })
                      }
                  >
                    <Translate contentKey="global.menu.container">Container</Translate>
                  </a>
                </Alert>
              </div>
          ) : (
              <div>
                <Alert color="warning">
                  <a
                      className="alert-link"
                      onClick={ () =>
                          navigate(getLoginUrl(), {
                            state: {from: pageLocation},
                          })
                      }
                  >
                    <Translate contentKey="global.messages.info.authenticated.link">sign in</Translate>
                  </a>
                </Alert>
              </div>
          ) }

        </Col>
      </Row>
  );
};

export default Home;

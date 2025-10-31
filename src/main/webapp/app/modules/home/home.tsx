import './home.scss';

import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row, Button, Input } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getLoginUrl, REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createMyWalletAddress } from "app/shared/reducers/user-management";

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

  const [ownerAddress, setOwnerAddress] = useState(null);

  const createWalletAddress = () => {
    if (ownerAddress) {
      dispatch(createMyWalletAddress({ownerAddress}));
    }
  };

  console.log("walletAddress", walletAddress);
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
                <p className="lead">
                  { walletAddress ? (
                  <span>Wallet Address: {walletAddress}</span>
                      ) :
                      (
                          <div>
                            <Input value={ownerAddress} onChange={e => setOwnerAddress(e.target.value)} />
                          <Button onClick={createWalletAddress} color={isFetching ? 'danger' : 'primary'} disabled={isFetching}>
                            <FontAwesomeIcon icon="wallet" />
                            &nbsp;
                            <Translate component="span" contentKey="buttons.generate">
                              Refresh
                            </Translate>
                          </Button>
                          </div>
                      )
                  }
                </p>
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

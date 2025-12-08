import './profile.scss';

import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import {
  createMyApiKey,
  createMyKeystore,
  createMyWalletAddress,
  getMyApiKey,
  getMyWalletAddress,
  reset,
  updateUser
} from 'app/shared/reducers/user-management';
import { Alert, Button, Col, Form, FormGroup, Input, Label, Row } from 'reactstrap';
import { useAppKit } from '@reown/appkit/react';
import { useAccount, useDisconnect } from 'wagmi';

export const Profile = () => {
  const dispatch = useAppDispatch();

  const account = useAppSelector(state => state.authentication.account);
  const walletAddress = useAppSelector(state => state.userManagement.walletAddress);
  const apiKey = useAppSelector(state => state.userManagement.apiKey);
  const userLoading = useAppSelector(state => state.userManagement.loading);
  const keystoreFile = useAppSelector(state => state.userManagement.keystoreFile);
  const fileName = useAppSelector(state => state.userManagement.fileName);

  const [formData, setFormData] = useState({
    firstName: account?.firstName || '',
    lastName: account?.lastName || '',
    email: account?.email || '',
  });
  const [privateKey, setPrivateKey] = useState('');
  const [keyAlias, setKeyAlias] = useState('hpp-eth-key');
  const [keyPassword, setKeyPassword] = useState('');


  const {open: openWalletDialog, close: closeWalletDialog} = useAppKit();

  const {address, isConnected} = useAccount();

  const {disconnect} = useDisconnect();


  const openWalletDialogClicked = () => {
    openWalletDialog({view: "Connect"});
  }

  const disconnectWalletClicked = () => {
    disconnect();
  }


  const [successMessage, setSuccessMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [ownerAddress, setOwnerAddress] = useState('');

  useEffect(() => {
    // Pre-fill the form with user properties when account state updates
    setFormData({
      firstName: account?.firstName || '',
      lastName: account?.lastName || '',
      email: account?.email || '',
    });
  }, [account]);

  useEffect(() => {
    dispatch(getMyWalletAddress());
    dispatch(getMyApiKey());
  }, []);


  useEffect(() => {
    if (address) {
      setOwnerAddress(address);
      closeWalletDialog();
    }
  }, [address]);

  useEffect(() => {
    if (keystoreFile) {
      const url = window.URL.createObjectURL(keystoreFile);
      const a = document.createElement('a');
      a.href = url;
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      a.remove();
      dispatch(reset());
      dispatch(getMyWalletAddress());
    }
  }, [keystoreFile, fileName, dispatch]);


  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = event.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = event => {
    event.preventDefault();
    setIsSaving(true);

    // Dispatch the updateUser action with updated data
    dispatch(updateUser(formData))
    .then(() => {
      setSuccessMessage('Your profile has been updated successfully.');
      setIsSaving(false);
    })
    .catch(() => {
      setIsSaving(false);
    });
  };

  const onCreateWalletAddress = () => {
    dispatch(createMyWalletAddress({ownerAddress}));
  };

  const onCreateApiKey = () => {
    dispatch(createMyApiKey());
  };

  const onGenerateKeystore = () => {
    dispatch(createMyKeystore({privateKey, keyAlias, password: keyPassword, createHppWallet: true, isWallet: true}));
  };

  const onGenerateKeystoreWithExistingWalletAddress = () => {
    dispatch(createMyKeystore({privateKey, keyAlias, password: keyPassword, walletAddress, isWallet: true}));
  };


  return (
      <div className="profile-page">
        <Row>
          <Col md="6" className="offset-md-3">
            <h2>My Profile</h2>
            { successMessage && <Alert color="success">{ successMessage }</Alert> }

            <Form onSubmit={ handleSubmit }>
              <FormGroup>
                <Label for="firstName">First Name</Label>
                <Input
                    type="text"
                    id="firstName"
                    name="firstName"
                    value={ formData.firstName }
                    onChange={ handleInputChange }
                    placeholder="Enter your first name"
                />
              </FormGroup>
              <FormGroup>
                <Label for="lastName">Last Name</Label>
                <Input
                    type="text"
                    id="lastName"
                    name="lastName"
                    value={ formData.lastName }
                    onChange={ handleInputChange }
                    placeholder="Enter your last name"
                />
              </FormGroup>
              <FormGroup>
                <Label for="email">Email</Label>
                <Input
                    type="email"
                    id="email"
                    name="email"
                    disabled={ true }
                    value={ formData.email }
                />
              </FormGroup>
              <Button color="primary" type="submit" disabled={ isSaving }>
                { isSaving ? 'Saving...' : 'Save Changes' }
              </Button>
            </Form>
            <br/>
            <br/>
            <br/>
            <FormGroup>
              <Label for="apiKey">API Key</Label>
              { apiKey ? (
                  <>
                    <Input
                        type="text"
                        id="apiKey"
                        name="apiKey"
                        disabled={ true }
                        value={ apiKey }
                    />
                    <br/>
                    <Button color="primary" disabled={ userLoading } onClick={ onCreateApiKey }>
                      { 'Regenerate API Key' }
                    </Button>
                  </>
              ) : (
                  <>
                    <br/>
                    <Button color="primary" disabled={ userLoading } onClick={ onCreateApiKey }>
                      { 'Generate API Key' }
                    </Button>
                  </>
              ) }
              <br/>
              <br/>
              <br/>
            </FormGroup>
            <FormGroup>
              <Label for="walletAddress">Wallet Address</Label>
              <Input
                  type="text"
                  id="walletAddress"
                  name="walletAddress"
                  disabled={ true }
                  value={ walletAddress }
              />
              <br/>
              <Label for="keyAlias">Key Alias</Label>
              <Input
                  type="text"
                  id="keyAlias"
                  name="keyAlias"
                  onChange={ e => setKeyAlias(e.target.value) }
                  placeholder="Enter your key alias"
                  value={ keyAlias }
              />
              <br/>
              <Label for="keyPassword">Keystore Password</Label>
              <Input
                  type="password"
                  id="keyPassword"
                  name="keyPassword"
                  onChange={ e => setKeyPassword(e.target.value) }
                  placeholder="Enter your key password"
                  value={ keyPassword }
              />
              <br/>
              <Label for="privateKey">Private Key</Label>
              <Input
                  type="text"
                  id="privateKey"
                  name="privateKey"
                  onChange={ e => setPrivateKey(e.target.value) }
                  placeholder="Enter your private key"
                  value={ privateKey }
              />
              <br/>
              { (keyAlias && keyAlias !== "" && keyPassword && keyPassword !== "" && privateKey && privateKey !== "") ? (
                  <>
                    <Button color="primary" disabled={ userLoading } onClick={ onGenerateKeystoreWithExistingWalletAddress }>
                      { 'Generate Keystore With Existing Wallet Address' }
                    </Button>
                    <br/>
                    <br/>
                    <Button color="primary" disabled={ userLoading } onClick={ onGenerateKeystore }>
                      { 'Generate Keystore With New Wallet Address' }
                    </Button>
                  </>
              ) : (
                  <>
                    <Button color="primary" disabled={ true }>
                      { 'Generate Keystore With Existing Wallet Address' }
                    </Button>
                    <br/>
                    <br/>
                    <Button color="primary" disabled={ true }>
                      { 'Generate Keystore With New Wallet Address' }
                    </Button>
                  </>
              )
              }
              <br/>
              <br/>
              <br/>
              <br/>
              <Input
                  type="text"
                  id="ownerAddress"
                  name="ownerAddress"
                  value={ ownerAddress }
                  onChange={ e => setOwnerAddress(e.target.value) }
                  placeholder="Enter your owner address"
              />
              <br/>
              { isConnected === true ? (
                  <Button color="primary" disabled={ userLoading } onClick={ disconnectWalletClicked }>
                    { 'Disconnect Wallet' }
                  </Button>
              ) : (
                  <Button color="primary" disabled={ userLoading } onClick={ openWalletDialogClicked }>
                    { 'Connect Wallet' }
                  </Button>
              ) }
              <br/>
              <br/>
              { (ownerAddress && ownerAddress !== "") ? (
                  (walletAddress && walletAddress !== "") ? (
                      <>
                        <Button color="primary" disabled={ userLoading } onClick={ onCreateWalletAddress }>
                          { 'Regenerate Wallet Address' }
                        </Button>
                      </>
                  ) : (
                      <>
                        <br/>
                        <Button color="primary" disabled={ userLoading } onClick={ onCreateWalletAddress }>
                          { 'Generate Wallet Address' }
                        </Button>
                      </>
                  )
              ) : null }
              <br/>
              <br/>
            </FormGroup>
            { userLoading && <Alert color="info" className="mt-3">Processing your request...</Alert> }

          </Col>
        </Row>
      </div>
  );
};

export default Profile;
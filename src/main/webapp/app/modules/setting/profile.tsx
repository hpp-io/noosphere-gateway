import './profile.scss';

import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createMyApiKey, createMyWalletAddress, getMyApiKey, getMyWalletAddress, updateUser } from 'app/shared/reducers/user-management';
import { Alert, Button, Col, Form, FormGroup, Input, Label, Row } from 'reactstrap';
import { useAppKit, useAppKitAccount } from '@reown/appkit/react';
import { useAccount, useDisconnect } from 'wagmi';

export const Profile = () => {
  const dispatch = useAppDispatch();

  const account = useAppSelector(state => state.authentication.account);
  const walletAddress = useAppSelector(state => state.userManagement.walletAddress);
  const apiKey = useAppSelector(state => state.userManagement.apiKey);
  const userLoading = useAppSelector(state => state.userManagement.loading);
  const [formData, setFormData] = useState({
    firstName: account?.firstName || '',
    lastName: account?.lastName || '',
    email: account?.email || '',
  });


  const { open: openWalletDialog , close: closeWalletDialog } = useAppKit();

  const { address, isConnected } = useAccount();

  const { disconnect } = useDisconnect();


  const openWalletDialogClicked = () =>{
    openWalletDialog({ view: "Connect" });
  }

  const disconnectWalletClicked = () =>{
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

            </FormGroup>
            <FormGroup>
              <Label for="walletAddress">Wallet Address</Label>
              { (walletAddress || walletAddress === "") ? (
                  <>
                    <Input
                        type="text"
                        id="walletAddress"
                        name="walletAddress"
                        disabled={ true }
                        value={ walletAddress }
                    />
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
                    {isConnected === true ?(
                        <Button color="primary" disabled={ userLoading } onClick={ disconnectWalletClicked }>
                          { 'Disconnect Wallet' }
                        </Button>
                    ):(
                        <Button color="primary" disabled={ userLoading } onClick={ openWalletDialogClicked }>
                          { 'Connect Wallet' }
                        </Button>
                    )}

                    <br/>
                    <br/>
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
              ) }
            </FormGroup>
            { userLoading && <Alert color="info" className="mt-3">Processing your request...</Alert> }

          </Col>
        </Row>
      </div>
  );
};

export default Profile;
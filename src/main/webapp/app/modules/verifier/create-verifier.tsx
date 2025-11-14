import './verifier.scss';

import React, { useState } from 'react';
import { Button, Input, Form, Row, Col, FormGroup, Label, Alert, Card, CardBody } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { IEnv } from "app/shared/model/env.model";
import { createVerifier } from './verifier.reducer';
import { convertEnvVariablesToString, convertPaymentsToString, convertVolumesToString } from "app/shared/util/entity-utils";

interface IPayment {
  address?: string;
  amount?: string;
}

export const CreateVerifier = () => {
  const dispatch = useAppDispatch();
  const loading = useAppSelector(state => state.verifier.loading);
  const updateSuccess = useAppSelector(state => state.verifier.updateSuccess);
  const errorMessage = useAppSelector(state => state.verifier.errorMessage);

  const [verifierData, setVerifierData] = useState({
    name: '',
    walletAddress: '',
    verifierAddress: '',
    imageName: '',
    port: '',
    command: ''
  });

  const [environmentVariables, setEnvironmentVariables] = useState<IEnv[]>([
    { name: '', value: '' }
  ]);

  const [volumes, setVolumes] = useState<string[]>(['']);

  const [payments, setPayments] = useState<IPayment[]>([
    { address: '', amount: '' }
  ]);

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setVerifierData({
      ...verifierData,
      [name]: value
    });

    // Clear validation error when user starts typing
    if (validationErrors[name]) {
      setValidationErrors({
        ...validationErrors,
        [name]: ''
      });
    }
  };

  const handleEnvChange = (index: number, field: 'name' | 'value', value: string) => {
    const updatedEnvVars = [...environmentVariables];
    updatedEnvVars[index] = {
      ...updatedEnvVars[index],
      [field]: value
    };
    setEnvironmentVariables(updatedEnvVars);
  };

  const addEnvVariable = () => {
    setEnvironmentVariables([...environmentVariables, { name: '', value: '' }]);
  };

  const removeEnvVariable = (index: number) => {
    if (environmentVariables.length > 1) {
      const updatedEnvVars = environmentVariables.filter((_, i) => i !== index);
      setEnvironmentVariables(updatedEnvVars);
    }
  };

  const handleVolumeChange = (index: number, value: string) => {
    const updatedVolumes = [...volumes];
    updatedVolumes[index] = value;
    setVolumes(updatedVolumes);
  };

  const addVolume = () => {
    setVolumes([...volumes, '']);
  };

  const removeVolume = (index: number) => {
    if (volumes.length > 1) {
      const updatedVolumes = volumes.filter((_, i) => i !== index);
      setVolumes(updatedVolumes);
    }
  };

  const handlePaymentChange = (index: number, field: 'address' | 'amount', value: string) => {
    const updatedPayments = [...payments];
    updatedPayments[index] = {
      ...updatedPayments[index],
      [field]: value
    };
    setPayments(updatedPayments);
  };

  const addPayment = () => {
    setPayments([...payments, { address: '', amount: '' }]);
  };

  const removePayment = (index: number) => {
    if (payments.length > 1) {
      const updatedPayments = payments.filter((_, i) => i !== index);
      setPayments(updatedPayments);
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!verifierData.name.trim()) {
      errors.name = 'Verifier name is required';
    }

    if (!verifierData.imageName.trim()) {
      errors.imageName = 'Verifier image name is required';
    }

    if (!verifierData.walletAddress.trim()) {
      errors.walletAddress = 'Wallet address is required';
    } else if (!/^0x[a-fA-F0-9]{40}$/.test(verifierData.walletAddress)) {
      errors.walletAddress = 'Invalid wallet address format';
    }

    if (!verifierData.verifierAddress.trim()) {
      errors.verifierAddress = 'Verifier address is required';
    } else if (!/^0x[a-fA-F0-9]{40}$/.test(verifierData.verifierAddress)) {
      errors.verifierAddress = 'Invalid verifier address format';
    }

    if (!verifierData.port.trim()) {
      errors.port = 'Port is required';
    } else if (isNaN(Number(verifierData.port))) {
      errors.port = 'Port must be a valid number';
    } else {
      const portNum = Number(verifierData.port);
      if (portNum < 1 || portNum > 65535) {
        errors.port = 'Port must be between 1 and 65535';
      }
    }

    // Validate environment variables
    const duplicateEnvNames = environmentVariables
      .map(env => env.name?.trim())
      .filter(name => name && name !== '')
      .filter((name, index, arr) => arr.indexOf(name) !== index);

    if (duplicateEnvNames.length > 0) {
      errors.environmentVariables = `Duplicate environment variable names found: ${duplicateEnvNames.join(', ')}`;
    }

    const invalidEnvNames = environmentVariables
      .filter(env => env.name && env.name.trim() !== '')
      .filter(env => env.name && !/^[a-zA-Z_][a-zA-Z0-9_]*$/.test(env.name.trim()));

    if (invalidEnvNames.length > 0) {
      errors.environmentVariables = 'Environment variable names must start with a letter or underscore and contain only letters, numbers, and underscores';
    }

    // Validate volumes
    const nonEmptyVolumes = volumes.filter(volume => volume.trim() !== '');
    const invalidVolumes = nonEmptyVolumes.filter(volume => {
      // Basic validation for Docker volume format (host_path:verifier_path or just verifier_path)
      const volumePattern = /^([a-zA-Z0-9._/-]+:)?[a-zA-Z0-9._/-]+$/;
      return !volumePattern.test(volume.trim());
    });

    if (invalidVolumes.length > 0) {
      errors.volumes = 'Volume entries must follow the format "host_path:verifier_path" or just "verifier_path"';
    }

    const duplicateVolumes = nonEmptyVolumes
      .filter((volume, index, arr) => arr.indexOf(volume) !== index);

    if (duplicateVolumes.length > 0) {
      errors.volumes = `Duplicate volume entries found: ${duplicateVolumes.join(', ')}`;
    }

    // Validate payments
    const nonEmptyPayments = payments.filter(payment => payment.address && payment.address.trim() !== '');

    const invalidAddresses = nonEmptyPayments.filter(payment => {
      return payment.address && !/^0x[a-fA-F0-9]{40}$/.test(payment.address.trim());
    });

    if (invalidAddresses.length > 0) {
      errors.payments = 'All payment addresses must be valid Ethereum addresses (42 characters starting with 0x)';
    }

    const invalidAmounts = nonEmptyPayments.filter(payment => {
      const amount = payment.amount?.trim();
      return amount && (isNaN(Number(amount)) || Number(amount) < 0);
    });

    if (invalidAmounts.length > 0) {
      errors.payments = 'All payment amounts must be valid non-negative numbers';
    }

    const duplicateAddresses = nonEmptyPayments
      .map(payment => payment.address?.trim())
      .filter((address, index, arr) => arr.indexOf(address) !== index);

    if (duplicateAddresses.length > 0) {
      errors.payments = `Duplicate payment addresses found: ${duplicateAddresses.join(', ')}`;
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (!validateForm()) {
      return;
    }

    const submitData = {
      ...verifierData,
      port: verifierData.port ? Number(verifierData.port) : undefined,
      name: verifierData.name || null,
      walletAddress: verifierData.walletAddress,
      verifierAddress: verifierData.verifierAddress,
      imageName: verifierData.imageName,
      command: verifierData.command || undefined,
      environmentVariables: convertEnvVariablesToString(environmentVariables) || undefined,
      volumes: convertVolumesToString(volumes) || undefined,
      payments: convertPaymentsToString(payments) || undefined
    };

    dispatch(createVerifier(submitData));
  };

  const handleReset = () => {
    setVerifierData({
      name: '',
      walletAddress: '',
      verifierAddress: '',
      imageName: '',
      port: '',
      command: ''
    });
    setEnvironmentVariables([{ name: '', value: '' }]);
    setVolumes(['']);
    setPayments([{ address: '', amount: '' }]);
    setValidationErrors({});
  };

  return (
    <div className="create-verifier verifier-form-section">
      <h2 className="text-center my-4">Create New Verifier</h2>

      {updateSuccess && (
        <Alert color="success">
          Verifier created successfully!
        </Alert>
      )}

      {errorMessage && (
        <Alert color="danger">
          {errorMessage}
        </Alert>
      )}

      <Form onSubmit={handleSubmit} className="verifier-form">
        <Row>
          <Col md={6}>
            <FormGroup>
              <Label for="name">Verifier Name *</Label>
              <Input
                type="text"
                id="name"
                name="name"
                placeholder="Enter verifier name"
                value={verifierData.name}
                onChange={handleInputChange}
                invalid={!!validationErrors.name}
                required
              />
              {validationErrors.name && (
                <div className="invalid-feedback">{validationErrors.name}</div>
              )}
            </FormGroup>
          </Col>
          <Col md={6}>
            <FormGroup>
              <Label for="imageName">Image Name *</Label>
              <Input
                type="text"
                id="imageName"
                name="imageName"
                placeholder="Enter verifier image name"
                value={verifierData.imageName}
                onChange={handleInputChange}
                invalid={!!validationErrors.imageName}
                required
              />
              {validationErrors.imageName && (
                <div className="invalid-feedback">{validationErrors.imageName}</div>
              )}
            </FormGroup>
          </Col>
        </Row>

        <Row>
          <Col md={2}>
            <FormGroup>
              <Label for="port">Port *</Label>
              <Input
                  type="number"
                  id="port"
                  name="port"
                  placeholder="Enter port number"
                  min="1"
                  max="65535"
                  value={verifierData.port}
                  onChange={handleInputChange}
                  invalid={!!validationErrors.port}
                  required
              />
              {validationErrors.port && (
                  <div className="invalid-feedback">{validationErrors.port}</div>
              )}
            </FormGroup>
          </Col>
        </Row>
        <Row>
          <Col md={12}>
            <FormGroup>
              <Label for="walletAddress">Wallet Address *</Label>
              <Input
                type="text"
                id="walletAddress"
                name="walletAddress"
                placeholder="0x..."
                value={verifierData.walletAddress}
                onChange={handleInputChange}
                invalid={!!validationErrors.walletAddress}
                required
              />
              {validationErrors.walletAddress && (
                <div className="invalid-feedback">{validationErrors.walletAddress}</div>
              )}
            </FormGroup>
          </Col>
        </Row>
        <Row>
          <Col md={12}>
            <FormGroup>
              <Label for="verifierAddress">Verifier Address *</Label>
              <Input
                type="text"
                id="verifierAddress"
                name="verifierAddress"
                placeholder="0x..."
                value={verifierData.verifierAddress}
                onChange={handleInputChange}
                invalid={!!validationErrors.verifierAddress}
                required
              />
              {validationErrors.verifierAddress && (
                <div className="invalid-feedback">{validationErrors.verifierAddress}</div>
              )}
            </FormGroup>
          </Col>
        </Row>



        <Row>
          <Col md={12}>
            <FormGroup>
              <Label for="command">Command</Label>
              <Input
                type="text"
                id="command"
                name="command"
                placeholder="Enter verifier command"
                value={verifierData.command}
                onChange={handleInputChange}
              />
            </FormGroup>
          </Col>
        </Row>

        {/* Environment Variables Section */}
        <Row>
          <Col md={12}>
            <FormGroup>
              <Label>Environment Variables</Label>
              <Card>
                <CardBody>
                  {environmentVariables.map((envVar, index) => (
                    <Row key={index} className="mb-2 align-items-end">
                      <Col md={4}>
                        <Label for={`env-name-${index}`} className="small">Variable Name</Label>
                        <Input
                          type="text"
                          id={`env-name-${index}`}
                          placeholder="e.g., NODE_ENV"
                          value={envVar.name}
                          onChange={(e) => handleEnvChange(index, 'name', e.target.value)}
                        />
                      </Col>
                      <Col md={6}>
                        <Label for={`env-value-${index}`} className="small">Value</Label>
                        <Input
                          type="text"
                          id={`env-value-${index}`}
                          placeholder="e.g., production"
                          value={envVar.value}
                          onChange={(e) => handleEnvChange(index, 'value', e.target.value)}
                        />
                      </Col>
                      <Col md={2}>
                        <div className="d-flex gap-2">
                          {environmentVariables.length > 1 && (
                            <Button
                              type="button"
                              color="danger"
                              size="sm"
                              onClick={() => removeEnvVariable(index)}
                              title="Remove variable"
                            >
                              ×
                            </Button>
                          )}
                          {index === environmentVariables.length - 1 && (
                            <Button
                              type="button"
                              color="success"
                              size="sm"
                              onClick={addEnvVariable}
                              title="Add variable"
                            >
                              +
                            </Button>
                          )}
                        </div>
                      </Col>
                    </Row>
                  ))}
                  {validationErrors.environmentVariables && (
                    <div className="text-danger small mt-2">
                      {validationErrors.environmentVariables}
                    </div>
                  )}
                </CardBody>
              </Card>
            </FormGroup>
          </Col>
        </Row>

        {/* Volumes Section */}
        <Row>
          <Col md={12}>
            <FormGroup>
              <Label>Volumes</Label>
              <Card>
                <CardBody>
                  {volumes.map((volume, index) => (
                    <Row key={index} className="mb-2 align-items-end">
                      <Col md={10}>
                        <Label for={`volume-${index}`} className="small">Volume Mount</Label>
                        <Input
                          type="text"
                          id={`volume-${index}`}
                          placeholder="e.g., /host/path:/verifier/path or /verifier/path"
                          value={volume}
                          onChange={(e) => handleVolumeChange(index, e.target.value)}
                        />
                      </Col>
                      <Col md={2}>
                        <div className="d-flex gap-2">
                          {volumes.length > 1 && (
                            <Button
                              type="button"
                              color="danger"
                              size="sm"
                              onClick={() => removeVolume(index)}
                              title="Remove volume"
                            >
                              ×
                            </Button>
                          )}
                          {index === volumes.length - 1 && (
                            <Button
                              type="button"
                              color="success"
                              size="sm"
                              onClick={addVolume}
                              title="Add volume"
                            >
                              +
                            </Button>
                          )}
                        </div>
                      </Col>
                    </Row>
                  ))}
                  {validationErrors.volumes && (
                    <div className="text-danger small mt-2">
                      {validationErrors.volumes}
                    </div>
                  )}
                  <div className="text-muted small mt-2">
                    <strong>Examples:</strong>
                    <ul className="mb-0 mt-1">
                      <li><code>/host/data:/app/data</code> - Bind mount host directory to verifier</li>
                      <li><code>myvolume:/app/data</code> - Named volume mount</li>
                      <li><code>/app/data</code> - Anonymous volume</li>
                    </ul>
                  </div>
                </CardBody>
              </Card>
            </FormGroup>
          </Col>
        </Row>

        {/* Payments Section */}
        <Row>
          <Col md={12}>
            <FormGroup>
              <Label>Accepted Payments</Label>
              <Card>
                <CardBody>
                  {payments.map((payment, index) => (
                    <Row key={index} className="mb-2 align-items-end">
                      <Col md={6}>
                        <Label for={`payment-address-${index}`} className="small">Address</Label>
                        <Input
                          type="text"
                          id={`payment-address-${index}`}
                          placeholder="0x1234567890123456789012345678901234567890"
                          value={payment.address}
                          onChange={(e) => handlePaymentChange(index, 'address', e.target.value)}
                        />
                      </Col>
                      <Col md={4}>
                        <Label for={`payment-amount-${index}`} className="small">Amount</Label>
                        <Input
                          type="number"
                          id={`payment-amount-${index}`}
                          placeholder="0.01"
                          step="0.01"
                          min="0"
                          value={payment.amount}
                          onChange={(e) => handlePaymentChange(index, 'amount', e.target.value)}
                        />
                      </Col>
                      <Col md={2}>
                        <div className="d-flex gap-2">
                          {payments.length > 1 && (
                            <Button
                              type="button"
                              color="danger"
                              size="sm"
                              onClick={() => removePayment(index)}
                              title="Remove payment"
                            >
                              ×
                            </Button>
                          )}
                          {index === payments.length - 1 && (
                            <Button
                              type="button"
                              color="success"
                              size="sm"
                              onClick={addPayment}
                              title="Add payment"
                            >
                              +
                            </Button>
                          )}
                        </div>
                      </Col>
                    </Row>
                  ))}
                  {validationErrors.payments && (
                    <div className="text-danger small mt-2">
                      {validationErrors.payments}
                    </div>
                  )}
                  <div className="text-muted small mt-2">
                    <strong>Note:</strong> Enter wallet addresses and corresponding payment amounts.
                    Addresses must be valid Ethereum addresses (42 characters starting with 0x).
                  </div>
                </CardBody>
              </Card>
            </FormGroup>
          </Col>
        </Row>

        <div className="form-actions mt-4">
          <Button
            type="submit"
            color="primary"
            disabled={loading}
            className="me-2"
          >
            {loading ? 'Creating...' : 'Create Verifier'}
          </Button>
          <Button
            type="button"
            color="secondary"
            onClick={handleReset}
            disabled={loading}
          >
            Reset
          </Button>
        </div>
      </Form>
    </div>
  );
};

export default CreateVerifier;
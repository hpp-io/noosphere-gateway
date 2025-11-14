import './agentRequest.scss';

import React, { useState } from 'react';
import { Button, Input, Form, Row, Col, FormGroup, Label, Alert, Card, CardBody } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createAgentRequest } from './agent-request.reducer';

export const CreateAgentRequest = () => {
  const dispatch = useAppDispatch();
  const loading = useAppSelector(state => state.agentRequest.loading);
  const updateSuccess = useAppSelector(state => state.agentRequest.updateSuccess);
  const errorMessage = useAppSelector(state => state.agentRequest.errorMessage);

  const [agentRequestData, setAgentRequestData] = useState({
    agent: null,
    container: null,
    userSubscription: null,
    statusCode: null,
  });

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setAgentRequestData({
      ...agentRequestData,
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




  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!agentRequestData.agent.trim()) {
      errors.agent = 'AgentRequest agent is required';
    }

    if (!agentRequestData.container.trim()) {
      errors.container = 'AgentRequest container is required';
    }

    if (!agentRequestData.userSubscription.trim()) {
      errors.container = 'AgentRequest userSubscription is required';
    }

    // Validate environment variables



    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (!validateForm()) {
      return;
    }

    const submitData = {
      ...agentRequestData,
      agent: agentRequestData.agent,
      container: agentRequestData.container,
      userSubscription: agentRequestData.userSubscription,
    };

    dispatch(createAgentRequest(submitData));
  };

  const handleReset = () => {
    setAgentRequestData({
      agent: null,
      container: null,
      userSubscription: null,
      statusCode: null,
    });
    setValidationErrors({});
  };

  return (
    <div className="create-agentRequest agentRequest-form-section">
      <h2 className="text-center my-4">Create New AgentRequest</h2>

      {updateSuccess && (
        <Alert color="success">
          AgentRequest created successfully!
        </Alert>
      )}

      {errorMessage && (
        <Alert color="danger">
          {errorMessage}
        </Alert>
      )}

      <Form onSubmit={handleSubmit} className="agentRequest-form">
        <Row>
          <Col md={6}>
            <FormGroup>
              <Label for="name">Agent *</Label>
              <Input
                type="text"
                id="agent"
                name="agent"
                placeholder="Enter agent"
                value={agentRequestData.agent}
                onChange={handleInputChange}
                invalid={!!validationErrors.agent}
                required
              />
              {validationErrors.agent && (
                <div className="invalid-feedback">{validationErrors.agent}</div>
              )}
            </FormGroup>
          </Col>
          <Col md={6}>
            <FormGroup>
              <Label for="imageName">Container *</Label>
              <Input
                type="text"
                id="container"
                name="container"
                placeholder="Enter container"
                value={agentRequestData.container}
                onChange={handleInputChange}
                invalid={!!validationErrors.container}
                required
              />
              {validationErrors.container && (
                <div className="invalid-feedback">{validationErrors.container}</div>
              )}
            </FormGroup>
          </Col>
        </Row>
        <Row>
          <Col md={2}>
            <FormGroup>
              <Label for="port">User Subscription *</Label>
              <Input
                  type="text"
                  id="userSubscription"
                  name="userSubscription"
                  placeholder="Enter user subscription"
                  value={agentRequestData.userSubscription}
                  onChange={handleInputChange}
                  invalid={!!validationErrors.userSubscription}
                  required
              />
              {validationErrors.userSubscription && (
                  <div className="invalid-feedback">{validationErrors.userSubscription}</div>
              )}
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
            {loading ? 'Creating...' : 'Create AgentRequest'}
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

export default CreateAgentRequest;
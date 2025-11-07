
import './container.scss';

import React, { useState } from 'react';
import { Button, Input, Form, Row, Col, FormGroup, Label, Alert } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { StatusCode } from "app/shared/model/enumerations/status-code.model";

export const AddContainer = () => {
  const dispatch = useAppDispatch();
  const loading = useAppSelector(state => state.container.loading);
  const updateSuccess = useAppSelector(state => state.container.updateSuccess);
  const errorMessage = useAppSelector(state => state.container.errorMessage);
  
  const [containerData, setContainerData] = useState({
    name: '',
    description: '',
    image: '',
    external: false,
    port: '',
    command: '',
    parameters: '',
    generatesProofs: false,
    price: '',
    statusCode: StatusCode.ACTIVE
  });

  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = event.target;
    setContainerData({
      ...containerData,
      [name]: type === 'checkbox' ? checked : value
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
    
    if (!containerData.name.trim()) {
      errors.name = 'Container name is required';
    }
    
    if (!containerData.image.trim()) {
      errors.image = 'Container image is required';
    }
    
    if (containerData.port && isNaN(Number(containerData.port))) {
      errors.port = 'Port must be a valid number';
    }
    
    if (containerData.price && isNaN(Number(containerData.price))) {
      errors.price = 'Price must be a valid number';
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
      ...containerData,
      port: containerData.port ? Number(containerData.port) : undefined,
      price: containerData.price ? Number(containerData.price) : undefined
    };

    // TODO: Implement the actual create container action
    // dispatch(createContainer(submitData));
    console.log('Container data to submit:', submitData);
  };

  const handleReset = () => {
    setContainerData({
      name: '',
      description: '',
      image: '',
      external: false,
      port: '',
      command: '',
      parameters: '',
      generatesProofs: false,
      price: '',
      statusCode: StatusCode.ACTIVE
    });
    setValidationErrors({});
  };

  return (
    <div className="add-container container-form-section">
      <h2 className="text-center my-4">Add New Container</h2>
      
      {updateSuccess && (
        <Alert color="success">
          Container created successfully!
        </Alert>
      )}
      
      {errorMessage && (
        <Alert color="danger">
          {errorMessage}
        </Alert>
      )}

      <Form onSubmit={handleSubmit} className="container-form">
        <Row>
          <Col md={6}>
            <FormGroup>
              <Label for="name">Container Name *</Label>
              <Input
                type="text"
                id="name"
                name="name"
                placeholder="Enter container name"
                value={containerData.name}
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
              <Label for="image">Container Image *</Label>
              <Input
                type="text"
                id="image"
                name="image"
                placeholder="Enter container image"
                value={containerData.image}
                onChange={handleInputChange}
                invalid={!!validationErrors.image}
                required
              />
              {validationErrors.image && (
                <div className="invalid-feedback">{validationErrors.image}</div>
              )}
            </FormGroup>
          </Col>
        </Row>

        <Row>
          <Col md={12}>
            <FormGroup>
              <Label for="description">Description</Label>
              <Input
                type="textarea"
                id="description"
                name="description"
                placeholder="Enter container description"
                value={containerData.description}
                onChange={handleInputChange}
                rows="3"
              />
            </FormGroup>
          </Col>
        </Row>

        <Row>
          <Col md={4}>
            <FormGroup>
              <Label for="port">Port</Label>
              <Input
                type="number"
                id="port"
                name="port"
                placeholder="Enter port number"
                value={containerData.port}
                onChange={handleInputChange}
                invalid={!!validationErrors.port}
              />
              {validationErrors.port && (
                <div className="invalid-feedback">{validationErrors.port}</div>
              )}
            </FormGroup>
          </Col>
          <Col md={4}>
            <FormGroup>
              <Label for="price">Price</Label>
              <Input
                type="number"
                step="0.01"
                id="price"
                name="price"
                placeholder="Enter price"
                value={containerData.price}
                onChange={handleInputChange}
                invalid={!!validationErrors.price}
              />
              {validationErrors.price && (
                <div className="invalid-feedback">{validationErrors.price}</div>
              )}
            </FormGroup>
          </Col>
          <Col md={4}>
            <FormGroup>
              <Label for="statusCode">Status</Label>
              <Input
                type="select"
                id="statusCode"
                name="statusCode"
                value={containerData.statusCode}
                onChange={handleInputChange}
              >
                {Object.values(StatusCode).map(code => (
                  <option key={code} value={code}>
                    {code}
                  </option>
                ))}
              </Input>
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
                placeholder="Enter container command"
                value={containerData.command}
                onChange={handleInputChange}
              />
            </FormGroup>
          </Col>
        </Row>

        <Row>
          <Col md={12}>
            <FormGroup>
              <Label for="parameters">Parameters</Label>
              <Input
                type="textarea"
                id="parameters"
                name="parameters"
                placeholder="Enter container parameters"
                value={containerData.parameters}
                onChange={handleInputChange}
                rows="2"
              />
            </FormGroup>
          </Col>
        </Row>

        <Row>
          <Col md={6}>
            <FormGroup check>
              <Label check>
                <Input
                  type="checkbox"
                  name="external"
                  checked={containerData.external}
                  onChange={handleInputChange}
                />
                {' '}External Container
              </Label>
            </FormGroup>
          </Col>
          <Col md={6}>
            <FormGroup check>
              <Label check>
                <Input
                  type="checkbox"
                  name="generatesProofs"
                  checked={containerData.generatesProofs}
                  onChange={handleInputChange}
                />
                {' '}Generates Proofs
              </Label>
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
            {loading ? 'Creating...' : 'Create Container'}
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

export default AddContainer;

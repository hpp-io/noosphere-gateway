import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { updateUser } from 'app/shared/reducers/user-management';
import { Alert, Button, Col, Form, FormGroup, Input, Label, Row } from 'reactstrap';

export const Profile = () => {
  const dispatch = useAppDispatch();

  const account = useAppSelector(state => state.authentication.account);
  const [formData, setFormData] = useState({
    firstName: account?.firstName || '',
    lastName: account?.lastName || '',
    email: account?.email || '',
  });

  const [successMessage, setSuccessMessage] = useState('');
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    // Pre-fill the form with user properties when account state updates
    setFormData({
      firstName: account?.firstName || '',
      lastName: account?.lastName || '',
      email: account?.email || '',
    });
  }, [account]);

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
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

  return (
    <div className="profile-page">
      <Row>
        <Col md="6" className="offset-md-3">
          <h2>My Profile</h2>
          {successMessage && <Alert color="success">{successMessage}</Alert>}

          <Form onSubmit={handleSubmit}>
            <FormGroup>
              <Label for="firstName">First Name</Label>
              <Input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                placeholder="Enter your first name"
              />
            </FormGroup>
            <FormGroup>
              <Label for="lastName">Last Name</Label>
              <Input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                placeholder="Enter your last name"
              />
            </FormGroup>
            <FormGroup>
              <Label for="email">Email</Label>
              <Input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="Enter your email address"
              />
            </FormGroup>
            <Button color="primary" type="submit" disabled={isSaving}>
              {isSaving ? 'Saving...' : 'Save Changes'}
            </Button>
          </Form>
        </Col>
      </Row>
    </div>
  );
};

export default Profile;
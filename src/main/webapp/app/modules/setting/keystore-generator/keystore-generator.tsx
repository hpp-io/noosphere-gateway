import React, { useState, useEffect } from 'react';
import { Button, FormGroup, Label, Input, Card, CardBody, CardTitle, Row, Col } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { createKeystore, validateKeystore, reset } from './keystore-generator.reducer';

const KeystoreGenerator = () => {
  const dispatch = useAppDispatch();
  const { loading, errorMessage, keystoreFile, fileName, validatedValue } = useAppSelector(state => state.keystoreGenerator);

  const [keyAlias, setKeyAlias] = useState('');
  const [password, setPassword] = useState('');
  const [privateKey, setPrivateKey] = useState('');

  const [validateFile, setValidateFile] = useState<File | null>(null);
  const [validateKeyAlias, setValidateKeyAlias] = useState('');
  const [validatePassword, setValidatePassword] = useState('');

  useEffect(() => {
    return () => {
      dispatch(reset());
    };
  }, []);

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
    }
  }, [keystoreFile, fileName, dispatch]);

  const handleGenerate = () => {
    dispatch(createKeystore({ keyAlias, password, privateKey }));
  };

  const handleValidate = () => {
    if (validateFile) {
      dispatch(validateKeystore({ file: validateFile, keyAlias: validateKeyAlias, password: validatePassword }));
    }
  };

  return (
    <Row>
      <Col md="6">
        <Card>
          <CardBody>
            <CardTitle tag="h2">Generate Keystore</CardTitle>
            <div>
              <FormGroup>
                <Label for="keyAlias">Key Alias</Label>
                <Input
                  type="text"
                  name="keyAlias"
                  id="keyAlias"
                  value={keyAlias}
                  onChange={e => setKeyAlias(e.target.value)}
                  required
                />
              </FormGroup>
              <FormGroup>
                <Label for="password">Keystore Password</Label>
                <Input
                  type="password"
                  name="password"
                  id="password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  required
                />
              </FormGroup>
              <FormGroup>
                <Label for="privateKey">Private Key</Label>
                <Input
                  type="textarea"
                  name="privateKey"
                  id="privateKey"
                  value={privateKey}
                  onChange={e => setPrivateKey(e.target.value)}
                  required
                />
              </FormGroup>
              <Button color="primary" onClick={handleGenerate} disabled={loading}>
                {loading ? 'Generating...' : 'Generate Keystore'}
              </Button>
            </div>
          </CardBody>
        </Card>
      </Col>
      <Col md="6">
        <Card>
          <CardBody>
            <CardTitle tag="h2">Validate Keystore</CardTitle>
            <div>
              <FormGroup>
                <Label for="validateFile">Keystore File</Label>
                <Input type="file" name="validateFile" id="validateFile" onChange={e => setValidateFile(e.target.files[0])} required />
              </FormGroup>
              <FormGroup>
                <Label for="validateKeyAlias">Key Alias</Label>
                <Input
                  type="text"
                  name="validateKeyAlias"
                  id="validateKeyAlias"
                  value={validateKeyAlias}
                  onChange={e => setValidateKeyAlias(e.target.value)}
                  required
                />
              </FormGroup>
              <FormGroup>
                <Label for="validatePassword">Keystore Password</Label>
                <Input
                  type="password"
                  name="validatePassword"
                  id="validatePassword"
                  value={validatePassword}
                  onChange={e => setValidatePassword(e.target.value)}
                  required
                />
              </FormGroup>
              {errorMessage && <p className="text-danger">{errorMessage}</p>}
              {validatedValue && (
                <div className="alert alert-success">
                  <p>Validation Successful!</p>
                  <p>
                    <strong>Retrieved Value:</strong> {validatedValue}
                  </p>
                </div>
              )}
              <Button color="primary" onClick={handleValidate} disabled={loading}>
                {loading ? 'Validating...' : 'Validate Keystore'}
              </Button>
            </div>
          </CardBody>
        </Card>
      </Col>
    </Row>
  );
};

export default KeystoreGenerator;

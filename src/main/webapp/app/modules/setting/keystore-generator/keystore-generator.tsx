import React, { useState, useEffect } from 'react';
import { Button, FormGroup, Label, Input, Card, CardBody, CardTitle, Row, Col } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { generateKeystore, readKeystore, reset } from './keystore-generator.reducer';

const KeystoreGenerator = () => {
  const dispatch = useAppDispatch();
  const { loading, errorMessage, keystoreFile, fileName, validatedValue } = useAppSelector(state => state.keystoreGenerator);

  const [keyAlias, setKeyAlias] = useState('');
  const [password, setPassword] = useState('');
  const [privateKey, setPrivateKey] = useState('');
  const [isWallet, setIsWallet] = useState(false);

  const [readFile, setReadFile] = useState<File | null>(null);
  const [readKeyAlias, setReadKeyAlias] = useState('');
  const [readPassword, setReadPassword] = useState('');
  const [readIsWallet, setReadIsWallet] = useState(false);

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
    dispatch(generateKeystore({ keyAlias, password, privateKey, isWallet }));
  };

  const handleRead = () => {
    if (readFile) {
      dispatch(readKeystore({ file: readFile, keyAlias: readKeyAlias, password: readPassword, isWallet: readIsWallet }));
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
              <br/>
              <FormGroup check>
                <Label check>
                  <Input type="checkbox" checked={isWallet} onChange={() => setIsWallet(!isWallet)} /> Is this a wallet key?
                </Label>
              </FormGroup>
              <br/>
              <br/>
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
            <CardTitle tag="h2">Read Keystore</CardTitle>
            <div>
              <FormGroup>
                <Label for="readFile">Keystore File</Label>
                <Input type="file" name="readFile" id="readFile" onChange={e => setReadFile(e.target.files[0])} required />
              </FormGroup>
              <FormGroup>
                <Label for="readKeyAlias">Key Alias</Label>
                <Input
                  type="text"
                  name="readKeyAlias"
                  id="readKeyAlias"
                  value={readKeyAlias}
                  onChange={e => setReadKeyAlias(e.target.value)}
                  required
                />
              </FormGroup>
              <FormGroup>
                <Label for="readPassword">Keystore Password</Label>
                <Input
                  type="password"
                  name="readPassword"
                  id="readPassword"
                  value={readPassword}
                  onChange={e => setReadPassword(e.target.value)}
                  required
                />
              </FormGroup>
              <br/>
              <FormGroup check>
                <Label check>
                  <Input type="checkbox" checked={readIsWallet} onChange={() => setReadIsWallet(!readIsWallet)} /> Is this a wallet key?
                </Label>
              </FormGroup>
              <br/>
              <br/>
              {errorMessage && <p className="text-danger">{errorMessage}</p>}
              {validatedValue && (
                <div className="alert alert-success">
                  <p>Read Successful!</p>
                  <p>
                    <strong>Retrieved Value:</strong> {validatedValue}
                  </p>
                </div>
              )}
              <Button color="primary" onClick={handleRead} disabled={loading}>
                {loading ? 'Reading...' : 'Read Keystore'}
              </Button>
            </div>
          </CardBody>
        </Card>
      </Col>
    </Row>
  );
};

export default KeystoreGenerator;

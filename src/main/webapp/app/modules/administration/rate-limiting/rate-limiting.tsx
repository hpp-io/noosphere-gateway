import React, { useState, useEffect } from 'react';
import { Table, Input, Button } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getBuckets, getConfigs, updateConfig, reset } from './rate-limiting.reducer';

const RateLimiting = () => {
  const dispatch = useAppDispatch();

  const buckets = useAppSelector(state => state.rateLimiting.buckets);
  const configs = useAppSelector(state => state.rateLimiting.configs);
  const updateSuccess = useAppSelector(state => state.rateLimiting.updateSuccess);

  const [localConfigs, setLocalConfigs] = useState({});

  useEffect(() => {
    dispatch(getBuckets());
    dispatch(getConfigs());
    return () => {
      dispatch(reset());
    };
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      dispatch(getBuckets());
      dispatch(getConfigs());
    }
  }, [updateSuccess]);

  useEffect(() => {
    setLocalConfigs(configs);
  }, [configs]);

  const handleConfigUpdate = (key, config) => {
    dispatch(updateConfig({ key, config }));
  };

  const handleInputChange = (key, field, value) => {
    setLocalConfigs({
      ...localConfigs,
      [key]: {
        ...localConfigs[key],
        [field]: value,
      },
    });
  };

  return (
    <div>
      <h2>Rate Limiting</h2>

      <h3>Buckets</h3>
      <Table>
        <thead>
          <tr>
            <th>Key</th>
            <th>Available Tokens</th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(buckets).map(([key, bucket]: [string, any]) => (
            <tr key={key}>
              <td>{key}</td>
              <td>{bucket.availableTokens}</td>
            </tr>
          ))}
        </tbody>
      </Table>

      <h3>Configurations</h3>
      <Table>
        <thead>
          <tr>
            <th>Key</th>
            <th>Capacity</th>
            <th>Refill Amount</th>
            <th>Refill Time (s)</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {Object.entries(localConfigs).map(([key, config]: [string, any]) => (
            <tr key={key}>
              <td>{key}</td>
              <td>
                <Input
                  type="number"
                  value={config.capacity}
                  onChange={(e) => handleInputChange(key, 'capacity', e.target.value)}
                />
              </td>
              <td>
                <Input
                  type="number"
                  value={config.refillAmount}
                  onChange={(e) => handleInputChange(key, 'refillAmount', e.target.value)}
                />
              </td>
              <td>
                <Input
                  type="number"
                  value={config.refillTimeInSeconds}
                  onChange={(e) => handleInputChange(key, 'refillTimeInSeconds', e.target.value)}
                />
              </td>
              <td>
                <Button onClick={() => handleConfigUpdate(key, config)}>Update</Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </div>
  );
};

export default RateLimiting;

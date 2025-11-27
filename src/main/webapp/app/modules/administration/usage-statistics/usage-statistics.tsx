import React, { useState, useEffect } from 'react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getUsageStatistics, reset } from './usage-statistics.reducer';
import { Table, Input, Button, DatePicker, Space } from 'antd';
import moment from 'moment';
import { Translate } from 'react-jhipster';
import { Dayjs } from 'dayjs';

const { RangePicker } = DatePicker;

export const UsageStatistics = () => {
  const dispatch = useAppDispatch();

  const usageStatisticsList = useAppSelector(state => state.usageStatistics.usageStatisticsList);
  const loading = useAppSelector(state => state.usageStatistics.loading);

  const [userId, setUserId] = useState('');
  const [apiKey, setApiKey] = useState('');
  const [apiGroup, setApiGroup] = useState('');
  const [startDate, setStartDate] = useState<Dayjs | null>(null);
  const [endDate, setEndDate] = useState<Dayjs | null>(null);

  useEffect(() => {
    dispatch(getUsageStatistics({}));
    return () => {
      dispatch(reset());
    };
  }, []);

  const handleFilter = () => {
    const start = startDate ? startDate.toISOString() : undefined;
    const end = endDate ? endDate.toISOString() : undefined;
    dispatch(getUsageStatistics({ userId, apiKey, apiGroup, startDate: start, endDate: end }));
  };

  const columns = [
    { title: 'Timestamp', dataIndex: 'timestamp', key: 'timestamp', render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss') },
    { title: 'User ID', dataIndex: 'userId', key: 'userId' },
    { title: 'API Key', dataIndex: 'apiKey', key: 'apiKey' },
    { title: 'API Group', dataIndex: 'apiGroup', key: 'apiGroup' },
    { title: 'Endpoint', dataIndex: 'endpoint', key: 'endpoint' },
    { title: 'Method', dataIndex: 'method', key: 'method' },
    { title: 'Status', dataIndex: 'status', key: 'status' },
    { title: 'Duration (ms)', dataIndex: 'duration', key: 'duration' },
  ];

  return (
    <div>
      <h2 id="usage-statistics-heading">
        <Translate contentKey="global.menu.admin.usageStatistics">Usage Statistics</Translate>
      </h2>
      <Space style={{ marginBottom: 16 }}>
        <Input placeholder="User ID" value={userId} onChange={e => setUserId(e.target.value)} />
        <Input placeholder="API Key" value={apiKey} onChange={e => setApiKey(e.target.value)} />
        <Input placeholder="API Group" value={apiGroup} onChange={e => setApiGroup(e.target.value)} />
        <RangePicker
          showTime
          format="YYYY-MM-DD HH:mm:ss"
          onChange={dates => {
            setStartDate(dates ? dates[0] : null);
            setEndDate(dates ? dates[1] : null);
          }}
        />
        <Button type="primary" onClick={handleFilter}>
          Filter
        </Button>
      </Space>
      <Table dataSource={usageStatisticsList} columns={columns} rowKey="timestamp" loading={loading} />
    </div>
  );
};

export default UsageStatistics;

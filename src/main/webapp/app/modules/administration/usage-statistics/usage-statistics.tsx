import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { searchUsageStatistics, reset } from './usage-statistics.reducer';
import { Table, Input, Button, DatePicker, Space } from 'antd';
import moment from 'moment';
import { Translate } from 'react-jhipster';
import { Dayjs } from 'dayjs';
import { JhiPagination } from 'react-jhipster';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

const { RangePicker } = DatePicker;

export const UsageStatistics = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const navigate = useNavigate();

  const usageStatisticsList = useAppSelector(state => state.usageStatistics.usageStatisticsList);
  const loading = useAppSelector(state => state.usageStatistics.loading);
  const totalItems = useAppSelector(state => state.usageStatistics.totalItems);

  const [userId, setUserId] = useState('');
  const [apiKey, setApiKey] = useState('');
  const [apiGroup, setApiGroup] = useState('');
  const [startDate, setStartDate] = useState<Dayjs | null>(null);
  const [endDate, setEndDate] = useState<Dayjs | null>(null);
  const [pagination, setPagination] = useState({
    activePage: 1,
    itemsPerPage: ITEMS_PER_PAGE,
    sort: 'id',
    order: 'desc',
  });

  const getAllEntities = () => {
    const start = startDate ? startDate.toISOString() : undefined;
    const end = endDate ? endDate.toISOString() : undefined;
    dispatch(
      searchUsageStatistics({
        userId,
        apiKey,
        apiGroup,
        startDate: start,
        endDate: end,
        page: pagination.activePage - 1,
        size: pagination.itemsPerPage,
        sort: `${pagination.sort},${pagination.order}`,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${pagination.activePage}&sort=${pagination.sort},${pagination.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [pagination.activePage, pagination.order, pagination.sort, userId, apiKey, apiGroup, startDate, endDate]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get('sort');
    if (page) {
      setPagination(p => ({ ...p, activePage: parseInt(page, 10) }));
    }
    if (sort) {
      const [sortField, sortOrder] = sort.split(',');
      setPagination(p => ({ ...p, sort: sortField, order: sortOrder }));
    }
  }, [location.search]);

  useEffect(() => {
    return () => {
      dispatch(reset());
    };
  }, []);

  const handleFilter = () => {
    setPagination({ ...pagination, activePage: 1 });
  };

  const handlePagination = page => {
    setPagination({ ...pagination, activePage: page });
  };

  const handleTableChange = (newPagination, filters, sorter) => {
    const sort = sorter.field;
    const order = sorter.order === 'ascend' ? ASC : DESC;
    if (sort && order) {
      setPagination({ ...pagination, sort, order, activePage: 1 });
    }
  };

  const columns = [
    {
      title: 'Timestamp',
      dataIndex: 'timestamp',
      key: 'timestamp',
      render: (text: string) => moment(text).format('YYYY-MM-DD HH:mm:ss'),
      sorter: true,
    },
    { title: 'User ID', dataIndex: 'userId', key: 'userId', sorter: true },
    { title: 'API Key', dataIndex: 'apiKey', key: 'apiKey', sorter: true },
    { title: 'API Group', dataIndex: 'apiGroup', key: 'apiGroup', sorter: true },
    { title: 'Endpoint', dataIndex: 'endpoint', key: 'endpoint', sorter: true },
    { title: 'Method', dataIndex: 'method', key: 'method', sorter: true },
    { title: 'Status', dataIndex: 'status', key: 'status', sorter: true },
    { title: 'Duration (ms)', dataIndex: 'duration', key: 'duration', sorter: true },
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
      <Table
        dataSource={usageStatisticsList}
        columns={columns}
        rowKey="id"
        loading={loading}
        pagination={false}
        onChange={handleTableChange}
      />
      {totalItems > 0 ? (
        <div className="d-flex justify-content-center">
          <JhiPagination
            activePage={pagination.activePage}
            onSelect={handlePagination}
            maxButtons={5}
            itemsPerPage={pagination.itemsPerPage}
            totalItems={totalItems}
          />
        </div>
      ) : null}
    </div>
  );
};

export default UsageStatistics;

import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Space, Table, Input, Button } from 'antd';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { JhiPagination } from 'react-jhipster';
import { searchRateLimits, IRateLimit } from './rate-limit.reducer';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';

export const RateLimit = () => {
  const dispatch = useAppDispatch();
  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [pagination, setPagination] = useState({
    activePage: 1,
    itemsPerPage: ITEMS_PER_PAGE,
    sort: 'apiKey',
    order: 'asc',
  });

  const rateLimitList = useAppSelector(state => state.rateLimit.rateLimits);
  const loading = useAppSelector(state => state.rateLimit.loading);
  const totalItems = useAppSelector(state => state.rateLimit.totalItems);

  const getAllEntities = () => {
    dispatch(
      searchRateLimits({
        apiKey: search,
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
  }, [pagination.activePage, pagination.order, pagination.sort, search]);

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

  const handleSearch = () => {
    setPagination({ ...pagination, activePage: 1 });
  };

  const clear = () => {
    setSearch('');
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
      title: <Translate contentKey="nsgw.rateLimit.apiKey" />,
      dataIndex: 'apiKey',
      key: 'apiKey',
      render: (text: string) => <Link to={text}>{text}</Link>,
      sorter: true,
    },
    {
      title: <Translate contentKey="nsgw.rateLimit.availableTokens" />,
      dataIndex: 'availableTokens',
      key: 'availableTokens',
    },
    {
      title: <Translate contentKey="nsgw.rateLimit.callsPerSecond" />,
      dataIndex: 'callsPerSecond',
      key: 'callsPerSecond',
    },
    {
      title: <Translate contentKey="nsgw.rateLimit.callsPerMinute" />,
      dataIndex: 'callsPerMinute',
      key: 'callsPerMinute',
    },
    {
      title: <Translate contentKey="nsgw.rateLimit.callsPerHour" />,
      dataIndex: 'callsPerHour',
      key: 'callsPerHour',
    },
    {
      title: <Translate contentKey="nsgw.rateLimit.callsPerDay" />,
      dataIndex: 'callsPerDay',
      key: 'callsPerDay',
    },
    {
      title: '',
      key: 'action',
      render: (_, record: IRateLimit) => (
        <Space size="middle">
          <Link to={`${record.apiKey}/edit`} data-cy="entityEditButton">
            <Button type="primary" size="small">
              <FontAwesomeIcon icon="pencil-alt" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.edit" />
              </span>
            </Button>
          </Link>
          <Link to={`${record.apiKey}/delete`} data-cy="entityDeleteButton">
            <Button danger size="small">
              <FontAwesomeIcon icon="trash" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.delete" />
              </span>
            </Button>
          </Link>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <h2 id="rate-limit-heading" data-cy="RateLimitHeading">
        <Translate contentKey="nsgw.rateLimit.home.title">Rate Limits</Translate>
        <div className="d-flex justify-content-end">
          <Link to="new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="nsgw.rateLimit.home.createLabel">Create new Rate Limit</Translate>
          </Link>
        </div>
      </h2>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder={translate('nsgw.rateLimit.home.search')}
          value={search}
          onChange={e => setSearch(e.target.value)}
          onPressEnter={handleSearch}
        />
        <Button type="primary" onClick={handleSearch}>
          <FontAwesomeIcon icon="search" />
        </Button>
        <Button onClick={clear}>
          <FontAwesomeIcon icon="trash" />
        </Button>
      </Space>

      <div className="table-responsive">
        <Table
          columns={columns}
          dataSource={rateLimitList}
          rowKey="apiKey"
          loading={loading}
          pagination={false}
          onChange={handleTableChange}
          locale={{
            emptyText: !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="nsgw.rateLimit.home.notFound">No Rate Limits found</Translate>
              </div>
            ),
          }}
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
    </div>
  );
};

export default RateLimit;

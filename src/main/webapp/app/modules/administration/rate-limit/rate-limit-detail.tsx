import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getRateLimit } from './rate-limit.reducer';

export const RateLimitDetail = () => {
  const dispatch = useAppDispatch();
  const { id } = useParams<{ id: string }>();

  useEffect(() => {
    dispatch(getRateLimit(id));
  }, []);

  const rateLimitEntity = useAppSelector(state => state.rateLimit.rateLimit);

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="rateLimitDetailsHeading">
          <Translate contentKey="nsgw.rateLimit.detail.title">Rate Limit</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="apiKey">
              <Translate contentKey="nsgw.rateLimit.apiKey">Api Key</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.key}</dd>
          <dt>
            <span id="availableTokens">
              <Translate contentKey="nsgw.rateLimit.availableTokens">Available Tokens</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.bucket?.availableTokens}</dd>
          <dt>
            <span id="callsPerSecond">
              <Translate contentKey="nsgw.rateLimit.callsPerSecond">Calls Per Second</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.rateLimitConfig?.callsPerSecond}</dd>
          <dt>
            <span id="callsPerMinute">
              <Translate contentKey="nsgw.rateLimit.callsPerMinute">Calls Per Minute</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.rateLimitConfig?.callsPerMinute}</dd>
          <dt>
            <span id="callsPerHour">
              <Translate contentKey="nsgw.rateLimit.callsPerHour">Calls Per Hour</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.rateLimitConfig?.callsPerHour}</dd>
          <dt>
            <span id="callsPerDay">
              <Translate contentKey="nsgw.rateLimit.callsPerDay">Calls Per Day</Translate>
            </span>
          </dt>
          <dd>{rateLimitEntity.rateLimitConfig?.callsPerDay}</dd>
        </dl>
        <Button tag={Link} to="/admin/rate-limit" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/admin/rate-limit/${rateLimitEntity.key}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default RateLimitDetail;

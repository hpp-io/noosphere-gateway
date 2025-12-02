import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getRateLimit, updateRateLimit, createRateLimit, reset } from './rate-limit.reducer';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const RateLimitUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const isNew = id === undefined;

  const rateLimitEntity = useAppSelector(state => state.rateLimit.rateLimit);
  const loading = useAppSelector(state => state.rateLimit.loading);
  const updating = useAppSelector(state => state.rateLimit.updating);
  const updateSuccess = useAppSelector(state => state.rateLimit.updateSuccess);

  const handleClose = () => {
    navigate('/admin/rate-limit');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getRateLimit(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = (values: any) => {
    const entity = {
      ...rateLimitEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createRateLimit(entity));
    } else {
      dispatch(updateRateLimit(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...rateLimitEntity,
          apiKey: rateLimitEntity.apiKey,
          availableTokens: rateLimitEntity.availableTokens,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nsgw.rateLimit.home.createOrEditLabel" data-cy="RateLimitCreateUpdateHeading">
            <Translate contentKey="nsgw.rateLimit.home.createOrEditLabel">Create or edit a RateLimit</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              <ValidatedField
                label={translate('nsgw.rateLimit.apiKey')}
                id="rate-limit-apiKey"
                name="apiKey"
                data-cy="apiKey"
                type="text"
                readOnly={!isNew}
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nsgw.rateLimit.callsPerSecond')}
                id="rate-limit-callsPerSecond"
                name="callsPerSecond"
                data-cy="callsPerSecond"
                type="text"
                validate={{
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nsgw.rateLimit.callsPerMinute')}
                id="rate-limit-callsPerMinute"
                name="callsPerMinute"
                data-cy="callsPerMinute"
                type="text"
                validate={{
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nsgw.rateLimit.callsPerHour')}
                id="rate-limit-callsPerHour"
                name="callsPerHour"
                data-cy="callsPerHour"
                type="text"
                validate={{
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nsgw.rateLimit.callsPerDay')}
                id="rate-limit-callsPerDay"
                name="callsPerDay"
                data-cy="callsPerDay"
                type="text"
                validate={{
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/admin/rate-limit" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default RateLimitUpdate;

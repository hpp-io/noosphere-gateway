import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getRateLimit, deleteRateLimit } from './rate-limit.reducer';

export const RateLimitDeleteDialog = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getRateLimit(id));
    setLoadModal(true);
  }, []);

  const rateLimitEntity = useAppSelector(state => state.rateLimit.rateLimit);
  const updateSuccess = useAppSelector(state => state.rateLimit.updateSuccess);

  const handleClose = () => {
    navigate('../..');
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmDelete = () => {
    dispatch(deleteRateLimit(rateLimitEntity.apiKey));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="rateLimitDeleteDialogHeading">
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="nsgw.rateLimit.delete.question">
        <Translate contentKey="nsgw.rateLimit.delete.question" interpolate={{ apiKey: rateLimitEntity.apiKey }}>
          Are you sure you want to delete this RateLimit?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-rateLimit" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default RateLimitDeleteDialog;

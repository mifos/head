package org.mifos.application.master.persistence.service;

import java.util.List;

import org.mifos.application.master.business.EntityMaster;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.service.PersistenceService;

public class MasterPersistenceService extends PersistenceService {

	private MasterPersistence serviceImpl = new MasterPersistence();

	

	public EntityMaster retrieveMasterData(String entityName, Short localeId,
			String classPath, String column) throws SystemException,
			ApplicationException {
		EntityMaster entityMaster = null;
		entityMaster = serviceImpl.getLookUpEntity(entityName, localeId,
				classPath, column);
		return entityMaster;
	}

	public MasterDataEntity findById(Class clazz, Short pk)
			throws PersistenceException {
		return (MasterDataEntity) serviceImpl.getPersistentObject(clazz, pk);
	}

	public List<PaymentTypeEntity> retrievePaymentTypes(Short localeId)
			throws PersistenceException {
		return serviceImpl.retrievePaymentTypes(localeId);
	}

	public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId,
			Short trxnTypeId) throws PersistenceException {
		return serviceImpl.getSupportedPaymentModes(localeId, trxnTypeId);
	}

	public List<MasterDataEntity> retrieveMasterEntities(Class entityName,
			Short localeId) throws PersistenceException {
		return serviceImpl.retrieveMasterEntities(entityName, localeId);
	}
}

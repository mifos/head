package org.mifos.application.master.persistence.service;

import java.util.List;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.service.PersistenceService;

public class MasterPersistenceService extends PersistenceService {

	private MasterPersistence serviceImpl = new MasterPersistence();
	/**
	 * This method obtains a the values corresponding to a locale for a particular entity. Eg: If entity is Salutation
	 * Then this method retrieves values Mr, Mrs, Ms for locale english and Monsieur, Madame for french locale
	 * @param entityId Id denoting the entity(Eg: Salutation)
	 * @param localeId The locale for which values have to be obtained
	 * @param searchResultName The name under which it will be put into the search results
	 * @return Search Result object
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public EntityMaster retrieveMasterData(String entityName,Short localeId) throws SystemException,ApplicationException{
		EntityMaster entityMaster = null;
		entityMaster = serviceImpl.getLookUpEntity(entityName,localeId);
		return entityMaster;
	}

	public EntityMaster  retrieveMasterData(String entityName, Short localeId, String classPath, String column )
			throws SystemException,ApplicationException{
		EntityMaster entityMaster = null;
		entityMaster = serviceImpl.getLookUpEntity(entityName,localeId,classPath,column);
		return entityMaster;
	}

	public MasterDataEntity findById(Class clazz,Short pk){
		return serviceImpl.findById(clazz,pk);
	}
	
	public List<PaymentTypeEntity> retrievePaymentTypes(Short localeId)throws PersistenceException{
		return serviceImpl.retrievePaymentTypes(localeId);
	}
	
	public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId, Short trxnTypeId)throws PersistenceException{
		return serviceImpl.getSupportedPaymentModes(localeId, trxnTypeId);
	}	
	
	public List<MasterDataEntity> retrieveMasterEntities(Class entityName, Short localeId) throws PersistenceException {
		return serviceImpl.retrieveMasterEntities(entityName, localeId);
	}
}

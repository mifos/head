package org.mifos.application.master.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.EntityMaster;
import org.mifos.application.master.business.LookUpMaster;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.TransactionTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

/**
 * This class is mostly used to look up instances of (a subclass of)
 * {@link MasterDataEntity} in the database.  Most of what is here
 * can better be accomplished by enums and by {@link MessageLookup}.
 */
public class MasterPersistence extends Persistence {

	public EntityMaster getLookUpEntity(String entityName, Short localeId)
			throws ApplicationException, SystemException {
		try {
			Session session = HibernateUtil.getSessionTL();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
			queryEntity.setString("entityType", entityName);
			queryEntity.setShort("localeId", localeId);

			EntityMaster entity = (EntityMaster) queryEntity.uniqueResult();

			entity.setLookUpValues(lookUpValue(entityName, localeId, session));

			return entity;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private List<LookUpMaster> lookUpValue(String entityName, Short localeId,
			Session session) {
		Query queryEntity = session
				.getNamedQuery("masterdata.entitylookupvalue");
		queryEntity.setString("entityType", entityName);
		queryEntity.setShort("localeId", localeId);
		List<LookUpMaster> entityList = queryEntity.list();
		return entityList;
	}

	public short getLocaleId(Locale locale) {
		return Short.valueOf("1");
	}

	public EntityMaster getLookUpEntity(String entityName, Short localeId,
			String classPath, String column) throws ApplicationException,
			SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSessionTL();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
			queryEntity.setString("entityType", entityName);
			queryEntity.setShort("localeId", localeId);

			EntityMaster entity = (EntityMaster) queryEntity.uniqueResult();
			entity.setLookUpValues(lookUpValue(entityName, localeId, classPath,
					column, session));
			return entity;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private List<LookUpMaster> lookUpValue(String entityName, Short localeId,
			String entityClass, String column, Session session) {
		Query queryEntity = session.createQuery(
			"select new org.mifos.application.master.business.LookUpMaster(" +
			"mainTable." + column + " ,lookup.lookUpId,lookupvalue.lookUpValue) " +
			"from org.mifos.application.master.business.LookUpValueEntity lookup," +
			"org.mifos.application.master.business.LookUpValueLocaleEntity lookupvalue," +
			entityClass + " mainTable " +
			"where mainTable.lookUpId = lookup.lookUpId" +
			" and lookup.lookUpEntity.entityType = ?" +
			" and lookup.lookUpId = lookupvalue.lookUpId" +
			" and lookupvalue.localeId = ?");
		queryEntity.setString(0, entityName);
		queryEntity.setShort(1, localeId);
		List<LookUpMaster> entityList = queryEntity.list();
		return entityList;
	}

	public List<PaymentTypeEntity> retrievePaymentTypes(Short localeId)
			throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			List<PaymentTypeEntity> paymentTypes = session
					.createQuery(
							"from org.mifos.application.master.business.PaymentTypeEntity")
					.list();
			for (PaymentTypeEntity paymentType : paymentTypes) {
				paymentType.setLocaleId(localeId);
			}
			return paymentTypes;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId,
			Short transactionTypeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("TRANSACTION_ID", transactionTypeId);
		List<PaymentTypeEntity> paymentTypes = ((TransactionTypeEntity) executeNamedQuery(
				NamedQueryConstants.GET_PAYMENT_TYPES, queryParameters).get(0))
				.getApplicablePaymentTypes();
		for (PaymentTypeEntity paymentType : paymentTypes) {
			paymentType.setLocaleId(localeId);
		}
		return paymentTypes;
	}

	public List<MasterDataEntity> retrieveMasterEntities(Class clazz,
			Short localeId) throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			List<MasterDataEntity> masterEntities = session.createQuery(
					"from " + clazz.getName()).list();
			for (MasterDataEntity masterData : masterEntities) {
				initialize(masterData.getNames());
				masterData.setLocaleId(localeId);
			}
			return masterEntities;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public MasterDataEntity retrieveMasterEntity(Short entityId, Class clazz,
			Short localeId) throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			List<MasterDataEntity> masterEntity = session.createQuery(
					"from " + clazz.getName()
							+ " masterEntity where masterEntity.id = "
							+ entityId).list();
			if (masterEntity != null && masterEntity.size() > 0){
				MasterDataEntity masterDataEntity =  masterEntity.get(0);
				masterDataEntity.setLocaleId(localeId);
				initialize(masterDataEntity.getNames());
				return masterDataEntity;
			}
			throw new PersistenceException("errors.entityNotFound");
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			EntityType entityType) throws PersistenceException {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(MasterConstants.ENTITY_TYPE, entityType
					.getValue());
			return executeNamedQuery(
					NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
		
	}
	
	public List<BusinessActivityEntity> retrieveMasterEntities(
			String entityName, Short localeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("entityType", entityName);
		queryParameters.put("localeId", localeId);
		return executeNamedQuery(
			NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE,
			queryParameters);
	}

	public String retrieveMasterEntities(Integer entityId, Short localeId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("lookUpId", entityId);
		queryParameters.put("localeId", localeId);
		List queryResult = executeNamedQuery(
			NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_NAME,
			queryParameters);
		return (String) queryResult.get(0);
	}
	
	public List<MasterDataEntity> retrieveMasterDataEntity(String classPath)
			throws PersistenceException {
		List<MasterDataEntity> queryResult = null;
		try {
			queryResult = HibernateUtil
			.getSessionTL()
			.createQuery(
					"from "+classPath)
			.list();
		} catch (Exception he) {
			throw new PersistenceException(he);
		}
		return queryResult;
	}
	
	public MasterDataEntity getMasterDataEntity(Class clazz, Short id) 
	throws PersistenceException {
		return (MasterDataEntity)getPersistentObject(clazz, id);
	}

}

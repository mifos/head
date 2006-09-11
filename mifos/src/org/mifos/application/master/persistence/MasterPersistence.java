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
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.TransactionTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class MasterPersistence extends Persistence {

	public EntityMaster getLookUpEntity(String entityName, Short localeId)
			throws ApplicationException, SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSessionTL();

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
			String classPath, String column, Session session) {

		String q = "select new org.mifos.application.master.util.valueobjects.LookUpMaster(mainTable.";
		String q2 = " ,lookup.lookUpId,lookupvalue.lookUpValue) from org.mifos.application.master.util.valueobjects.LookUpValue lookup,org.mifos.application.master.util.valueobjects.LookUpValueLocale lookupvalue,";
		String q3 = " mainTable where mainTable.lookUpId =lookup.lookUpId and lookup.lookUpEntity.entityType =? and lookup.lookUpId=lookupvalue.lookUpId and lookupvalue.localeId=?";

		q = q + column + q2 + classPath + q3;

		Query queryEntity = session.createQuery(q);
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
			new PersistenceException();
		}
		return null;
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
				masterData.setLocaleId(localeId);
			}
			return masterEntities;
		} catch (HibernateException he) {
			new PersistenceException(he);
		}
		return null;
	}

	public MasterDataEntity retrieveMasterEntity(Short entityId, Class clazz,
			Short localeId) throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			List<MasterDataEntity> masterEntity = session.createQuery(
					"from " + clazz.getName()
							+ " masterEntity where masterEntity.id = "
							+ entityId).list();
			if (masterEntity != null && masterEntity.size() > 0)
				return masterEntity.get(0);
			throw new PersistenceException("errors.entityNotFound");
		} catch (HibernateException he) {
			new PersistenceException(he);
		}
		return null;
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			EntityType entityType) throws PersistenceException {
		try {
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(MasterConstants.ENTITY_TYPE, entityType
					.getValue());
			return (List<CustomFieldDefinitionEntity>) executeNamedQuery(
					NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public List<BusinessActivityEntity> retrieveMasterEntities(
			String entityName, Short localeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("entityType", entityName);
		queryParameters.put("localeId", localeId);
		List<BusinessActivityEntity> queryResult = null;
		try {
			queryResult = executeNamedQuery(
					NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE,
					queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return queryResult;
	}

	public String retrieveMasterEntities(Integer entityId, Short localeId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("lookUpId", entityId);
		queryParameters.put("localeId", localeId);
		List queryResult = null;
		try {
			queryResult = executeNamedQuery(
					NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_NAME,
					queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return (String) queryResult.get(0);
	}
}

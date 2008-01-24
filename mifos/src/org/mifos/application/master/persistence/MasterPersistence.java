package org.mifos.application.master.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.config.Localization;

/**
 * This class is mostly used to look up instances of (a subclass of)
 * {@link MasterDataEntity} in the database.  Most of what is here
 * can better be accomplished by enums and by {@link MessageLookup}.
 * 
 * Test cases: {@link TestMasterPersistence}
 */
public class MasterPersistence extends Persistence {

	/**
	 * Only two non-test usages, one that may never be called and one for 
	 * getting labels. 
	 */
	public CustomValueList getLookUpEntity(String entityName, Short localeId)
			throws ApplicationException, SystemException {
		try {
			Session session = HibernateUtil.getSessionTL();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
			queryEntity.setString("entityType", entityName);
			queryEntity.setShort("localeId", localeId);

			CustomValueList entity = (CustomValueList) queryEntity
					.uniqueResult();

			entity.setCustomValueListElements(lookUpValue(entityName, localeId,
					session));

			return entity;
		}
		catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private List<CustomValueListElement> lookUpValue(String entityName,
			Short localeId, Session session) {
		Query queryEntity = session
				.getNamedQuery("masterdata.entitylookupvalue");
		queryEntity.setString("entityType", entityName);
		queryEntity.setShort("localeId", localeId);
		List<CustomValueListElement> entityList = queryEntity.list();
		return entityList;
	}

	
	public short getLocaleId(Locale locale) {
		return Localization.getInstance().getLocaleId();
	}

	/**
	 * Used once in getMasterData, otherwise, test usage and one other 
	 * method MifosPropertyMessageResources.getCustomValueListElements 
	 * (and that method may never be called)
	 */
	public CustomValueList getCustomValueList(String entityName,
			Short localeId, String classPath, String column)
			throws ApplicationException, SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSessionTL();

			Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
			queryEntity.setString("entityType", entityName);
			queryEntity.setShort("localeId", localeId);

			CustomValueList entity = (CustomValueList) queryEntity
					.uniqueResult();
			entity.setCustomValueListElements(getCustomValueListElements(
					entityName, localeId, classPath, column, session));
			return entity;
		}
		catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private List<CustomValueListElement> getCustomValueListElements(
			String entityName, Short localeId, String entityClass,
			String column, Session session) {
		Query queryEntity = session
				.createQuery("select new org.mifos.application.master.business.CustomValueListElement("
						+ "mainTable."
						+ column
						+ " ,lookup.lookUpId,lookupvalue.lookUpValue) "
						+ "from org.mifos.application.master.business.LookUpValueEntity lookup,"
						+ "org.mifos.application.master.business.LookUpValueLocaleEntity lookupvalue,"
						+ entityClass
						+ " mainTable "
						+ "where mainTable.lookUpId = lookup.lookUpId"
						+ " and lookup.lookUpEntity.entityType = ?"
						+ " and lookup.lookUpId = lookupvalue.lookUpId"
						+ " and lookupvalue.localeId = ?");
		queryEntity.setString(0, entityName);
		
		// Jan 16, 2008 work in progress
		// all override or custom values are now stored in locale 1
		// queryEntity.setShort(1, localeId);
		queryEntity.setShort(1, (short)1);
		List<CustomValueListElement> entityList = queryEntity.list();
		
		/*
		 * Now go through the list and if there is no text value for the element
		 * then use the default localized value.
		 */
		
		for (CustomValueListElement valueListElement : entityList) {
			String lookupValue = valueListElement.getLookUpValue();
			if (lookupValue == null || 
				(lookupValue != null && lookupValue.length() == 0)) {
				String key = entityName + "." + valueListElement.getLookUpId();
				String localizedValue = MessageLookup.getInstance().lookup(key);
				valueListElement.setLookupValue(localizedValue);
			}
		}
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
		}
		catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	/*public List<PaymentTypeEntity> getSupportedPaymentModes(Short localeId,
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
	 }*/

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
		}
		catch (Exception e) {
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
			if (masterEntity != null && masterEntity.size() > 0) {
				MasterDataEntity masterDataEntity = masterEntity.get(0);
				masterDataEntity.setLocaleId(localeId);
				initialize(masterDataEntity.getNames());
				return masterDataEntity;
			}
			throw new PersistenceException("errors.entityNotFound");
		}
		catch (Exception he) {
			throw new PersistenceException(he);
		}
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			EntityType entityType) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(MasterConstants.ENTITY_TYPE, entityType.getValue());
		return executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS,
				queryParameters);

	}

	public CustomFieldDefinitionEntity retrieveOneCustomFieldDefinition(
			Short fieldId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("fieldId", fieldId);
		return (CustomFieldDefinitionEntity) execUniqueResultNamedQuery(
				NamedQueryConstants.RETRIEVE_ONE_CUSTOM_FIELD, queryParameters);

	}

	/**
	 * This method is used to retrieve both custom and fixed value list elements.
	 */
	public List<ValueListElement> retrieveMasterEntities(
			String entityName, Short localeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("entityType", entityName);
		queryParameters.put("localeId", localeId);
		List<ValueListElement> elements = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE,
				queryParameters);
		for (ValueListElement valueListElement : elements) {
			String name = valueListElement.getName();
			if (name == null || 
				(name != null && name.length() == 0)) {
				String key = entityName + "." + valueListElement.getValueKey();
				String localizedValue = MessageLookup.getInstance().lookup(key);
				valueListElement.setName(localizedValue);
			}
		}
		return elements;

	}

	public String retrieveMasterEntities(Integer entityId, Short localeId)
			throws PersistenceException {
		String masterEntity = null;
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("lookUpId", entityId);
		queryParameters.put("localeId", localeId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_NAME,
				queryParameters);
		if ((queryResult != null) && (queryResult.size() > 0))
			masterEntity = (String) queryResult.get(0);
		return masterEntity;
	}

	public List<MasterDataEntity> retrieveMasterDataEntity(String classPath)
			throws PersistenceException {
		List<MasterDataEntity> queryResult = null;
		try {
			queryResult = HibernateUtil.getSessionTL().createQuery(
					"from " + classPath).list();
		}
		catch (Exception he) {
			throw new PersistenceException(he);
		}
		return queryResult;
	}

	public MasterDataEntity getMasterDataEntity(Class clazz, Short id)
			throws PersistenceException {
		return (MasterDataEntity) getPersistentObject(clazz, id);
	}

	/**
	 * Update the String value of a LookUpValueLocaleEntity.
	 * @param id - the database id of the LookUpValueLocaleEntity object representing a ValueListElement
	 */
	public void updateValueListElementForLocale(Integer lookupValueEntityId,
			Short localeId, String newValue) throws PersistenceException {
		LookUpValueEntity lookupValueEntity = (LookUpValueEntity) getPersistentObject(
				LookUpValueEntity.class, lookupValueEntityId);

		;
		for (LookUpValueLocaleEntity valueForLocale : lookupValueEntity
				.getLookUpValueLocales()) {
			if (valueForLocale.getLocaleId().equals(localeId)) {
				valueForLocale.setLookUpValue(newValue);
				createOrUpdate(valueForLocale);
				break;
			}
		}
	}

	/**
	 * Create a new list element for a single locale.
	 * 
	 * It would be nicer for this to operate on objects rather than
	 * ids, but it is a first step that works.
	 */
	public void addValueListElementForLocale(Short lookupEnityId,
			String newElementText, short localeId) throws PersistenceException {
		MifosLookUpEntity lookUpEntity = (MifosLookUpEntity) getPersistentObject(
				MifosLookUpEntity.class, lookupEnityId);
		LookUpValueEntity lookUpValueEntity = new LookUpValueEntity();
		lookUpValueEntity.setLookUpEntity(lookUpEntity);
		createOrUpdate(lookUpValueEntity);

		LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
		lookUpValueLocaleEntity.setLocaleId(localeId);
		lookUpValueLocaleEntity.setLookUpValue(newElementText);
		lookUpValueLocaleEntity.setLookUpId(lookUpValueEntity.getLookUpId());
		createOrUpdate(lookUpValueLocaleEntity);
	}

	/**
	 * This method is intended to delete a single LookUpValueEntity and all
	 * its associated LookUpValueLocaleEntity objects.  The primary purpose is for 
	 * test script cleanup, since deletion of LookUpValueEntity elements is 
	 * not allowed in the main app.
	 * 
	 * It would be nicer for this to operate on objects rather than
	 * ids, but it is a first step that works.
	 */
	public void deleteValueListElement(Integer lookupValueEntityId)
			throws PersistenceException {
		LookUpValueEntity lookUpValueEntity = (LookUpValueEntity) getPersistentObject(
				LookUpValueEntity.class, lookupValueEntityId);

		// the cascade property defined for lookUpValueLocales member of LookUpValueEntity
		// means that deleting the LookUpValueEntity should delete all the associated 
		// LookUpValueLocaleEntity objects as well.
		delete(lookUpValueEntity);
	}

	/*
	 * Return a list of the names of the categories of objects that can
	 * have custom fields added (as specified in {@link CustomFieldCategory})
	 */
	public List<String> getCustomFieldCategories() {
		List<String> categories = new ArrayList<String>();
		for (CustomFieldCategory category : CustomFieldCategory.values()) {
			categories.add(category.toString());
		}
		return categories;
	}

	public void addLookUpEntity(MifosLookUpEntity lookUpEntity)
			throws PersistenceException {

		createOrUpdate(lookUpEntity);
	}

	public LookUpValueLocaleEntity retrieveOneLookUpValueLocaleEntity(
			short localeId, int lookUpId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("aLocaleId", new SupportedLocalesEntity(Short
				.valueOf(localeId)));
		queryParameters.put("aLookUpId", lookUpId);
		Object obj = execUniqueResultNamedQuery(
				NamedQueryConstants.GETLOOKUPVALUELOCALE, queryParameters);
		if (null != obj) {
			return (LookUpValueLocaleEntity) obj;
		}
		return null;
	}


}

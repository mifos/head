package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.application.master.util.valueobjects.LookUpLabel;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class AuditConfigurationPersistence extends Persistence {

	public Map<String, String> retrieveAllCustomFieldsDefinition(Short localeId)
			throws PersistenceException {
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			List<CustomFieldDefinitionEntity> customFields = (List<CustomFieldDefinitionEntity>) executeNamedQuery(
					NamedQueryConstants.RETRIEVE_ALL_CUSTOM_FIELDS, null);
			for (Iterator<CustomFieldDefinitionEntity> iter = customFields
					.iterator(); iter.hasNext();) {
				CustomFieldDefinitionEntity customFieldDefinitionEntity = iter
						.next();
				LookUpEntity lookUpEntity = customFieldDefinitionEntity
						.getLookUpEntity();
				Set<LookUpLabel> lookUpLabelSet = lookUpEntity
						.getLookUpLabelSet();
				for (Iterator<LookUpLabel> iterator = lookUpLabelSet.iterator(); iterator
						.hasNext();) {
					LookUpLabel lookUpLabel = iterator.next();
					if (lookUpLabel.getLocaleId().equals(localeId)) {
						valueMap.put(customFieldDefinitionEntity.getFieldId()
								.toString(), lookUpLabel.getLabelName());
					}
				}
			}
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return valueMap;

	}
	
	public Map<String, String>  retrieveProductStatus(Short localeId) throws PersistenceException{
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			List<PrdStatusEntity> productStates = (List<PrdStatusEntity>) executeNamedQuery(
					NamedQueryConstants.ALL_PRD_STATES, null);
			for (Iterator<PrdStatusEntity> iter = productStates
					.iterator(); iter.hasNext();) {
				PrdStatusEntity prdStatusEntity = iter
						.next();
				Set<LookUpValueLocaleEntity> lookUpValueLocaleSet= prdStatusEntity.getPrdState().getLookUpValue().getLookUpValueLocales();
				for (Iterator<LookUpValueLocaleEntity> iterator = lookUpValueLocaleSet.iterator(); iterator
						.hasNext();) {
					LookUpValueLocaleEntity lookUpValueLocaleEntity = iterator.next();
					if (lookUpValueLocaleEntity.getLocaleId().equals(localeId)) {
						valueMap.put(prdStatusEntity.getOfferingStatusId().toString(), lookUpValueLocaleEntity.getLookUpValue());
					}
				}
			}
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return valueMap;
	}
	
	public Map<String, String>  retrieveRecurrenceTypes(Short localeId) throws PersistenceException{
		Map<String, String> valueMap = new HashMap<String, String>();
		try {
			List<RecurrenceTypeEntity> recurrenceTypes = (List<RecurrenceTypeEntity>) executeNamedQuery(
					NamedQueryConstants.FETCH_ALL_RECURRENCE_TYPES, null);
			for (Iterator<RecurrenceTypeEntity> iter = recurrenceTypes
					.iterator(); iter.hasNext();) {
				RecurrenceTypeEntity recurrenceTypeEntity = iter
						.next();
				valueMap.put(recurrenceTypeEntity.getRecurrenceId().toString(), recurrenceTypeEntity.getRecurrenceName());
			}
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return valueMap;
	}
	
}

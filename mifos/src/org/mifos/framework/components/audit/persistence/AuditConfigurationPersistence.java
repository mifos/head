package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.application.master.util.valueobjects.LookUpLabel;
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
						// System.out.println("***************id : " +
						// customFieldDefinitionEntity.getFieldId());
						// System.out.println("***************name : " +
						// lookUpLabel.getLabelName());
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
}

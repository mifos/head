package org.mifos.application.master.business;

import java.util.List;

import org.mifos.framework.business.View;

/**
 * This class represents a {@link LookUpEntity} and its associated
 * list of {@link LookUpValueEntity} objects for a given locale.
 * 
 * A better name for this class might be ValueListForLocale.
 */
public class CustomValueList extends View {

	/**
	 * entityId refers to the {@link LookUpEnity} represented by this list
	 */
	private Short entityId;

	private Short localeId;

	private String entityLabel;

	private List<CustomValueListElement> customValueListElements;

	public CustomValueList() {
	}

	/**
	 * This is only used in the HQL query "masterdata.entityvalue" in LookUpEntity.hbm.xml 
	 */
	public CustomValueList(java.lang.Short entityId, java.lang.Short localeId,
			java.lang.String entityLabel) {

		this.entityId = entityId;
		this.localeId = localeId;
		this.entityLabel = entityLabel;
	}

	public java.lang.Short getEntityId() {
		return entityId;
	}

	public java.lang.Short getlocaleId() {
		return localeId;
	}

	public java.lang.String getEntityLabel() {
		return entityLabel;
	}

	/**
	 * Method which returns the customValueListElements	
	 * @return Returns the customValueListElements.
	 */
	public List<CustomValueListElement> getCustomValueListElements() {
		return customValueListElements;
	}

	/**
	 * Method which sets the customValueListElements
	 * @param customValueListElements The customValueListElements to set.
	 */
	public void setCustomValueListElements(List<CustomValueListElement> customValueListElements) {
		this.customValueListElements = customValueListElements;
	}

	/**
	 * Method which obtains a particular lookupValue for the given lookup id
	 * @param customValueListElements The customValueListElements to set.
	 */
	public String getLookUpValueForId(int lookUpId) {
		String lookUpValue = "";
		for (int i = 0; i < customValueListElements.size(); i++) {
			CustomValueListElement lookUpEntityValue = customValueListElements.get(i);
			if (lookUpId == lookUpEntityValue.getLookUpId().intValue()) {
				lookUpValue = lookUpEntityValue.getLookUpValue();
				break;
			}
		}
		return lookUpValue;
	}

}

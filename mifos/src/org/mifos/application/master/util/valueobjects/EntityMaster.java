package org.mifos.application.master.util.valueobjects;

import java.util.List;

import org.mifos.framework.util.valueobjects.ValueObject;

public class EntityMaster extends ValueObject {

	private Short entityId;

	private Short localeId;

	private String entityLabel;

	private List<LookUpMaster> lookUpMaster;

	public EntityMaster() {
	}

	public EntityMaster(java.lang.Short entityId, java.lang.Short localeId,
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

	public void setLookUpValues(List<LookUpMaster> lookUpMaster) {
		this.lookUpMaster = lookUpMaster;
	}

	public List<LookUpMaster> getLookUpValues() {
		return lookUpMaster;
	}

	/**
	 * Method which returns the lookUpMaster	
	 * @return Returns the lookUpMaster.
	 */
	public List<LookUpMaster> getLookUpMaster() {
		return lookUpMaster;
	}

	/**
	 * Method which sets the lookUpMaster
	 * @param lookUpMaster The lookUpMaster to set.
	 */
	public void setLookUpMaster(List<LookUpMaster> lookUpMaster) {
		this.lookUpMaster = lookUpMaster;
	}

	/**
	 * Method which obtains a particular lookupValue for the given lookup id
	 * @param lookUpMaster The lookUpMaster to set.
	 */
	public String getLookUpValueForId(int lookUpId) {
		String lookUpValue = "";
		for (int i = 0; i < lookUpMaster.size(); i++) {
			LookUpMaster lookUpEntityValue = lookUpMaster.get(i);
			if (lookUpId == lookUpEntityValue.getLookUpId().intValue()) {
				lookUpValue = lookUpEntityValue.getLookUpValue();
				break;
			}
		}
		return lookUpValue;
	}

}

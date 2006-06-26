/**
 *
 */
package org.mifos.application.customer.util.valueobjects;

import org.mifos.application.master.util.valueobjects.LookUpEntity;

/**
 * @author sumeethaec
 *
 */
public class CustomFieldDefn {

	/**Denotes the field Id which identifes the custom field definition*/
	private Short fieldId;
	/**Denotes the entity Id which identifes the custom field naem according to locale*/
	private LookUpEntity lookUpEntity;
	/**Denotes the level id of the customer*/
	private int levelId;
	/**denotes the data type of custom field*/
	private Short fieldType;

	private Short entityType;

	private Short mandatoryFlag;
	/**Denotes if the custom field is mandatory or not*/
	private boolean isMandatory;
	/**
	 * Method which returns fieldId
	 * @return Returns the fieldId.
	 */
	public Short getFieldId() {
		return fieldId;
	}
	/**
	 * Method which sets fieldId
	 * @param fieldId The fieldId to set.
	 */
	public void setFieldId(Short fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * Method which returns fieldType
	 * @return Returns the fieldType.
	 */
	public Short getFieldType() {
		return fieldType;
	}
	/**
	 * Method which sets fieldType
	 * @param fieldType The fieldType to set.
	 */
	public void setFieldType(Short fieldType) {
		this.fieldType = fieldType;
	}
	/**
	 * Method which returns isMandatory
	 * @return Returns the isMandatory.
	 */
	public boolean isMandatory() {
		return isMandatory;
	}
	/**
	 * Method which sets isMandatory
	 * @param isMandatory The isMandatory to set.
	 */
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}


	/**
	 * Method which returns the levelId
	 * @return Returns the levelId.
	 */
	public int getLevelId() {
		return levelId;
	}
	/**
	 * Method which sets the levelId
	 * @param levelId The levelId to set.
	 */
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
	/**
	 * @return Returns the entityType}.
	 */
	public Short getEntityType() {
		return entityType;
	}
	/**
	 * @param entityType The entityType to set.
	 */
	public void setEntityType(Short entityType) {
		this.entityType = entityType;
	}
	/**
	 * @return Returns the mandatoryFlag}.
	 */
	public Short getMandatoryFlag() {
		return mandatoryFlag;
	}
	/**
	 * @param mandatoryFlag The mandatoryFlag to set.
	 */
	public void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
	/**
	 * @return Returns the lookUpEntity}.
	 */
	public LookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}
	/**
	 * @param lookUpEntity The lookUpEntity to set.
	 */
	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}


}

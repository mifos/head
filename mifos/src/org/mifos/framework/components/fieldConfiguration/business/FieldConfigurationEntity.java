package org.mifos.framework.components.fieldConfiguration.business;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.exceptions.PersistenceException;




public class FieldConfigurationEntity {

	private Integer fieldConfigId;
	private String  fieldName;
	private EntityMaster  entityMaster;
	private Short mandatoryFlag;
	private Short hiddenFlag;
	private FieldConfigurationEntity  parentFieldConfig;
	
	public FieldConfigurationEntity(){
	}
	
	public Integer getFieldConfigId() {
		return fieldConfigId;
	}
	
	@SuppressWarnings("unused") // see .hbm.xml file
	private void setFieldConfigId(Integer fieldConfigId) {
		this.fieldConfigId = fieldConfigId;
	}
	
	@SuppressWarnings("unused") // see .hbm.xml file
	private void setHiddenFlag(Short hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}
	
	public Short getHiddenFlag() {
		return hiddenFlag;
	}
	
	public boolean isHidden() {
		// TODO: what about null and other values besides 0 or 1?
		// should those be an exception (can't happen) or what?
		return hiddenFlag == FieldConfigurationConstant.YES;
	}

	public Short getMandatoryFlag() {
		return mandatoryFlag;
	}
	
	public boolean isMandatory() {
		// TODO: what about null and other values besides 0 or 1?
		// should those be an exception (can't happen) or what?
		return mandatoryFlag == FieldConfigurationConstant.YES;
	}

	@SuppressWarnings("unused") // see .hbm.xml file
	private void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	/**
	 * Most callers instead want {@link #getEntityType()}.
	 */
	EntityMaster getEntityMaster() {
		return entityMaster;
	}

	public EntityType getEntityType() {
		return EntityType.fromInt(entityMaster.getId());
	}

	@SuppressWarnings("unused") // see .hbm.xml file
	private void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public String getFieldName() {
		return fieldName;
	}

	@SuppressWarnings("unused") // see .hbm.xml file
	private void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public FieldConfigurationEntity getParentFieldConfig() {
		return parentFieldConfig;
	}

	public void setParentFieldConfig(FieldConfigurationEntity parentFieldConfig) {
		this.parentFieldConfig = parentFieldConfig;
	}

	public String getLabel(){
		return this.getEntityMaster().getEntityType()+"."+this.getFieldName();
	}
	
	public void update(Short mandatoryFlag, Short hiddenFlag)
			throws PersistenceException {
		this.mandatoryFlag = mandatoryFlag;
		this.hiddenFlag = hiddenFlag;
		new FieldConfigurationPersistence().createOrUpdate(this);
	}

}

package org.mifos.framework.components.fieldConfiguration.business;

import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;



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
	
	private void setFieldConfigId(Integer fieldConfigId) {
		this.fieldConfigId = fieldConfigId;
	}
	
	private void setHiddenFlag(Short hiddenFlag) {
		this.hiddenFlag = hiddenFlag;
	}
	
	public Short getHiddenFlag() {
		return hiddenFlag;
	}

	public Short getMandatoryFlag() {
		return mandatoryFlag;
	}
	
	private void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public EntityMaster getEntityMaster() {
		return entityMaster;
	}

	private void setEntityMaster(EntityMaster entityMaster) {
		this.entityMaster = entityMaster;
	}

	public String getFieldName() {
		return fieldName;
	}

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
	
}

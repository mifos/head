package org.mifos.framework.components.fieldConfiguration.business;




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

	public Short getMandatoryFlag() {
		return mandatoryFlag;
	}
	
	@SuppressWarnings("unused") // see .hbm.xml file
	private void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public EntityMaster getEntityMaster() {
		return entityMaster;
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
	
}

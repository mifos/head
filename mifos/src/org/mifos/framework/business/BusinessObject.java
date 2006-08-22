package org.mifos.framework.business;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.security.util.UserContext;

public abstract class BusinessObject extends PersistentObject{
	
	protected UserContext userContext ;
	
	private FieldConfigItf fieldConfig = FieldConfigImplementer.getInstance();
	
	List<FieldConfigurationEntity> fieldList;
	
	public BusinessObject(){
		this.userContext = null;
	}
	
	protected BusinessObject(UserContext userContext) {
		this.userContext=userContext;
	}
	
	public void setUserContext(UserContext userContext){
		this.userContext=userContext;
	}
	
	public UserContext getUserContext(){
		return this.userContext;
	}
	public List<FieldConfigurationEntity> getMandatoryFieldList(){
		List<FieldConfigurationEntity> mandatoryFieldList=new ArrayList<FieldConfigurationEntity>();
		for(FieldConfigurationEntity fieldConfigurationEntity : fieldList){
			if(fieldConfig.isFieldManadatory(fieldConfigurationEntity.getLabel())){
				mandatoryFieldList.add(fieldConfigurationEntity);
			}
		}
		return mandatoryFieldList;
	}
		
	public List<FieldConfigurationEntity> getHiddenFieldList(){
		List<FieldConfigurationEntity> hiddenFieldList=new ArrayList<FieldConfigurationEntity>();
		for(FieldConfigurationEntity fieldConfigurationEntity : fieldList){
			if(fieldConfig.isFieldHidden(fieldConfigurationEntity.getLabel())){
				hiddenFieldList.add(fieldConfigurationEntity);
			}
		}
		return hiddenFieldList;
	}
	
	protected void setCreateDetails() {
		setCreatedDate(new Date());
		setCreatedBy(userContext.getId());
	}
	
	protected void setUpdateDetails() {
		setUpdatedDate(new Date());
		setUpdatedBy(userContext.getId());
	}
		
}

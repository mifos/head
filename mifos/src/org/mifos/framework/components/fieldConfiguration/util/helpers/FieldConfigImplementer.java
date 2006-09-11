package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.service.FieldConfigurationPersistenceService;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;

public class FieldConfigImplementer implements FieldConfigItf{
	
	private static FieldConfigurationPersistenceService fieldConfigurationPersistenceService=new FieldConfigurationPersistenceService();	
	private static FieldConfigImplementer fieldConfigImplementer=new FieldConfigImplementer();
	private Map<Short, List<FieldConfigurationEntity>> entityFieldMap=new HashMap<Short, List<FieldConfigurationEntity>>();
	//TODO The field below will be removed after M1-M2 migration
	private Map<Short, List<FieldConfigurationEntity>> entityMandatoryFieldMap=new HashMap<Short, List<FieldConfigurationEntity>>();
	private Map<Object,Object> entityMap=EntityMasterData.getEntityMasterMap();;
		
	private FieldConfigImplementer(){}
	
	//TODO The method below will be removed after M1-M2 migration
	public Map<Short, List<FieldConfigurationEntity>> getEntityMandatoryFieldMap() {
		return entityMandatoryFieldMap;
	}

	public Map<Short, List<FieldConfigurationEntity>> getEntityFieldMap() {
		return entityFieldMap;
	}
	
	public Map<Object, Object> getEntityMap() {
		return entityMap;
	}

	public static FieldConfigImplementer getInstance(){
		return fieldConfigImplementer;
	}

	public boolean isFieldHidden(String labelName) {
		if(labelName==null || labelName.equals("") || labelName.indexOf(".")==-1)
			return false;
		labelName=labelName.trim();
		String entityName=labelName.substring(0,labelName.indexOf("."));
		String fieldName=labelName.substring(labelName.indexOf(".")+1);
		List<FieldConfigurationEntity>  fieldList=getEntityFieldMap().get(getEntityMap().get(entityName));
		if(fieldList!=null && fieldList.size()>0)
			for(FieldConfigurationEntity fieldConfigurationEntity : fieldList){
				FieldConfigurationEntity parentfieldConfigurationEntity=fieldConfigurationEntity.getParentFieldConfig();
				if((fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.YES) || (parentfieldConfigurationEntity!=null && parentfieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.YES))) && fieldConfigurationEntity.getFieldName().endsWith(fieldName)){
					return true;
				}
			}
		return false;
	}

	public boolean isFieldManadatory(String labelName) {
		if(labelName==null || labelName.equals("") || labelName.indexOf(".")==-1)
			return false;
		labelName=labelName.trim();
		String entityName=labelName.substring(0,labelName.indexOf("."));
		String fieldName=labelName.substring(labelName.indexOf(".")+1);
		List<FieldConfigurationEntity>  fieldList=getEntityFieldMap().get(getEntityMap().get(entityName));
		if(fieldList!=null && fieldList.size()>0)	
			for(FieldConfigurationEntity fieldConfigurationEntity : fieldList){
				FieldConfigurationEntity parentfieldConfigurationEntity =fieldConfigurationEntity.getParentFieldConfig();
				if(parentfieldConfigurationEntity==null){
					if(fieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.YES) && 
							fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO) && 
							fieldConfigurationEntity.getFieldName().endsWith(fieldName)){
						return true;
					}
				}else if((parentfieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.YES) &&
						parentfieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO) && 
						fieldConfigurationEntity.getFieldName().endsWith(fieldName) &&  
						fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO)) ||
						((parentfieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.NO) &&
							parentfieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO) && 
							fieldConfigurationEntity.getFieldName().endsWith(fieldName) &&  
							fieldConfigurationEntity.getHiddenFlag().equals(FieldConfigurationConstant.NO) &&
							fieldConfigurationEntity.getMandatoryFlag().equals(FieldConfigurationConstant.YES)))){
						return true;
				}
			}
		return false;
	}
	
	
	/*This method is used to intialize the mandatory and entiyField maps */
	public void init() throws HibernateProcessException, PersistenceException{
		List<EntityMaster> entityMasterList=fieldConfigurationPersistenceService.getEntityMasterList();
		for(EntityMaster entityMaster : entityMasterList){
			getEntityFieldMap().put(entityMaster.getId(),fieldConfigurationPersistenceService.getListOfFields(entityMaster.getId()));
			//TODO The code below will be removed after M1-M2 migration
			getEntityMandatoryFieldMap().put(entityMaster.getId(),getMandatoryFieldList(entityMaster.getId()));
		}
	}
	
	//	TODO The code below will be removed after M1-M2 migration
	private List<FieldConfigurationEntity> getMandatoryFieldList(Short entityId){
		List<FieldConfigurationEntity> fieldList=getEntityFieldMap().get(entityId);
		List<FieldConfigurationEntity> mandatoryFieldList=new ArrayList<FieldConfigurationEntity>();
		for(FieldConfigurationEntity fieldConfigurationEntity : fieldList){
			if(isFieldManadatory(fieldConfigurationEntity.getLabel())){
				mandatoryFieldList.add(fieldConfigurationEntity);
			}
		}
		return mandatoryFieldList;
	}
	
}

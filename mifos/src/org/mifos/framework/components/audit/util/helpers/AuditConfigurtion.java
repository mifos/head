/**

 * AuditConfiguration.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.framework.components.audit.util.helpers;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;

import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.application.master.util.valueobjects.LookUpMaster;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class will be replaced by a configuration file in M2
 * @author krishankg
 *
 */
public class AuditConfigurtion {

	public static Map<Object,Map> entityMap=new HashMap<Object,Map>();
	private Map<String,Map> propertyMap=null;
	private Map<String,String> valueMap=null;
	
	
	PropertyResourceBundle columnNames=null;
	private Locale locale = null;
	private ConfigurationIntf  labelConfig = MifosConfiguration.getInstance();
	public static Map<Object,Map> entityMapForColumn=new HashMap<Object,Map>();
	private Map<String,String> columnPropertyMap=null;
	
	//Key Value pair
	private void addToValueMap(String id,String textToDisplay){
		valueMap.put(id,textToDisplay); 
	}

	private void addToPropertyMap(String propertyName,Map valueMap){
		propertyMap.put(propertyName,valueMap); 
	}

	private void addToEntityMap(String entityName,Map propertyMap){
		entityMap.put(entityName,propertyMap); 
	}
	
	//Column Mapping
	private void addToEntityMapForColumn(String entityName,Map columnPropertyMap){
		entityMapForColumn.put(entityName,columnPropertyMap); 
	}

	private void addToColumnPropertyMap(String propertyName,String displayName){
		columnPropertyMap.put(propertyName,displayName); 
	}
	
	
	
	private void getResourceBundle(){
		
			columnNames =	(PropertyResourceBundle)PropertyResourceBundle.getBundle("org/mifos/framework/util/resources/audit/ColumnMappingBundle");
			locale =Configuration.getInstance().getSystemConfig().getMFILocale();
	}
	
	
	public void createEntityValueMap() throws SystemException, ApplicationException{
		getResourceBundle();
		ColumnPropertyMapping columnPropertyMapping =XMLParser.getInstance().parser();
		EntityType[] entityTypes = columnPropertyMapping.getEntityTypes();
		for(int i=0;i<entityTypes.length;i++){
			addToEntityMapForColumn(entityTypes[i].getName(),createColumnNames(entityTypes[i].getPropertyNames()));
			addToEntityMap(entityTypes[i].getName(),createPropertyNames(entityTypes[i].getPropertyNames()));
		}
	}
	
	

	private Map<String,String> createColumnNames(PropertyName[] propertyNames){
		columnPropertyMap=new HashMap<String,String>();
		for(int i=0;i<propertyNames.length;i++){
			String propertyName=null;
			if(propertyNames[i].getParentName()!=null){
				propertyName=propertyNames[i].getParentName().concat(propertyNames[i].getName()); 
			}else{
				propertyName=propertyNames[i].getName();
			}
			if(propertyNames[i].getDoNotLog().equalsIgnoreCase(XMLConstants.YES)){
				addToColumnPropertyMap(propertyName,XMLConstants.DONOTLOGTHISPROPERTY);
			}else{
				addToColumnPropertyMap(propertyName,getColumnName(propertyNames[i].getDisplayKey()));
			}
		}
		return columnPropertyMap;
	}
	
	private Map<String,Map> createPropertyNames(PropertyName[] propertyNames) throws  SystemException, ApplicationException{
		propertyMap=new HashMap<String,Map>();
		for(int i=0;i<propertyNames.length;i++){
			if(propertyNames[i].getLookUp().equalsIgnoreCase(XMLConstants.YES))
				if(propertyNames[i].getParentName()!=null){
					addToPropertyMap(propertyNames[i].getParentName().concat(propertyNames[i].getName()),createValueMap(propertyNames[i].getEntityName()));
				}else{
					addToPropertyMap(propertyNames[i].getName(),createValueMap(propertyNames[i].getEntityName()));
				}
		}
		return propertyMap;
	}
	
	
	private Map<String,String> createValueMap(EntityName entityName) throws  SystemException, ApplicationException{
		valueMap=new HashMap<String,String>();
		if(entityName.getClassPath()==null && entityName.getPkColumn()==null){
			fetchMasterData(entityName.getName(),Short.valueOf("1"));
		}else{
			fetchMasterData(entityName.getName(),Short.valueOf("1"),entityName.getClassPath().getPath(),entityName.getPkColumn().getName());
		}
		 return valueMap;
	}
	
	
	private void fetchMasterData(String entityName,Short localeId) throws SystemException, ApplicationException{
		
		MasterDataRetriever masterDataRetriever=null;
		SearchResults obj=null;
		EntityMaster entityMaster =null;
		List<LookUpMaster> lookUpMasterData=null;
			masterDataRetriever=new MasterDataRetriever();
			obj=masterDataRetriever.retrieveMasterData(entityName,localeId,"test");
			entityMaster=(EntityMaster)obj.getValue();
			lookUpMasterData=entityMaster.getLookUpMaster();
			for(LookUpMaster lookUpMaster:lookUpMasterData) {
				addToValueMap(lookUpMaster.getLookUpId().toString(),lookUpMaster.getLookUpValue());
			}
    }
	
	
		
	private Map fetchMasterData(String entityName,Short localeId,String classPath,String pkColumn){
		valueMap=new HashMap<String,String>();
		MasterDataRetriever masterDataRetriever=null;
		SearchResults obj=null;
		EntityMaster entityMaster =null;
		List<LookUpMaster> lookUpMasterData=null;
		
		try{
			masterDataRetriever=new MasterDataRetriever();
			obj=masterDataRetriever.retrieveMasterData(entityName,localeId,"test",classPath,pkColumn);
			entityMaster=(EntityMaster)obj.getValue();
			lookUpMasterData=entityMaster.getLookUpMaster();
			for(LookUpMaster lookUpMaster:lookUpMasterData) {
				addToValueMap(lookUpMaster.getId().toString(),lookUpMaster.getLookUpValue());
			}
		}catch(Exception e){
			e.printStackTrace(); 
		}
		return valueMap; 
	}
	

	public  String getValueOfCorrespondingId(String entityType,String propertyName,Object id) {
		propertyMap=entityMap.get(entityType);
		valueMap=propertyMap.get(propertyName);
		String value=null;
		if(id==null){
			return "";
		}
		value=valueMap.get(id.toString());
		if(value==null){
			return "";
		}else{
			return value;
		}
	}
	
	public boolean checkForPropertyName(String entityType,String propertyName){
		propertyMap=entityMap.get(entityType);
		if(propertyMap==null){
			return false;
		}
		valueMap=propertyMap.get(propertyName);
		if(valueMap==null){
			return false;
		}else{
			return true;
		}
	}
	
	
	public String getColumnNameForPropertyName(String entityType,String propertyName){
		String columnName=null;
		columnPropertyMap=entityMapForColumn.get(entityType);
		if(columnPropertyMap==null){
			return null;
		}
		columnName=columnPropertyMap.get(propertyName);
		if(columnName==null){
			return null;
		}else{
			return columnName;
		}
	}
	private String getColumnName(String DisplayKey){
		String keys[] =getKeys(DisplayKey);
		
		String columnName ="";
		for (int i = 0; i < keys.length; i++) {
			if(keys[i].contains("."))
				columnName= columnName+" "+columnNames.getString(keys[i]);
			else
				try{
				columnName= columnName+" "+labelConfig.getLabel(keys[i],locale);
				}
			catch( ConfigurationException ce){
				//ignore it user may not see the label
			}
		}
		return columnName;
	}
	private String [] getKeys(String DisplayKey){
		String keys[] =null;
		if(DisplayKey.contains(",") )
			 keys= DisplayKey.split(",");
		else {
			keys=new String[]{DisplayKey};
		}
		return keys;
	}
	
}

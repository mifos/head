/**

 * InterceptHelper.java    version: xxx



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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.Type;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class InterceptHelper {

	private Map<Object,Object> initialValues;
	private Map<Object,Object> changedValues;
	private Map<Object,Object> columnNames;
	private Locale locale;
	private Short localeId;
	private String entityName;
	private String newEntityName;
	private Integer entityId;
	private StringBuilder initialArray=null;
	private StringBuilder changeArray=null;
	private MifosLogger logger;
	
	public InterceptHelper(){
		logger = MifosLogManager.getLogger(LoggerConstants.AUDITLOGGER);
		initialValues = new HashMap<Object,Object>();
		changedValues=new HashMap<Object,Object>();
		columnNames=new HashMap<Object,Object>();
	}
	
	public boolean isInitialValueMapEmpty(){
		if(initialValues==null || initialValues.size()==0)
			return true;
		return false;
	}
	
	public Map getInitialValueMap(){
		return initialValues;
	}
	
	public Map getChangeValueMap(){
		return changedValues;
	}
	
	public Map getPropertyNames(){
		return columnNames;
	}
	
	public Object getInitialValue(Object key){
		return initialValues.get(key);
	}
	
	public Object getChangeValue(Object key){
		return changedValues.get(key);
	}
	
	public Object getPropertyName(Object key){
		return columnNames.get(key);
	}
	
	public String getEntityName(){
		return entityName;
	}
	
	public Integer getEntityId(){
		return entityId;
	}
	
	
	public Map hibernateMeta(Object object,String state) {
		logger.debug("object : " +  object);
		ClassMetadata customMeta = HibernateUtil.getSessionFactory().getClassMetadata(object.getClass());
		Object[] propertyValues = customMeta.getPropertyValues(object,EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();
		
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			locale=((BusinessObject)object).getUserContext().getMfiLocale();
			localeId=((BusinessObject)object).getUserContext().getMfiLocaleId();
			logger.debug("initial path class: "+ AuditConfigurtion.getEntityToClassPath(object.getClass().getName()));
			entityName = AuditConfigurtion.getEntityToClassPath(object.getClass().getName());
			entityId = Integer.valueOf(customMeta.getIdentifier(object,EntityMode.POJO).toString());
		}
		
		setPrimaryKeyValues(customMeta,object,customMeta.getIdentifierPropertyName(),state);
		
		for (int i = 0; i < propertyNames.length; i++) {
			logger.debug("hibernateMeta property name : "+propertyNames[i]+ " and value : " + propertyValues[i]); 
				if (!propertyTypes[i].isEntityType()  
						&& !propertyTypes[i].isCollectionType() 
							&& !propertyTypes[i].isComponentType()) {
					if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
						String name=propertyNames[i];
						logger.debug("i hibernateMeta "+name+ " : " + propertyValues[i]);
						if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
							String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
								initialValues.put(propertyNames[i],value);
						}else{
							if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
								initialValues.put(propertyNames[i], ((Calendar)propertyValues[i]).getTime());
							}else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
								initialValues.put(propertyNames[i], new String((byte[])propertyValues[i]));
							}else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
								try{
									Date date=(Date)propertyValues[i];
									initialValues.put(propertyNames[i], DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								}catch(Exception e){
									initialValues.put(propertyNames[i],propertyValues[i].toString());
								}
							}else{
								initialValues.put(propertyNames[i], propertyValues[i]);
							}
						}
						String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
						if(columnName!=null && !columnName.equals("")){
							columnNames.put(propertyNames[i],columnName);
						}else{
							columnNames.put(propertyNames[i],propertyNames[i]);
						}
					}else{
						String name=propertyNames[i];
						logger.debug("c hibernateMeta "+name+ " : " + propertyValues[i]);
						if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
							String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
							    changedValues.put(propertyNames[i], value);
						}else{
							if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
								changedValues.put(propertyNames[i], ((Calendar)propertyValues[i]).getTime());
							}else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
								changedValues.put(propertyNames[i], new String((byte[])propertyValues[i]));
							}else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
								try{
									Date date=(Date)propertyValues[i];
									changedValues.put(propertyNames[i], DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								}catch(Exception e){
									changedValues.put(propertyNames[i],propertyValues[i].toString());
								}
							}else{
								changedValues.put(propertyNames[i], propertyValues[i]);
							}
						}
						String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
						if(columnName!=null && !columnName.equals("")){
							columnNames.put(propertyNames[i],columnName);
						}else{
							columnNames.put(propertyNames[i],propertyNames[i]);
						}
					}
			}
				
			if(propertyTypes[i].isEntityType() && 
					!propertyTypes[i].isComponentType() && 
						propertyValues[i] instanceof MasterDataEntity &&
						AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],null)){
				populateValueForObjectsOfTypeMasterDataEntity(propertyValues[i],state,propertyNames[i]);
			}
			
			//Reading Collection Types   
			if(propertyTypes[i].isCollectionType()
						&&  AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],null) &&
						AuditConfigurtion.isObjectPropertiesToBeMerged(entityName,propertyNames[i],null)) {
				populateAndMergeCollectionTypes(state,propertyValues[i],propertyNames[i],null);
			}
			
			if(propertyTypes[i].isCollectionType()
					&&  AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],null) &&
					!AuditConfigurtion.isObjectPropertiesToBeMerged(entityName,propertyNames[i],null)) {
				Iterator iterator = ((Set) propertyValues[i]).iterator();
				while (iterator.hasNext()) {
					Object obj = iterator.next();
					readFurtherMetaForCollectionType(obj, propertyNames[i],state);
				}
			}
			if (propertyTypes[i].isEntityType() &&
					!propertyTypes[i].isComponentType() &&
						!(propertyValues[i] instanceof MasterDataEntity) && 
						AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],null)) {
					Object obj = propertyValues[i];
					if (obj != null) {
						readFurtherMeta(obj,propertyNames[i],state);
					}
			}
			
			//Reading further Money type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					(propertyValues[i] instanceof Money)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMoneyType(obj1, propertyNames[i],state);
				}
			}
			
			//Reading further component type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],null)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherComponenetMeta(obj1, propertyNames[i],state,propertyTypes[i]);
				}
			}
			
		}

		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			return initialValues;
		}
		else{
			return changedValues;
		}
		
	}
	
	
	
	
	private void readFurtherMeta(Object obj,String firstName,String state) {
		Class clazz = getClazz(obj);

		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(clazz);

		Object[] propertyValues = customMeta.getPropertyValues(obj,
				EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();

		setPrimaryKeyValues(customMeta,obj,firstName.concat(customMeta.getIdentifierPropertyName()),state);
		
		
		for (int i = 0; i < propertyNames.length; i++) {
			logger.debug("readFurtherMeta property : " + propertyNames[i]);
			setColumnValues(propertyTypes[i],propertyNames[i],firstName,state,propertyValues[i]);
			
			//Reading masterdata Types
			if(propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
					propertyValues[i] instanceof MasterDataEntity &&
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)){
				populateValueForObjectsOfTypeMasterDataEntity(propertyValues[i],state,firstName.concat(propertyNames[i]));
			}
			
			//Reading Collection Types	
			if (propertyTypes[i].isCollectionType()
					&& AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)
					&& !AuditConfigurtion.isObjectPropertiesToBeMerged(entityName,propertyNames[i],firstName)) {
				Iterator iterator = ((Set) propertyValues[i]).iterator();
				while (iterator.hasNext()) {
					Object valueFromSet = iterator.next();
					readFurtherMetaForCollectionType(valueFromSet,propertyNames[i],state);
				}
			}
			
			//Reading Collection Types   
			if(propertyTypes[i].isCollectionType()
						&&  AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName) &&
						AuditConfigurtion.isObjectPropertiesToBeMerged(entityName,propertyNames[i],firstName)) {
				populateAndMergeCollectionTypes(state,propertyValues[i],propertyNames[i],firstName);
			}
			
			//Reading further entity type 
			if (propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMeta(obj1, propertyNames[i],state);
				}
			}
			
			//Reading further component type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherComponenetMeta(obj1, propertyNames[i],state,propertyTypes[i]);
				}
			}
			
			//Reading further Money type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					(propertyValues[i] instanceof Money)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMoneyType(obj1, firstName.concat(propertyNames[i]),state);
				}
			}
			
		}
	}
	
	private void readFurtherMoneyType(Object obj,String name,String state){
		 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			logger.debug("i readFurtherMoneyType "+name+ " : " + obj);
			initialValues.put(name, obj.toString());
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			if(columnName!=null && !columnName.equals(""))
				columnNames.put(name,columnName);
			else
				columnNames.put(name,name);
    	 }else{
    		 logger.debug("c readFurtherMoneyType "+name+ " : " + obj); 
		 	changedValues.put(name, obj.toString());
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			if(columnName!=null && !columnName.equals(""))
				columnNames.put(name,columnName);
			else
				columnNames.put(name,name);
				
    	 }
	}

	
	private void readFurtherComponenetMeta(Object obj,String firstName,String state,Type propertyType){
		
		AbstractComponentType abstractComponentType =(AbstractComponentType)propertyType;

		Object[] propertyValues = abstractComponentType.getPropertyValues(obj,EntityMode.POJO);
		String[] propertyNames = abstractComponentType.getPropertyNames();

		for (int i = 0; i < propertyNames.length; i++) {
			
			 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
					 String name=firstName.concat(propertyNames[i]);
					 logger.debug("i setColumnValues "+name+ " : " + propertyValues[i]);
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 initialValues.put(name, value);
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 initialValues.put(name,((Calendar)propertyValues[i]).getTime());
						 }else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
								initialValues.put(name, new String((byte[])propertyValues[i]));	
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 try{
								 Date date=(Date)propertyValues[i];
							     initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
							 }catch(Exception e){
								 initialValues.put(name, propertyValues[i].toString());
							 }
						 }else{
							 initialValues.put(name, propertyValues[i]);
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }
				 else{
					 String name=firstName.concat(propertyNames[i]);
					 logger.debug("c setColumnValues "+name+ " : " + propertyValues[i]);
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 changedValues.put(name, value);
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 changedValues.put(name, ((Calendar)propertyValues[i]).getTime());
						 }else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
							 changedValues.put(name, new String((byte[])propertyValues[i]));	
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 Date date=(Date)propertyValues[i];
							 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
						 }else{
							 changedValues.put(name, propertyValues[i]);
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }
			}
	}
	
	
	private void setColumnValues(Type propertyType,String propertyName,String firstName,String state,Object propertyValue){
		if (!propertyType.isEntityType() 
				&& !propertyType.isComponentType() 
				&&	!propertyType.isCollectionType()) {
			 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
				 String name=firstName.concat(propertyName);
				 logger.debug("i setColumnValues "+name+ " : " + propertyValue);
				 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
					 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValue,localeId);
					 initialValues.put(name, value);
				 }else{
					 if(propertyValue instanceof Calendar && propertyValue!=null){
						 initialValues.put(name,((Calendar)propertyValue).getTime());
					 }else if(propertyValue instanceof byte[] && propertyValue!=null){
							initialValues.put(name, new String((byte[])propertyValue));	
					 }else if(propertyValue instanceof Date && propertyValue!=null){
						 try{
							 Date date=(Date)propertyValue;
							initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
						 }catch(Exception e){
							 initialValues.put(name, propertyValue.toString());
						 }
					 }else{
						 initialValues.put(name, propertyValue);
					 }
				 }
				 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
				 if(columnName!=null && !columnName.equals("")){
					 columnNames.put(name,columnName);
				 }else{
					 columnNames.put(name,propertyName);
				 }
			 }
			 else{
				 String name=firstName.concat(propertyName);
				 logger.debug("c setColumnValues "+name+ " : " + propertyValue);
				 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
					 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValue,localeId);
					 changedValues.put(name, value);
				 }else{
					 if(propertyValue instanceof Calendar && propertyValue!=null){
						 changedValues.put(name, ((Calendar)propertyValue).getTime());
					 }else if(propertyValue instanceof byte[] && propertyValue!=null){
						 changedValues.put(name, new String((byte[])propertyValue));	
					 }else if(propertyValue instanceof Date && propertyValue!=null){
						 try{
							 Date date=(Date)propertyValue;
							 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
						 }catch(Exception e){
							 changedValues.put(name, propertyValue.toString());
						 }
					 }else{
						 changedValues.put(name, propertyValue);
					 }
				 }
				 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
				 if(columnName!=null && !columnName.equals("")){
					 columnNames.put(name,columnName);
				 }else{
					 columnNames.put(name,propertyName);
				 }
			 }
		}

	}


	
	private void readFurtherMetaForCollectionType(Object obj,String firstName,String state) {
		Class l = getClazz(obj);

		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(l);

		Object[] propertyValues = customMeta.getPropertyValues(obj,	EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();

		setPrimaryKeyValueForCollectionType(customMeta,obj,firstName.concat(customMeta.getIdentifierPropertyName()),state);
		
		for (int i = 0; i < propertyNames.length; i++) {
			if (!propertyTypes[i].isEntityType() 
					&& !propertyTypes[i].isComponentType() && !propertyTypes[i].isCollectionType()) {
				 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
					 String name=firstName.concat(propertyNames[i]);
					 logger.debug("i readFurtherMetaForCollectionType : "+name+ " : " + propertyValues[i]);
					 String oldValue=getOldValueToKey(initialValues,name);
	
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 if(!oldValue.equals("")){
							 initialValues.put(name, value.concat(",").concat(oldValue));
						 }else{
							 initialValues.put(name, value);
						 }
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 initialValues.put(name,((Calendar)propertyValues[i]).getTime().toString().concat(",").concat(oldValue) );
							 }else{
								 initialValues.put(name,((Calendar)propertyValues[i]).getTime());
							 }
						 }else if(!(propertyValues[i] instanceof Calendar) && !(propertyValues[i] instanceof Date) && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 initialValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
							 }else{
								 initialValues.put(name, propertyValues[i]);
							 }
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 try{
									 Date date=(Date)propertyValues[i];
									 initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()).toString().concat(",").concat(oldValue));
								 }catch(Exception e){
									 initialValues.put(name,propertyValues[i].toString().concat(",").concat(oldValue));
								 }
							 }else{
								 try{
									 Date date=(Date)propertyValues[i];
									 initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								 }catch(Exception e){
									 initialValues.put(name,propertyValues[i].toString());
								 }
							 }
						 }else{
							 if(!oldValue.equals("")){
								 initialValues.put(name, oldValue);
							 }else{
								 initialValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }else{
					 String name=firstName.concat(propertyNames[i].toString());
					 logger.debug("c readFurtherMetaForCollectionType : "+name+ " : " + propertyValues[i]);
					 String oldValue=getOldValueToKey(changedValues,name);
	
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 if(!value.equals("")){
							 changedValues.put(name, value.concat(",").concat(oldValue));
						 }else{
							 changedValues.put(name, oldValue);
						 }
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 changedValues.put(name, ((Calendar)propertyValues[i]).getTime().toString().concat(",").concat(oldValue) );
							 }else{
								 changedValues.put(name, ((Calendar)propertyValues[i]).getTime() );
							 }
						 }else if(!(propertyValues[i] instanceof Calendar) && !(propertyValues[i] instanceof Date) && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 changedValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
							 }else{
								 changedValues.put(name, propertyValues[i]);
							 }
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 try{
									 Date date= (Date)propertyValues[i];	
									 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()).toString().concat(",").concat(oldValue));
								 }catch(Exception e){
									 changedValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
								 }
							 }else{
								 try{
									 Date date= (Date)propertyValues[i];
									 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								 }catch(Exception e){
									 changedValues.put(name, propertyValues[i].toString());
								 }
							 }
						 }else{
							 if(!oldValue.equals("")){
								 changedValues.put(name, oldValue);
							 }else{
								 changedValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }
			}
			
			

			if(propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
					propertyValues[i] instanceof MasterDataEntity &&
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)){
				populateValueForObjectsOfTypeMasterDataEntityInCollections(propertyValues[i],state,firstName.concat(propertyNames[i]));
			}

			if (propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
				Object object = propertyValues[i];
				if (object != null) {
					readFurtherMetaForCollectionType(object,propertyNames[i],state);
				}

			}
			
			//Reading further Money type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					(propertyValues[i] instanceof Money)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMoneyType(obj1, firstName.concat(propertyNames[i]),state);
				}
			}
			
			//Reading further component type
			if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
					!(propertyValues[i] instanceof MasterDataEntity) && 
					AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readComponenetTypeInCollectionTypeWithoutMerge(obj1, propertyNames[i],state,propertyTypes[i]);
				}
			}

		}
	}
	
	
	private void readAndMergeCollectionTypes(Object obj,String firstName,String parentName,String state) {
		Class l = getClazz(obj);

		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(l);

		Object[] propertyValues = customMeta.getPropertyValues(obj,	EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();
		
		//setPrimaryKeyValueForCollectionTypeAndMerge(customMeta,obj,firstName.concat(customMeta.getIdentifierPropertyName()),state);
		
		for (int i = 0; i < propertyNames.length; i++) {
			logger.debug("property Name : " + propertyNames[i] + " value : " + propertyValues[i]);
				if (!propertyTypes[i].isEntityType() 
						&& !propertyTypes[i].isComponentType() && !propertyTypes[i].isCollectionType()) {
					 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
						 String name=firstName.concat(propertyNames[i]);
						 logger.debug("i readFurtherMetaForCollectionType "+name+ " value : " + propertyValues[i]);
						 if(isValueLoggable(propertyNames[i],firstName)){
							 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
								 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
								 if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
									 initialArray.append(value);
								 else if(value.trim().length()!=0)
									 initialArray.append("-").append(value);	 
							 }else{
								 if(propertyValues[i]!= null)
									 if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
										 initialArray.append(propertyValues[i]);
									 else if(propertyValues[i].toString().trim().length()!=0)
										 initialArray.append("-").append(propertyValues[i]);
									 
							 }
						 }
					 }
					 else{
						 String name=firstName.concat(propertyNames[i].toString());
						 logger.debug("c readFurtherMetaForCollectionType "+name+ " value : " + propertyValues[i]);
						 if(isValueLoggable(propertyNames[i],firstName)){
							 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
								 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
								 if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(","))
									 changeArray.append(value);
								 else if(value.trim().length()!=0)
									 changeArray.append("-").append(value);
							 }else{
								 if(propertyValues[i]!= null)
									 if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(","))
										 changeArray.append(propertyValues[i]);
									 else if(propertyValues[i].toString().trim().length()!=0)
										 changeArray.append("-").append(propertyValues[i]);
							 }
						 }
					 }
				}
	
				if(propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
						propertyValues[i] instanceof MasterDataEntity &&
						AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)){
	
					populateAndMergeValueForObjectsOfTypeMasterDataEntityInCollections(propertyValues[i],state,firstName.concat(propertyNames[i]));
				}
				if (propertyTypes[i].isEntityType() && !propertyTypes[i].isComponentType() &&
						!(propertyValues[i] instanceof MasterDataEntity) && 
						AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
					Object object = propertyValues[i];
					if (object != null) {
						readAndMergeCollectionTypes(object,propertyNames[i],firstName,state);
						
					}
				}
				//Reading further Money type
				if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
						!(propertyValues[i] instanceof MasterDataEntity) && 
						(propertyValues[i] instanceof Money)) {
					Object obj1 = propertyValues[i];
					if (obj1 != null) {
						readFurtherMoneyType(obj1, firstName.concat(propertyNames[i]),state);
					}
				}
				
				//Reading further component type
				if (!propertyTypes[i].isEntityType() && propertyTypes[i].isComponentType() &&
						!(propertyValues[i] instanceof MasterDataEntity) && 
						AuditConfigurtion.isObjectToBeLogged(entityName,propertyNames[i],firstName)) {
					Object obj1 = propertyValues[i];
					if (obj1 != null) {
						readComponenetTypeInCollectionTypeWithMerge(obj1, propertyNames[i],state,propertyTypes[i]);
					}
				}
		}
	}
	
	private void readComponenetTypeInCollectionTypeWithMerge(Object obj,String firstName,String state,Type propertyType){
		
		AbstractComponentType abstractComponentType =(AbstractComponentType)propertyType;

		Object[] propertyValues = abstractComponentType.getPropertyValues(obj,EntityMode.POJO);
		String[] propertyNames = abstractComponentType.getPropertyNames();
		
		for (int i = 0; i < propertyNames.length; i++) {
			logger.debug("property Name : " + propertyNames[i] + "  value  : "+propertyValues[i]);
					 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
						 String name=firstName.concat(propertyNames[i]);
						 logger.debug("i readComponenetTypeInCollectionTypeWithMerge "+name+ " value : " + propertyValues[i]);
						 if(isValueLoggable(propertyNames[i],firstName)){
							 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
								 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
								 if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
									 initialArray.append(value);
								 else if(value.trim().length()!=0)
									 initialArray.append("-").append(value);	 
							 }else{
								 if(propertyValues[i]!= null)
									 if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
										 initialArray.append(propertyValues[i]);
									 else if(propertyValues[i].toString().trim().length()!=0)
										 initialArray.append("-").append(propertyValues[i]);
									 
							 }
						 }
					 }
					 else{
						 String name=firstName.concat(propertyNames[i].toString());
						 logger.debug("c readComponenetTypeInCollectionTypeWithMerge "+name+ " value : " + propertyValues[i]);
						 if(isValueLoggable(propertyNames[i],firstName)){
							 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
								 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
								 if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(","))
									 changeArray.append(value);
								 else if(value.trim().length()!=0)
									 changeArray.append("-").append(value);
							 }else{
								 if(propertyValues[i]!= null)
									 if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(","))
										 changeArray.append(propertyValues[i]);
									 else if(propertyValues[i].toString().trim().length()!=0)
										 changeArray.append("-").append(propertyValues[i]);
							 }
						 }
					 }
				
		}
		
		
	}

	
	
	private void readComponenetTypeInCollectionTypeWithoutMerge(Object obj,String firstName,String state,Type propertyType){
		
		AbstractComponentType abstractComponentType =(AbstractComponentType)propertyType;

		Object[] propertyValues = abstractComponentType.getPropertyValues(obj,EntityMode.POJO);
		String[] propertyNames = abstractComponentType.getPropertyNames();
		
		for (int i = 0; i < propertyNames.length; i++) {
			 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
					 String name=firstName.concat(propertyNames[i]);
					 logger.debug("i readComponenetTypeInCollectionTypeWithoutMerge : "+name+ " value : " + propertyValues[i]);
					 String oldValue=getOldValueToKey(initialValues,name);
	
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 if(!oldValue.equals("")){
							 initialValues.put(name, value.concat(",").concat(oldValue));
						 }else{
							 initialValues.put(name, value);
						 }
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 initialValues.put(name,((Calendar)propertyValues[i]).getTime().toString().concat(",").concat(oldValue) );
							 }else{
								 initialValues.put(name,((Calendar)propertyValues[i]).getTime());
							 }
						 }else if(!(propertyValues[i] instanceof Calendar) && !(propertyValues[i] instanceof Date) && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 initialValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
							 }else{
								 initialValues.put(name, propertyValues[i]);
							 }
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 try{
									 Date date=(Date)propertyValues[i];
									 initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()).toString().concat(",").concat(oldValue));
								 }catch(Exception e){
									 initialValues.put(name,propertyValues[i].toString().concat(",").concat(oldValue));
								 }
							 }else{
								 try{
									 Date date=(Date)propertyValues[i];
									 initialValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								 }catch(Exception e){
									 initialValues.put(name,propertyValues[i].toString());
								 }
							 }
						 }else{
							 if(!oldValue.equals("")){
								 initialValues.put(name, oldValue);
							 }else{
								 initialValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }else{
					 String name=firstName.concat(propertyNames[i].toString());
					 logger.debug("c readComponenetTypeInCollectionTypeWithoutMerge : "+name+ " value : " + propertyValues[i]);
					 String oldValue=getOldValueToKey(changedValues,name);
	
					 if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
						 String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,propertyValues[i],localeId);
						 if(!value.equals("")){
							 changedValues.put(name, value.concat(",").concat(oldValue));
						 }else{
							 changedValues.put(name, oldValue);
						 }
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 changedValues.put(name, ((Calendar)propertyValues[i]).getTime().toString().concat(",").concat(oldValue) );
							 }else{
								 changedValues.put(name, ((Calendar)propertyValues[i]).getTime() );
							 }
						 }else if(!(propertyValues[i] instanceof Calendar) && !(propertyValues[i] instanceof Date) && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 changedValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
							 }else{
								 changedValues.put(name, propertyValues[i]);
							 }
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 if(!oldValue.equals("")){
								 try{
									 Date date= (Date)propertyValues[i];	
									 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()).toString().concat(",").concat(oldValue));
								 }catch(Exception e){
									 changedValues.put(name, propertyValues[i].toString().concat(",").concat(oldValue));
								 }
							 }else{
								 try{
									 Date date= (Date)propertyValues[i];
									 changedValues.put(name, DateHelper.getUserLocaleDate(locale,new java.sql.Date(date.getTime()).toString()));
								 }catch(Exception e){
									 changedValues.put(name, propertyValues[i].toString());
								 }
							 }
						 }else{
							 if(!oldValue.equals("")){
								 changedValues.put(name, oldValue);
							 }else{
								 changedValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
					 if(columnName!=null && !columnName.equals("")){
						 columnNames.put(name,columnName);
					 }else{
						 columnNames.put(name,propertyNames[i]);
					 }
				 }
		}
	}


	
	
	private void setPrimaryKeyValueForCollectionType(ClassMetadata customMeta,Object obj,String name,String state){
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){		
			String oldValue=getOldValueToKey(initialValues,name);
			logger.debug("i setPrimaryKeyValueForCollectionType : "+ name +" value : "+customMeta.getIdentifier(obj,EntityMode.POJO));
			if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
				String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
				if(!oldValue.equals("")){
					initialValues.put(name, value.concat(",").concat(oldValue));
				}else{
					initialValues.put(name, value);
				}
			}else{
				if(customMeta.getIdentifier(obj,EntityMode.POJO)!=null){
					if(!oldValue.equals("")){
						initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO).toString().concat(",").concat(oldValue));
					}else{
						initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO));
					}
				}
			}
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			if(columnName!=null && !columnName.equals("")){
				columnNames.put(name,columnName);
			}else{
				columnNames.put(name,customMeta.getIdentifierPropertyName());
			}
		}
		else{
			String oldValue=getOldValueToKey(changedValues,name);
			logger.debug("c setPrimaryKeyValueForCollectionType : "+ name +" value : "+customMeta.getIdentifier(obj,EntityMode.POJO));
			if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
				String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
				if(!oldValue.equals("")){
					changedValues.put(name,value.concat(",").concat(oldValue));
				}else{
					changedValues.put(name,value);
				}
			}else{
				if(customMeta.getIdentifier(obj,EntityMode.POJO)!=null){
					if(!oldValue.equals("")){
						changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO).toString().concat(",").concat(oldValue));
					}else{
						changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO));
					}
				}
			}
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			if(columnName!=null && !columnName.equals("")){
				columnNames.put(name,columnName);
			}else{
				columnNames.put(name,customMeta.getIdentifierPropertyName());
			}
		}
	}
	
	private void setPrimaryKeyValueForCollectionTypeAndMerge(ClassMetadata customMeta,Object obj,String name,String state){
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){		
			logger.debug("i setPrimaryKeyValueForCollectionTypeAndMerge : "+ name +" value : "+customMeta.getIdentifier(obj,EntityMode.POJO));
			if(isValueLoggable(name,null)){
				if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
					String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
					if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
						 initialArray.append(value);
				    else if(value.trim().length()!=0)
						 initialArray.append("-").append(value);
				}else{
					if(initialArray.toString().trim().length()==0 || initialArray.toString().endsWith(","))
			    		 initialArray.append(customMeta.getIdentifier(obj,EntityMode.POJO));
				    else if(customMeta.getIdentifier(obj,EntityMode.POJO).toString().trim().length()!=0)
						 initialArray.append("-").append(customMeta.getIdentifier(obj,EntityMode.POJO));
				}
			}
		}
		else{
			logger.debug("c setPrimaryKeyValueForCollectionTypeAndMerge : "+ name +" value : "+customMeta.getIdentifier(obj,EntityMode.POJO));
			if(isValueLoggable(name,null)){
				if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
					String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
					if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(",")) 
						changeArray.append(value);
				    else if(value.trim().length()!=0)
				    	changeArray.append("-").append(value);
				}else{
					if(changeArray.toString().trim().length()==0 || changeArray.toString().endsWith(","))
						changeArray.append(customMeta.getIdentifier(obj,EntityMode.POJO));
				    else if(customMeta.getIdentifier(obj,EntityMode.POJO).toString().trim().length()!=0)
				    	changeArray.append("-").append(customMeta.getIdentifier(obj,EntityMode.POJO));
				}
			}
		}
	}
	
	private void setPrimaryKeyValues(ClassMetadata customMeta,Object obj,String name,String state){
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
				String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
				initialValues.put(name, value);
				logger.debug("i setPrimaryKeyValues "+name+ " value : " +  value);
			}else{
				initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO));
				logger.debug("i setPrimaryKeyValues "+name+ " value : " + customMeta.getIdentifier(obj,EntityMode.POJO));
			}
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			if(columnName!=null && !columnName.equals("")){
				columnNames.put(name,columnName);
			}else{
				columnNames.put(name,customMeta.getIdentifierPropertyName());
			}
		}
		else{
			if(AuditConfigurtion.checkForPropertyName(entityName,name,localeId)){
				String value=AuditConfigurtion.getValueOfCorrespondingId(entityName,name,customMeta.getIdentifier(obj,EntityMode.POJO),localeId);
				logger.debug("c setPrimaryKeyValues "+name+ " value : " +  value);
				changedValues.put(name,value);
			}else{
				changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO));
			}
			String columnName=AuditConfigurtion.getColumnNameForPropertyName(entityName,name);
			logger.debug("c setPrimaryKeyValues "+name+ " value : " + customMeta.getIdentifier(obj,EntityMode.POJO));
			if(columnName!=null && !columnName.equals("")){
				columnNames.put(name,columnName);
			}else{
				columnNames.put(name,customMeta.getIdentifierPropertyName());
			}
		}
	}
	
	
	
	private void populateValueForObjectsOfTypeMasterDataEntity(Object obj,String state,String name){
		Class clazz = getClazz(obj);
		
		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(clazz);

		setPrimaryKeyValues(customMeta,obj,name,state);
		
	}
	
	private void populateValueForObjectsOfTypeMasterDataEntityInCollections(Object obj,String state,String name){
		Class clazz = getClazz(obj);
		
		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(clazz);
		
		setPrimaryKeyValueForCollectionType(customMeta,obj,name,state);
		
	}
	
	private void populateAndMergeValueForObjectsOfTypeMasterDataEntityInCollections(Object obj,String state,String name){
		Class clazz = getClazz(obj);
		
		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(clazz);

		Object[] propertyValues = customMeta.getPropertyValues(obj,
				EntityMode.POJO);
		
		setPrimaryKeyValueForCollectionTypeAndMerge(customMeta,obj,name,state);
		
	}
	
	private Class getClazz(Object obj){
		try {
			HibernateProxy hibernateProxy = (HibernateProxy) obj;
			LazyInitializer lazyInitializer = hibernateProxy.getHibernateLazyInitializer();
			return lazyInitializer.getPersistentClass();
		} catch (ClassCastException e) {
			return obj.getClass();
		}
	}
	
	
	private String  getOldValueToKey(Map map,String key){
		String value=null;
		if(map.get(key)!=null){
			value=map.get(key).toString();
			if(!value.trim().equals("")){
				return value;
			}else{
				return "";
			}
		}else{
			return "";
		}
    }

	private boolean isValueLoggable(String propertyName,String parentName) {
		if (propertyName.equalsIgnoreCase(AuditConstants.VERSIONNO)
				|| propertyName.equalsIgnoreCase(AuditConstants.CREATEDBY)
				|| propertyName.equalsIgnoreCase(AuditConstants.CREATEDDATE)
				|| propertyName.equalsIgnoreCase(AuditConstants.UPDATEDBY)
				|| propertyName.equalsIgnoreCase(AuditConstants.UPDATEDDATE)
				|| propertyName.equalsIgnoreCase(AuditConstants.LOOKUPID)) {
			return false;
		}
		if(parentName!=null && AuditConfigurtion.getColumnNameForPropertyName(entityName,
						parentName.concat(propertyName)).equalsIgnoreCase(
						XMLConstants.DONOTLOGTHISPROPERTY))
			return false;
		else if(AuditConfigurtion.getColumnNameForPropertyName(entityName,
						propertyName).equalsIgnoreCase(
						XMLConstants.DONOTLOGTHISPROPERTY))
			return false;
		return true;
	}
	
	private void populateAndMergeCollectionTypes(String state,Object propertyValue,String propertyName,String parentName){
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN))
			initialArray=new StringBuilder();
		else
			changeArray=new StringBuilder();
		Iterator iterator = ((Set) propertyValue).iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			readAndMergeCollectionTypes(obj, propertyName,parentName,state);
			if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN))
				initialArray.append(",");
			else
				changeArray.append(",");
		}
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			initialValues.put(propertyName,initialArray);
			if(parentName!=null)
				propertyName=parentName.concat(propertyName);
			columnNames.put(propertyName,AuditConfigurtion.getColumnNameForPropertyName(entityName,propertyName));
		}
		else{
			changedValues.put(propertyName,changeArray);
			if(parentName!=null)
				propertyName=parentName.concat(propertyName);
			columnNames.put(propertyName,AuditConfigurtion.getColumnNameForPropertyName(entityName,propertyName));
		}
	}
	


}

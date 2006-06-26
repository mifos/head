/**

 * InterceptHelper.java    version: xxx



 * Copyright © 2005-2006 Grameen Foundation USA

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
import org.hibernate.type.Type;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.struts.tags.DateHelper;

/**
 * @author krishankg
 *
 */
public class InterceptHelper {

	private Map initialValues = null;
	private Map changedValues=null;
	private Map duplicateEntity=null;
	AuditConfigurtion auditConfigurtion=null;
	Locale mfiLocale=null;

	public InterceptHelper(){
		auditConfigurtion=new AuditConfigurtion();
	}

	public Map hibernateMeta(LogInfo logInfo,String state) {
		Session session = null;
		initialValues = new HashMap();
		changedValues=new HashMap();
		mfiLocale=logInfo.getContext().getUserContext().getMfiLocale();    
		try {
			session = HibernateUtil.getSession();

			ClassMetadata customMeta = HibernateUtil.getSessionFactory()
					.getClassMetadata(
							logInfo.getValueMap().get(AuditConstants.REALOBJECT).getClass());
			Object[] propertyValues = null;
			if (logInfo.getEntityId() instanceof Short) {
				propertyValues = customMeta.getPropertyValues(session.get(
						logInfo.getValueMap().get(AuditConstants.REALOBJECT).getClass(),
						(Short) logInfo.getEntityId()), EntityMode.POJO);
			} else if (logInfo.getEntityId() instanceof Integer) {
				propertyValues = customMeta.getPropertyValues(session.get(
						logInfo.getValueMap().get(AuditConstants.REALOBJECT).getClass(),
						(Integer) logInfo.getEntityId()), EntityMode.POJO);
			}

			String[] propertyNames = customMeta.getPropertyNames();
			Type[] propertyTypes = customMeta.getPropertyTypes();
			if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
				String name=customMeta.getIdentifierPropertyName();
				if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
					String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(
						logInfo.getValueMap().get(AuditConstants.REALOBJECT),EntityMode.POJO));
					initialValues.put(name,value);
				}else{
					initialValues.put(name,customMeta.getIdentifier(
							logInfo.getValueMap().get(AuditConstants.REALOBJECT),EntityMode.POJO));
				}
				String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
				if(columnName!=null){
					logInfo.addPropertyName(customMeta.getIdentifierPropertyName(),columnName);
				}else{
					logInfo.addPropertyName(customMeta.getIdentifierPropertyName(),customMeta.getIdentifierPropertyName());
				}
			}
			else{
				String name=customMeta.getIdentifierPropertyName();
				if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
					String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(
						logInfo.getValueMap().get(AuditConstants.REALOBJECT),EntityMode.POJO));
					changedValues.put(name,value);
				}else{
					changedValues.put(name,customMeta.getIdentifier(
							logInfo.getValueMap().get(AuditConstants.REALOBJECT),EntityMode.POJO));
				}
				String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
				if(columnName!=null){
					logInfo.addPropertyName(customMeta.getIdentifierPropertyName(),columnName);
				}else{
					logInfo.addPropertyName(customMeta.getIdentifierPropertyName(),customMeta.getIdentifierPropertyName());
				}
			}

			for (int i = 0; i < propertyNames.length; i++) {
				if (!propertyTypes[i].isEntityType()
						&& !propertyTypes[i].isCollectionType()) {
					if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
						String name=propertyNames[i];
						if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
							String value=idToValueConverter(logInfo.getEntityType(),name,propertyValues[i]);
								initialValues.put(propertyNames[i],value);
						}else{
							if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
								initialValues.put(propertyNames[i], ((Calendar)propertyValues[i]).getTime());
							}else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
									initialValues.put(propertyNames[i], new String((byte[])propertyValues[i]));	
							}else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
								initialValues.put(propertyNames[i], DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
							}else{
								initialValues.put(propertyNames[i], propertyValues[i]);
							}
						}
						String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
						if(columnName!=null){
							logInfo.addPropertyName(propertyNames[i],columnName);
						}else{
							logInfo.addPropertyName(propertyNames[i],propertyNames[i]);
						}
					}
					else{
						String name=propertyNames[i];
						if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
							String value=idToValueConverter(logInfo.getEntityType(),name,propertyValues[i]);
							    changedValues.put(propertyNames[i], value);
						}else{
							if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
								changedValues.put(propertyNames[i], ((Calendar)propertyValues[i]).getTime());
							}else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
								changedValues.put(propertyNames[i], new String((byte[])propertyValues[i]));	
							}else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
								changedValues.put(propertyNames[i], DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
							}else{
								changedValues.put(propertyNames[i], propertyValues[i]);
							}
						}
						String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
						if(columnName!=null){
							logInfo.addPropertyName(propertyNames[i],columnName);
						}else{
							logInfo.addPropertyName(propertyNames[i],propertyNames[i]);
						}
					}
				}
				//Reading Collection Types   
				if(propertyTypes[i].isCollectionType()
						&&  logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(AuditConstants.REALOBJECT)) {
					Iterator iterator = ((Set) propertyValues[i]).iterator();
					while (iterator.hasNext()) {
						Object obj = iterator.next();
						readFurtherMetaForCollectionType(obj, logInfo,propertyNames[i].toString(),state);
					}
				}
				if (propertyTypes[i].isEntityType()
						&&  logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(AuditConstants.REALOBJECT)) {
					Object obj = propertyValues[i];
					if (obj != null) {
						readFurtherMeta(obj, logInfo,propertyNames[i].toString(),state);
					}
				}
			}

		} catch (HibernateProcessException e) {

			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			return initialValues;
		}
		else{
			return changedValues;
		}
	}

	public void readFurtherMeta(Object obj, LogInfo logInfo,String firstName,String state) {
		Class l = null;
		try {
			HibernateProxy h = (HibernateProxy) obj;
			LazyInitializer li = h.getHibernateLazyInitializer();
			l = li.getPersistentClass();
		} catch (ClassCastException e) {
			l = obj.getClass();
		}

		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(l);

		Object[] propertyValues = customMeta.getPropertyValues(obj,
				EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();

		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			String name=firstName.concat(customMeta.getIdentifierPropertyName());
			if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
				String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(obj,EntityMode.POJO));
				initialValues.put(name, value);
			}else{
				initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO));
			}
			String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
			if(columnName!=null){
				logInfo.addPropertyName(name,columnName);
			}else{
				logInfo.addPropertyName(name,customMeta.getIdentifierPropertyName());
			}
		}
		else{
			String name=firstName.concat(customMeta.getIdentifierPropertyName());
			if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
				String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(obj,EntityMode.POJO));
				changedValues.put(name,value);
			}else{
				changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO));
			}
			String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
			if(columnName!=null){
				logInfo.addPropertyName(name,columnName);
			}else{
				logInfo.addPropertyName(name,customMeta.getIdentifierPropertyName());
			}
		}

		for (int i = 0; i < propertyNames.length; i++) {
		
			if (!propertyTypes[i].isEntityType()
					&& !propertyTypes[i].isCollectionType()) {
				 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
					 String name=firstName.concat(propertyNames[i]);
					 if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
						 String value=idToValueConverter(logInfo.getEntityType(),name,propertyValues[i]);
						 initialValues.put(name, value);
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 initialValues.put(name,((Calendar)propertyValues[i]).getTime());
						 }else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
								initialValues.put(name, new String((byte[])propertyValues[i]));	
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
								initialValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
						 }else{
							 initialValues.put(name, propertyValues[i]);
						 }
					 }
					 String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
					 if(columnName!=null){
						 logInfo.addPropertyName(name,columnName);
					 }else{
						 logInfo.addPropertyName(name,propertyNames[i]);
					 }
				 }
				 else{
					 String name=firstName.concat(propertyNames[i]);
					 if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
						 String value=idToValueConverter(logInfo.getEntityType(),name,propertyValues[i]);
						 changedValues.put(name, value);
					 }else{
						 if(propertyValues[i] instanceof Calendar && propertyValues[i]!=null){
							 changedValues.put(name, ((Calendar)propertyValues[i]).getTime());
						 }else if(propertyValues[i] instanceof byte[] && propertyValues[i]!=null){
							 changedValues.put(name, new String((byte[])propertyValues[i]));	
						 }else if(propertyValues[i] instanceof Date && propertyValues[i]!=null){
							 changedValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
						 }else{
							 changedValues.put(name, propertyValues[i]);
						 }
					 }
					 String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
					 if(columnName!=null){
						 logInfo.addPropertyName(name,columnName);
					 }else{
						 logInfo.addPropertyName(name,propertyNames[i]);
					 }
				 }
			}

			//Reading Collection Types	
			if (propertyTypes[i].isCollectionType()
					&& logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(firstName)) {
				Iterator iterator = ((Set) propertyValues[i]).iterator();
				while (iterator.hasNext()) {
					Object valueFromSet = iterator.next();
					//// System.out.println("******************Value of object: "+ valueFromSet);
					readFurtherMetaForCollectionType(valueFromSet, logInfo,propertyNames[i].toString(),state);
				}
			}
			
			if (propertyTypes[i].isEntityType()
					&& logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(firstName)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMeta(obj1, logInfo,propertyNames[i].toString(),state);
				}
			}
		}
	}

	public String idToValueConverter(String entityType,String name,Object id){
		return auditConfigurtion.getValueOfCorrespondingId(entityType,name,id);
	}
	
	
	public void readFurtherMetaForCollectionType(Object obj, LogInfo logInfo,String firstName,String state) {
		Class l = null;
		try {
			HibernateProxy h = (HibernateProxy) obj;
			LazyInitializer li = h.getHibernateLazyInitializer();
			l = li.getPersistentClass();
		} catch (ClassCastException e) {
			l = obj.getClass();
		}

		ClassMetadata customMeta = HibernateUtil.getSessionFactory()
				.getClassMetadata(l);

		Object[] propertyValues = customMeta.getPropertyValues(obj,
				EntityMode.POJO);
		String[] propertyNames = customMeta.getPropertyNames();
		Type[] propertyTypes = customMeta.getPropertyTypes();

		//logging the value for primary key column
		if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
			String name=firstName.concat(customMeta.getIdentifierPropertyName());
			String oldValue=getOldValueToKey(initialValues,name);
	
			if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
				String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(obj,EntityMode.POJO));
				if(!oldValue.equals("")){
					initialValues.put(name, value.concat(",").concat(oldValue));
				}else{
					initialValues.put(name, value);
				}
			}else{
				if(!oldValue.equals("")){
					initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO).toString().concat(",").concat(oldValue));
				}else{
					initialValues.put(name, customMeta.getIdentifier(obj,EntityMode.POJO));
				}
			}
			String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
			if(columnName!=null){
				logInfo.addPropertyName(name,columnName);
			}else{
				logInfo.addPropertyName(name,customMeta.getIdentifierPropertyName());
			}
		}
		else{
			String name=firstName.concat(customMeta.getIdentifierPropertyName());
			String oldValue=getOldValueToKey(changedValues,name);
			if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
				String value=idToValueConverter(logInfo.getEntityType(),name,customMeta.getIdentifier(obj,EntityMode.POJO));
				if(!oldValue.equals("")){
					changedValues.put(name,value.concat(",").concat(oldValue));
				}else{
					changedValues.put(name,value);
				}
			}else{
				if(!oldValue.equals("")){
					changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO).toString().concat(",").concat(oldValue));
				}else{
					changedValues.put(name,customMeta.getIdentifier(obj,EntityMode.POJO));
				}
			}
			String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
			if(columnName!=null){
				logInfo.addPropertyName(name,columnName);
			}else{
				logInfo.addPropertyName(name,customMeta.getIdentifierPropertyName());
			}
		}

		for (int i = 0; i < propertyNames.length; i++) {
			if (!propertyTypes[i].isEntityType()
					&& !propertyTypes[i].isCollectionType()) {
				 if(state.equalsIgnoreCase(AuditConstants.TRANSACTIONBEGIN)){
					 String name=firstName.concat(propertyNames[i]);
	
					 String oldValue=getOldValueToKey(initialValues,name);
	
					 if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
						 String value=idToValueConverter(logInfo.getEntityType(),name,propertyValues[i]);
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
								 initialValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()).toString().concat(",").concat(oldValue));
							 }else{
								 initialValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
							 }
						 }else{
							 if(!oldValue.equals("")){
								 initialValues.put(name, oldValue);
							 }else{
								 initialValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
					 if(columnName!=null){
						 logInfo.addPropertyName(name,columnName);
					 }else{
						 logInfo.addPropertyName(name,propertyNames[i]);
					 }
				 }
				 else{
					 String name=firstName.concat(propertyNames[i].toString());
	
					 String oldValue=getOldValueToKey(changedValues,name);
	
					 if(auditConfigurtion.checkForPropertyName(logInfo.getEntityType(),name)){
						 String value=idToValueConverter( logInfo.getEntityType(),name,propertyValues[i]);
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
								 changedValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()).toString().concat(",").concat(oldValue));
							 }else{
								 changedValues.put(name, DateHelper.getUserLocaleDate(mfiLocale,propertyValues[i].toString()));
							 }
						 }else{
							 if(!oldValue.equals("")){
								 changedValues.put(name, oldValue);
							 }else{
								 changedValues.put(name, propertyValues[i]);
							 }
						 }
					 }
					 String columnName=auditConfigurtion.getColumnNameForPropertyName(logInfo.getEntityType(),name);
					 if(columnName!=null){
						 logInfo.addPropertyName(name,columnName);
					 }else{
						 logInfo.addPropertyName(name,propertyNames[i]);
					 }
				 }
			}

			//Reading Collection Types	
			if (propertyTypes[i].isCollectionType()
					&& logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(firstName)) {
				Iterator iterator = ((Set) propertyValues[i]).iterator();
				while (iterator.hasNext()) {
					Object valueFromSet = iterator.next();
					readFurtherMetaForCollectionType(valueFromSet, logInfo,propertyNames[i].toString(),state);
				}
			}


			if (propertyTypes[i].isEntityType()
					&& logInfo.getValueMap().get(propertyNames[i].toUpperCase()) != null && logInfo.getValueMap().get(propertyNames[i].toUpperCase()).toString().equalsIgnoreCase(firstName)) {
				Object obj1 = propertyValues[i];
				if (obj1 != null) {
					readFurtherMetaForCollectionType(obj1, logInfo,propertyNames[i].toString(),state);
				}

			}
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

}

/**

 * LogInfo.java    version: xxx

 

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.mifos.framework.util.valueobjects.Context;


/**
 * @author krishankg
 *
 */
public class LogInfo {
	
	/**
	 * This represents the primay key column value of the corresponding valueobject that is being modified of saves.
	 */
	private Object entityId;  
	/**
	 * This represents the module name 
	 */
	private String entityType;
	
	/**
	 * This is the context
	 */
	private Context context;
	
	/**
	 * This is the map that contains the objects  that needs to be iterated.
	 */
	private Map valueMap;
	
	/**
	 * This is map of initial values before update
	 */
	private Map initialValues;
	
	/**
	 * This is map of changed value after update
	 */
	private Map changedValues;
	
	/**
	 * This is list which contains the keys of changed value in Map
	 */
	private List keyList;
	
	/**
	 * This Map is to get the propertyNames
	 */
	private Map propertyNames=new HashMap();
	
	
	public LogInfo(Object entityId,String entityType,Context context,Map valueMap){
		this.entityId=entityId;
		this.entityType=entityType;
		this.context=context;
		this.valueMap=valueMap; 
    }
	
	
	public LogInfo(Object entityId,String entityType,Context context,LogValueMap valueMap){
		this.entityId=entityId;
		this.entityType=entityType;
		this.context=context;
		this.valueMap=valueMap.getValueMap(); 
    }
	
	public Object getEntityId() {
		return entityId;
	}

	public void setEntityId(Object entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Map getValueMap() {
		return valueMap;
	}

	public void setValueMap(Map valueMap) {
		this.valueMap = valueMap;
	}

	public Map getInitialValues() {
		return initialValues;
	}

	public void setInitialValues(Map initialValues) {
		this.initialValues = initialValues;
	}

	public void setInitialValueMap() {
		InterceptHelper interceptHelper=new InterceptHelper();
		setInitialValues(interceptHelper.hibernateMeta(this,AuditConstants.TRANSACTIONBEGIN));
	}
	

	public Map getChangedValues() {
		return changedValues;
	}

	public void setChangedValues(Map changedValues) {
		this.changedValues = changedValues;
	}

	public Map getPropertyNames() {
		return propertyNames;
	}

	public void setPropertyNames(Map propertyNames) {
		this.propertyNames = propertyNames;
	}
	
	public void addPropertyName(Object key,Object value){
		propertyNames.put(key,value);
	}

}
	

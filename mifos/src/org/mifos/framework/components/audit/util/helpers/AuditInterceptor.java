/**

 * AuditIntercepter.java    version: xxx



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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;

public class AuditInterceptor implements Interceptor {

	private AuditLog auditLog;
	private InterceptHelper interceptHelper;
	private MifosLogger logger;
	private UserContext userContext;
	private Boolean flag=false;


	public AuditInterceptor() {
		logger = MifosLogManager.getLogger(LoggerConstants.AUDITLOGGER);
		interceptHelper = new InterceptHelper();
	}

	public  void createInitialValueMap(Object object){
		userContext=((BusinessObject)object).getUserContext();
		interceptHelper.hibernateMeta(object,AuditConstants.TRANSACTIONBEGIN);
	}
	
	public void createChangeValueMap(Object object){
		logger.debug("createChangeValueMap  enity name : "+interceptHelper.getEntityName());
		logger.debug("createChangeValueMap class: "+object.getClass().getName());
		if(interceptHelper.getEntityName().equals(AuditConfigurtion.getEntityToClassPath(object.getClass().getName())))
			interceptHelper.hibernateMeta(object,AuditConstants.TRANSACTIONEND);
	}
	
	public boolean isAuditLogRequired(){
		if(interceptHelper.isInitialValueMapEmpty())
			return false;
		return true;
	}
	
	public void beforeTransactionCompletion(Transaction tx) {
	}

	public boolean onSave(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) {
		return false;
	}

	public void onDelete(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) {
	}

	public void preFlush(Iterator arg0) {
	}

	public void postFlush(Iterator arg0) {
	}

	public Boolean isTransient(Object arg0) {
		return null;
	}

	public int[] findDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		return null;
	}

	public Object instantiate(String entityName, EntityMode entityMode,
			Serializable id) {
		return null;
	}

	public String getEntityName(Object arg) {
		return null;
	}

	public Object getEntity(String arg0, Serializable arg1) {
		return null;
	}
		
	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2,
			String[] arg3, Type[] arg4) {
		return false;
	}

	public void afterTransactionBegin(Transaction tx) {
	}

	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		return false;
	}
	
	public void afterTransactionCompletion(Transaction tx) {
		if(tx!=null && tx.wasCommitted() && !tx.wasRolledBack())
			flag=true;
		if (tx==null && flag && ((interceptHelper.getInitialValueMap() != null
				&& interceptHelper.getInitialValueMap().size() > 0) || 
				(interceptHelper.getChangeValueMap() != null
						&& interceptHelper.getChangeValueMap().size() > 0))) {
			logger.debug("After transaction completion");
			auditLog = new AuditLog(interceptHelper.getEntityId(), EntityType
					.getEntityValue(interceptHelper.getEntityName().toUpperCase()),
					userContext.getName(),
					new Date(System.currentTimeMillis()), userContext.getId());
			Set<AuditLogRecord> auditLogRecords = createAuditLogRecord();
			auditLog.addAuditLogRecords(auditLogRecords);
			if (!auditLogRecords.isEmpty()) {
				auditLog.save();
			}
		}
	}
	
	private Set<AuditLogRecord> createAuditLogRecord() {
		Set<AuditLogRecord> auditLogRecords=new HashSet<AuditLogRecord>();
		Set set=interceptHelper.getPropertyNames().keySet();
		Iterator iterator=set.iterator();
		while(iterator.hasNext()){
			AuditLogRecord auditLogRecord=null;
			Object key=iterator.next();
			if((key.toString()).toLowerCase().contains(AuditConstants.VERSIONNO)
					|| (key.toString()).toLowerCase().contains(AuditConstants.CREATEDBY)
					|| (key.toString()).toLowerCase().contains(AuditConstants.CREATEDDATE)
					||(key.toString()).toLowerCase().contains(AuditConstants.UPDATEDBY)
					||(key.toString()).toLowerCase().contains(AuditConstants.UPDATEDDATE)
					||(key.toString()).toLowerCase().contains(AuditConstants.LOOKUPID)){
				continue;
			}
			logger.debug("Key: "+key);
			logger.debug("Column Name : " +interceptHelper.getPropertyName(key));
			logger.debug("Initial Value : " + interceptHelper.getInitialValue(key));
			logger.debug("New  Value : " + interceptHelper.getChangeValue(key));
			if (interceptHelper.getInitialValue(key)  != null 
					&& !interceptHelper.getInitialValue(key).toString().trim().equals("")
					&& interceptHelper.getChangeValue(key) == null 
					&& !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY) ) {
				auditLogRecord = new AuditLogRecord(interceptHelper
						.getPropertyName(key).toString().trim(),removeComma(interceptHelper
						.getInitialValue(key).toString()), "-", auditLog);
				auditLogRecords.add(auditLogRecord);
			} else if (interceptHelper.getInitialValue(key) == null
					&& interceptHelper.getChangeValue(key) != null
					&& !interceptHelper.getChangeValue(key).toString().equals("")
					&& !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY)) {
				auditLogRecord = new AuditLogRecord(interceptHelper
						.getPropertyName(key).toString().trim(), "-",
						removeComma(interceptHelper.getChangeValue(key)
								.toString()), auditLog);
				auditLogRecords.add(auditLogRecord);
			} else if (interceptHelper.getChangeValue(key) != null
					&& interceptHelper.getInitialValue(key) != null
					&& !(interceptHelper.getChangeValue(key).toString()).equals(interceptHelper.getInitialValue(key).toString())
					&& (compareSet(interceptHelper.getInitialValue(key).toString(),interceptHelper.getChangeValue(key).toString())==false) 
					&& !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY)) {
				String newValue=null;
				if(interceptHelper.getChangeValue(key).toString().trim().equals("")){
					newValue="-";
				}else{
					newValue=removeComma(interceptHelper.getChangeValue(key).toString());
				}
				String oldValue=null;
				if(interceptHelper.getInitialValue(key).toString().trim().equals("")){
					oldValue="-";
				}else{
					oldValue=removeComma(interceptHelper.getInitialValue(key).toString());
				}
				auditLogRecord=new AuditLogRecord(interceptHelper.getPropertyName(key).toString().trim(),oldValue,newValue,auditLog);
				auditLogRecords.add(auditLogRecord);
			}
		}
		return auditLogRecords;
	}
	

	private boolean compareSet(String initialString,String changeString){
		if(initialString.trim().startsWith(",")){
			initialString=initialString.substring(1,initialString.length()); 
		}
		if(initialString.trim().endsWith(",")){
			initialString=initialString.substring(0,initialString.length()-1); 
		}
		
		if(changeString.trim().startsWith(",")){
			changeString=changeString.substring(1,changeString.length()); 
		}
		if(changeString.trim().endsWith(",")){
			changeString=changeString.substring(0,changeString.length()-1); 
		}
		String[] initialCollectionOfStrings=initialString.split(",");
		String[] changedCollectionOfStrings=changeString.split(",");
		if(initialCollectionOfStrings.length!=changedCollectionOfStrings.length){
			return false;
		}
		int initialCollectionCount=0;
	    int changedCollectionCount=0;
		List<String> initialList = new ArrayList<String>();
		for (int i = 0; i < initialCollectionOfStrings.length; i++) {
			initialList.add(initialCollectionOfStrings[i]);
		}
		List<String> changeList =  new ArrayList<String>();
		for (int i = 0; i < changedCollectionOfStrings.length; i++) {
			changeList.add(changedCollectionOfStrings[i]);
		}
		
	    for (Iterator<String> iter = initialList.iterator(); iter.hasNext();) {
			String  initialValue =  iter.next();
			initialCollectionCount++;
			for (Iterator<String> iterator = changeList.iterator(); iterator.hasNext();) {
				String  changeValue =  iterator.next();
				if(changeValue.equals(initialValue)){
					iterator.remove();
					changedCollectionCount++;
				}
			}
		}
		if(initialCollectionCount==changedCollectionCount){
			return true;
		}
		else{
			return false;
		}
	}
	
	private String removeComma(String string){
		string=string.trim();
		if(string.startsWith(",")){
			string=string.substring(1,string.length()); 
		}
		if(string.endsWith(",")){
			string=string.substring(0,string.length()-1); 
		}
		return string;
	}
	

}

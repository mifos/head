/**

 * AuditIntercepter.java    version: xxx



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

import java.lang.String;
import java.lang.Object;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.Type;
import org.mifos.framework.components.audit.dao.AuditLogDAO;
import org.mifos.framework.components.audit.util.valueobjects.AuditLog;
import org.mifos.framework.components.audit.util.valueobjects.AuditLogRecord;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.hibernate.helper.HibernateUtil;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;
import java.util.Iterator;
import java.lang.Boolean;

/**
 * @author krishankg
 *
 */
public class AuditInterceptor implements Interceptor {

	private LogInfo logInfo = null;
	private LogInfo[] logInfos=null; 
	private AuditLog auditLog = null;
	private Collection<AuditLogRecord> auditLogRecords = null;
	private InterceptHelper interceptHelper=null;
	private Integer DirtyCount=0;
	private Integer FinalCount=0;
	private MifosLogger logger =null;


	public AuditInterceptor() {
		logger = MifosLogManager.getLogger(LoggerConstants.AUDITLOGGER);	
	}

	public AuditInterceptor(LogInfo logInfo) {
		this.logInfo = logInfo;
		logger = MifosLogManager.getLogger(LoggerConstants.AUDITLOGGER);
	}
	
	
	public AuditInterceptor(LogInfo[] logInfo) {
		this.logInfos = logInfo;
		logger = MifosLogManager.getLogger(LoggerConstants.AUDITLOGGER);
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

	public AuditLog  createAuditLog(Collection<AuditLogRecord> auditLogRecord,LogInfo logInfo) {
		auditLog.setFeatureId(new Integer(logInfo.getEntityId().toString()));
		auditLog.setFeatureName(new Short(EntityMasterData.entityMap.get(
				logInfo.getEntityType()).toString()));
		auditLog.setUpdatedDate(new Date(System.currentTimeMillis()));
		auditLog.setUserName(logInfo.getContext().getUserContext().getId());
		auditLog.setActualName(logInfo.getContext().getUserContext().getName());
		auditLog.setAuditLogRecords(auditLogRecords);
		return auditLog; 
	}


	protected Collection<AuditLogRecord> createAuditLogRecord(LogInfo logInfo) {
		AuditLogRecord auditLogRecord = null;
		auditLogRecords = new HashSet<AuditLogRecord>();
		Set set=logInfo.getPropertyNames().keySet();
		Iterator iterator=set.iterator();
		Object key=null;
		while(iterator.hasNext()){
			key=iterator.next();
			if((key.toString()).toLowerCase().contains(AuditConstants.VERSIONNO)
					|| (key.toString()).toLowerCase().contains(AuditConstants.CREATEDBY)
					|| (key.toString()).toLowerCase().contains(AuditConstants.CREATEDDATE)
					||(key.toString()).toLowerCase().contains(AuditConstants.UPDATEDBY)
					||(key.toString()).toLowerCase().contains(AuditConstants.UPDATEDDATE)
					||(key.toString()).toLowerCase().contains(AuditConstants.LOOKUPID)){
				continue;
			}

			logger.debug("Key: "+key);
			if (logInfo.getInitialValues().get(key)  != null 
					&& !logInfo.getInitialValues().get(key).toString().trim().equals("")
					&& logInfo.getChangedValues().get(key) == null 
					&& !logInfo.getPropertyNames().get(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY) ) {
				auditLogRecord = new AuditLogRecord();
				auditLogRecord.setFieldName(logInfo.getPropertyNames().get(key).toString());
				auditLogRecord.setNewValue("-");
				auditLogRecord.setOldValue(removeComma(logInfo.getInitialValues().get(key).toString()));
				auditLogRecord.setAuditLog(auditLog);
				addToAuditLogRecord(auditLogRecord);
			} else if (logInfo.getInitialValues().get(key) == null
					&& logInfo.getChangedValues().get(key) != null
					&& !logInfo.getChangedValues().get(key).toString().equals("")
					&& !logInfo.getPropertyNames().get(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY)) {
				auditLogRecord = new AuditLogRecord();
				auditLogRecord.setFieldName(logInfo.getPropertyNames().get(key).toString());
				auditLogRecord.setNewValue(removeComma(logInfo.getChangedValues().get(key).toString()));
				auditLogRecord.setOldValue("-");
				auditLogRecord.setAuditLog(auditLog);
				addToAuditLogRecord(auditLogRecord);
			} else if (logInfo.getChangedValues().get(key) != null
					&& logInfo.getInitialValues().get(key) != null
					&& !(logInfo.getChangedValues().get(key).toString()).equals(logInfo.getInitialValues().get(key).toString())
					&& (compareSet(logInfo.getInitialValues().get(key).toString(),logInfo.getChangedValues().get(key).toString())==false) 
					&& !logInfo.getPropertyNames().get(key).toString().equalsIgnoreCase(XMLConstants.DONOTLOGTHISPROPERTY)) {
				auditLogRecord = new AuditLogRecord();
				auditLogRecord.setFieldName(logInfo.getPropertyNames().get(key).toString());
				if(logInfo.getChangedValues().get(key).toString().trim().equals("")){
					auditLogRecord.setNewValue("-");
				}else{
					auditLogRecord.setNewValue(removeComma(logInfo.getChangedValues().get(key).toString()));
				}
				if(logInfo.getInitialValues().get(key).toString().trim().equals("")){
					auditLogRecord.setOldValue("-");
				}else{
					auditLogRecord.setOldValue(removeComma(logInfo.getInitialValues().get(key).toString()));
				}
				auditLogRecord.setAuditLog(auditLog);
				addToAuditLogRecord(auditLogRecord);
			}
		}
		return auditLogRecords; 
	}


	protected void addToAuditLogRecord(AuditLogRecord auditLogRecord) {
		auditLogRecords.add(auditLogRecord);
	}

	public void beforeTransactionCompletion(Transaction tx) {
	}

	protected Collection createAuditLogRecord(int[] indexOfChangedProperties,
			Object[] currentState, Object[] previousState,
			String[] propertyNames) {
		return null;
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

	public void afterTransactionCompletion(Transaction tx) {
		if(tx==null){
			if(logInfo!=null && logInfos==null){
				interceptHelper=new InterceptHelper(); ;
				logInfo.setChangedValues(interceptHelper.hibernateMeta(logInfo,AuditConstants.TRANSACTIONEND));
				auditLog=new AuditLog(); 
				Collection<AuditLogRecord> auditLogRecords=createAuditLogRecord(logInfo);
				if (!auditLogRecords.isEmpty()) {
					logInfo.setInitialValues(null);
					logInfo.setChangedValues(null);
					logInfo.setPropertyNames(null);
					AuditLogDAO auditLogDao = new AuditLogDAO();
					auditLogDao.createAuditLog(createAuditLog(auditLogRecords,logInfo));
				}
			}else if(logInfos!=null && logInfo==null){
				for(int i=0;i<logInfos.length;i++){
					interceptHelper=new InterceptHelper(); 
					logInfos[i].setChangedValues(interceptHelper.hibernateMeta(logInfos[i],AuditConstants.TRANSACTIONEND));
					auditLog=new AuditLog();
					Collection<AuditLogRecord> auditLogRecords=createAuditLogRecord(logInfos[i]);
					if (!auditLogRecords.isEmpty()) {
						logInfos[i].setInitialValues(null);
						logInfos[i].setChangedValues(null);
						logInfos[i].setPropertyNames(null);
						AuditLogDAO auditLogDao = new AuditLogDAO();
						auditLogDao.createAuditLog(createAuditLog(auditLogRecords,logInfos[i]));
					}
				}
			}
		}
	}

	
	public boolean compareSet(String initialString,String changeString){
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
		if(initialCollectionOfStrings.length == changedCollectionOfStrings.length){
			for(int i=0;i<initialCollectionOfStrings.length;i++){
				initialCollectionCount++;
				for(int j=0;j<changedCollectionOfStrings.length;j++){
					if(initialCollectionOfStrings[i].equals(changedCollectionOfStrings[j])){
						changedCollectionCount++;
					}
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
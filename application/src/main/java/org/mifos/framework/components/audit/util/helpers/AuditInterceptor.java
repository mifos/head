/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.components.audit.util.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.util.DateTimeService;
import org.mifos.security.util.UserContext;

/*
 * For Hibernate 3.2.2 or so, we'd just extend EmptyInterceptor.
 * But that doesn't exist in 3.0 beta4.
 */
public class AuditInterceptor extends EmptyInterceptor {

    private AuditLog auditLog;
    private InterceptHelper interceptHelper;
    private UserContext userContext;
    private Boolean flag = false;

    public AuditInterceptor() {
        interceptHelper = new InterceptHelper();
    }

    public void createInitialValueMap(Object object) {
        userContext = ((BusinessObject) object).getUserContext();
        interceptHelper.hibernateMeta(object, AuditConstants.TRANSACTIONBEGIN);
    }

    public void createChangeValueMap(Object object) {
        if (interceptHelper.getEntityName().equals(AuditConfigurtion.getEntityToClassPath(object.getClass().getName()))) {
            interceptHelper.hibernateMeta(object, AuditConstants.TRANSACTIONEND);
        }
    }

    public boolean isAuditLogRequired() {
        if (interceptHelper.isInitialValueMapEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
        if (tx != null && tx.wasCommitted() && !tx.wasRolledBack()) {
            flag = true;
        }
        if (flag
                && ((interceptHelper.getInitialValueMap() != null && interceptHelper.getInitialValueMap().size() > 0) || (interceptHelper
                        .getChangeValueMap() != null && interceptHelper.getChangeValueMap().size() > 0))) {
            auditLog = new AuditLog(interceptHelper.getEntityId(), EntityType.getEntityValue(interceptHelper
                    .getEntityName().toUpperCase()), userContext.getName(), new DateTimeService()
                    .getCurrentJavaSqlDate(), userContext.getId());
            Set<AuditLogRecord> auditLogRecords = createAuditLogRecord();
            auditLog.addAuditLogRecords(auditLogRecords);
            if (!auditLogRecords.isEmpty()) {
                auditLog.save();
            }
        }
    }

    private Set<AuditLogRecord> createAuditLogRecord() {
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        Set set = interceptHelper.getPropertyNames().keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            AuditLogRecord auditLogRecord = null;
            Object key = iterator.next();
            if ((key.toString()).toLowerCase().contains(AuditConstants.VERSIONNO)
                    || (key.toString()).toLowerCase().contains(AuditConstants.CREATEDBY)
                    || (key.toString()).toLowerCase().contains(AuditConstants.CREATEDDATE)
                    || (key.toString()).toLowerCase().contains(AuditConstants.UPDATEDBY)
                    || (key.toString()).toLowerCase().contains(AuditConstants.UPDATEDDATE)
                    || (key.toString()).toLowerCase().contains(AuditConstants.LOOKUPID)) {
                continue;
            }
            if (interceptHelper.getInitialValue(key) != null
                    && !interceptHelper.getInitialValue(key).toString().trim().equals("")
                    && interceptHelper.getChangeValue(key) == null
                    && !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(
                            XMLConstants.DONOTLOGTHISPROPERTY)) {
                auditLogRecord = new AuditLogRecord(interceptHelper.getPropertyName(key).toString().trim(),
                        removeComma(interceptHelper.getInitialValue(key).toString()), "-", auditLog);
                auditLogRecords.add(auditLogRecord);
            } else if (interceptHelper.getInitialValue(key) == null
                    && interceptHelper.getChangeValue(key) != null
                    && !interceptHelper.getChangeValue(key).toString().equals("")
                    && !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(
                            XMLConstants.DONOTLOGTHISPROPERTY)) {
                auditLogRecord = new AuditLogRecord(interceptHelper.getPropertyName(key).toString().trim(), "-",
                        removeComma(interceptHelper.getChangeValue(key).toString()), auditLog);
                auditLogRecords.add(auditLogRecord);
            } else if (interceptHelper.getChangeValue(key) != null
                    && interceptHelper.getInitialValue(key) != null
                    && !(interceptHelper.getChangeValue(key).toString()).equals(interceptHelper.getInitialValue(key)
                            .toString())
                    && (compareSet(interceptHelper.getInitialValue(key).toString(), interceptHelper.getChangeValue(key)
                            .toString()) == false)
                    && !interceptHelper.getPropertyName(key).toString().equalsIgnoreCase(
                            XMLConstants.DONOTLOGTHISPROPERTY)) {
                String newValue = null;
                if (interceptHelper.getChangeValue(key).toString().trim().equals("")) {
                    newValue = "-";
                } else {
                    newValue = removeComma(interceptHelper.getChangeValue(key).toString());
                }
                String oldValue = null;
                if (interceptHelper.getInitialValue(key).toString().trim().equals("")) {
                    oldValue = "-";
                } else {
                    oldValue = removeComma(interceptHelper.getInitialValue(key).toString());
                }
                auditLogRecord = new AuditLogRecord(interceptHelper.getPropertyName(key).toString().trim(), oldValue,
                        newValue, auditLog);
                auditLogRecords.add(auditLogRecord);
            }
        }
        return auditLogRecords;
    }

    private boolean compareSet(String initialString, String changeString) {
        if (initialString.trim().startsWith(",")) {
            initialString = initialString.substring(1, initialString.length());
        }
        if (initialString.trim().endsWith(",")) {
            initialString = initialString.substring(0, initialString.length() - 1);
        }

        if (changeString.trim().startsWith(",")) {
            changeString = changeString.substring(1, changeString.length());
        }
        if (changeString.trim().endsWith(",")) {
            changeString = changeString.substring(0, changeString.length() - 1);
        }
        String[] initialCollectionOfStrings = initialString.split(",");
        String[] changedCollectionOfStrings = changeString.split(",");
        if (initialCollectionOfStrings.length != changedCollectionOfStrings.length) {
            return false;
        }
        int initialCollectionCount = 0;
        int changedCollectionCount = 0;
        List<String> initialList = new ArrayList<String>();
        for (String initialCollectionOfString : initialCollectionOfStrings) {
            initialList.add(initialCollectionOfString);
        }
        List<String> changeList = new ArrayList<String>();
        for (String changedCollectionOfString : changedCollectionOfStrings) {
            changeList.add(changedCollectionOfString);
        }

        for (String initialValue : initialList) {
            initialCollectionCount++;
            for (Iterator<String> iterator = changeList.iterator(); iterator.hasNext();) {
                String changeValue = iterator.next();
                if (changeValue.equals(initialValue)) {
                    iterator.remove();
                    changedCollectionCount++;
                }
            }
        }
        if (initialCollectionCount == changedCollectionCount) {
            return true;
        } else {
            return false;
        }
    }

    private String removeComma(String string) {
        string = string.trim();
        if (string.startsWith(",")) {
            string = string.substring(1, string.length());
        }
        if (string.endsWith(",")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

}

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

package org.mifos.framework.components.audit.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.persistence.AuditPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class AuditBusinessService implements BusinessService {

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<AuditLogView> getAuditLogRecords(Short entityType, Integer entityId) throws ServiceException {
        try {
            AuditPersistence auditPersistence = new AuditPersistence();
            PersonnelBusinessService personnelService = new PersonnelBusinessService();
            List<AuditLog> auditLogRecords = auditPersistence.getAuditLogRecords(entityType, entityId);
            List<AuditLogView> auditLogViewList = new ArrayList<AuditLogView>();
            for (AuditLog auditLog : auditLogRecords) {
                for (AuditLogRecord auditLogRecord : auditLog.getAuditLogRecords()) {
                    AuditLogView auditLogView = new AuditLogView();
                    auditLogView.setDate(auditLog.getUpdatedDate().toString());
                    Short userId = auditLog.getUpdatedBy();
                    PersonnelBO personnel = personnelService.getPersonnel(userId);
                    auditLogView.setUser(personnel.getUserName());
                    auditLogView.setField(auditLogRecord.getFieldName());
                    String encryptedPasswordAuditFieldName = AuditConfigurtion.getColumnNameForPropertyName(
                            AuditConstants.PERSONNEL, AuditConstants.Audit_PASSWORD);
                    if ((null != encryptedPasswordAuditFieldName)
                            && (auditLogRecord.getFieldName().equals(encryptedPasswordAuditFieldName.trim()))) {
                        auditLogView.setOldValue(AuditConstants.HIDDEN_PASSWORD);
                        auditLogView.setNewValue(AuditConstants.HIDDEN_PASSWORD);
                    } else {
                        auditLogView.setOldValue(auditLogRecord.getOldValue());
                        auditLogView.setNewValue(auditLogRecord.getNewValue());
                    }
                    auditLogViewList.add(auditLogView);
                }
            }
            return auditLogViewList;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

}

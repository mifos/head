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

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AuditBusinessServiceTest extends MifosIntegrationTestCase {

    public AuditBusinessServiceTest() throws SystemException, ApplicationException {
        super();
    }

    public void testGetAuditLogRecords() throws Exception {
        AuditLog auditLog = new AuditLog(1, (short) 2, "Mifos", new Date(System.currentTimeMillis()), (short) 3);
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        auditLog.save();
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        List<AuditLogView> auditLogViewList = auditBusinessService.getAuditLogRecords((short) 2, 1);
        assertEquals(1, auditLogViewList.size());
        auditLog = getAuditLog(1, (short) 2);
        TestObjectFactory.cleanUp(auditLog);
    }

    public void testGetAuditLogRecordsPasswordChange() throws Exception {
        AuditLog auditLog = new AuditLog(1, (short) 2, "Mifos", new Date(System.currentTimeMillis()), (short) 3);
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("Password", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        auditLog.save();
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        List<AuditLogView> auditLogViewList = auditBusinessService.getAuditLogRecords((short) 2, 1);
        assertEquals(1, auditLogViewList.size());
        AuditLogView auditLogView = auditLogViewList.get(0);
        assertEquals(AuditConstants.HIDDEN_PASSWORD, auditLogView.getOldValue());
        assertEquals(AuditConstants.HIDDEN_PASSWORD, auditLogView.getNewValue());
        auditLog = getAuditLog(1, (short) 2);
        TestObjectFactory.cleanUp(auditLog);
    }

    private AuditLog getAuditLog(Integer entityId, Short entityType) {
        return (AuditLog) StaticHibernateUtil.getSessionTL().createQuery(
                "from AuditLog al where al.entityId=" + entityId + " and al.entityType=" + entityType).uniqueResult();
    }
}

/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.framework.components.audit.persistence;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AuditPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public AuditPersistenceIntegrationTest() throws Exception {
        super();
    }

    public void testSave() {
        AuditLog auditLog = new AuditLog(Integer.valueOf("1"), Short.valueOf("2"), "Mifos", new Date(System
                .currentTimeMillis()), Short.valueOf("3"));
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        auditLog.save();
        auditLog = getAuditLog(Integer.valueOf("1"), Short.valueOf("2"));

       Assert.assertEquals(Integer.valueOf("1"), auditLog.getEntityId());
       Assert.assertEquals(Short.valueOf("2"), auditLog.getEntityType());
       Assert.assertEquals("Mifos", auditLog.getModifierName());
       Assert.assertEquals("Mifos", auditLog.getModifierName());
       Assert.assertEquals(1, auditLog.getAuditLogRecords().size());
        for (AuditLogRecord logRecord : auditLog.getAuditLogRecords()) {
           Assert.assertEquals("ColumnName_1", logRecord.getFieldName());
           Assert.assertEquals("test_1", logRecord.getOldValue());
           Assert.assertEquals("new_test_1", logRecord.getNewValue());
        }
        TestObjectFactory.cleanUp(auditLog);
    }

    public void testGetAuditLogRecords() throws Exception {
        AuditLog auditLog = new AuditLog(Integer.valueOf("1"), Short.valueOf("2"), "Mifos", new Date(System
                .currentTimeMillis()), Short.valueOf("3"));
        Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
        AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1", "test_1", "new_test_1", auditLog);
        auditLogRecords.add(auditLogRecord);
        auditLog.addAuditLogRecords(auditLogRecords);
        auditLog.save();
        auditLog = getAuditLog(Integer.valueOf("1"), Short.valueOf("2"));
        AuditPersistence auditPersistence = new AuditPersistence();
        List<AuditLog> auditLogList = auditPersistence.getAuditLogRecords(Short.valueOf("2"), Integer.valueOf("1"));
       Assert.assertEquals(1, auditLogList.size());
        TestObjectFactory.cleanUp(auditLog);
    }

    private AuditLog getAuditLog(Integer entityId, Short entityType) {
        return (AuditLog) StaticHibernateUtil.getSessionTL().createQuery(
                "from AuditLog al where al.entityId=" + entityId + " and al.entityType=" + entityType).uniqueResult();
    }

}

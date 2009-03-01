package org.mifos.framework.components.audit.persistence;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAuditPersistence extends MifosTestCase {

	public TestAuditPersistence() throws SystemException, ApplicationException {
        super();
    }

    public void testSave() {
		AuditLog auditLog = new AuditLog(Integer.valueOf("1"), Short
				.valueOf("2"), "Mifos", new Date(System.currentTimeMillis()),
				Short.valueOf("3"));
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		auditLog = getAuditLog(Integer.valueOf("1"), Short.valueOf("2"));

		assertEquals(Integer.valueOf("1"), auditLog.getEntityId());
		assertEquals(Short.valueOf("2"), auditLog.getEntityType());
		assertEquals("Mifos", auditLog.getModifierName());
		assertEquals("Mifos", auditLog.getModifierName());
		assertEquals(1, auditLog.getAuditLogRecords().size());
		for (AuditLogRecord logRecord : auditLog.getAuditLogRecords()) {
			assertEquals("ColumnName_1", logRecord.getFieldName());
			assertEquals("test_1", logRecord.getOldValue());
			assertEquals("new_test_1", logRecord.getNewValue());
		}
		TestObjectFactory.cleanUp(auditLog);
	}
	
	public void testGetAuditLogRecords() throws Exception{
		AuditLog auditLog = new AuditLog(Integer.valueOf("1"), Short
				.valueOf("2"), "Mifos", new Date(System.currentTimeMillis()),
				Short.valueOf("3"));
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		auditLog = getAuditLog(Integer.valueOf("1"), Short.valueOf("2"));
		AuditPersistence auditPersistence = new AuditPersistence();
		List<AuditLog> auditLogList=auditPersistence.getAuditLogRecords(Short.valueOf("2"),Integer.valueOf("1"));
		assertEquals(1,auditLogList.size());
		TestObjectFactory.cleanUp(auditLog);
	}

	private AuditLog getAuditLog(Integer entityId, Short entityType) {
		return (AuditLog) HibernateUtil.getSessionTL().createQuery(
				"from AuditLog al where al.entityId=" + entityId
						+ " and al.entityType=" + entityType).uniqueResult();
	}

}

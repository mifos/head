package org.mifos.framework.components.audit.business.service;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAuditBusinessService extends MifosTestCase{

	
	public void testGetAuditLogRecords() throws Exception {
		AuditLog auditLog = new AuditLog(Integer.valueOf("1"), Short
				.valueOf("2"), "Mifos", new Date(System.currentTimeMillis()),
				Short.valueOf("3"));
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		AuditBusinessService auditBusinessService = new AuditBusinessService();
		List<AuditLogView> auditLogViewList = auditBusinessService.getAuditLogRecords(Short.valueOf("2"),Integer.valueOf("1"));
		assertEquals(1,auditLogViewList.size());
		auditLog = getAuditLog(Integer.valueOf("1"), Short.valueOf("2"));
		TestObjectFactory.cleanUp(auditLog);
	}
	
	private AuditLog getAuditLog(Integer entityId, Short entityType) {
		return (AuditLog) HibernateUtil.getSessionTL().createQuery(
				"from AuditLog al where al.entityId=" + entityId
						+ " and al.entityType=" + entityType).uniqueResult();
	}
}

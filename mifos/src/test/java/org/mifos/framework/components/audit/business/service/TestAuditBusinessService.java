package org.mifos.framework.components.audit.business.service;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAuditBusinessService extends MifosTestCase{

	
	public TestAuditBusinessService() throws SystemException, ApplicationException {
        super();
    }

    public void testGetAuditLogRecords() throws Exception {
		AuditLog auditLog = new AuditLog(1, (short)2, "Mifos", 
				new Date(System.currentTimeMillis()), (short)3);
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		AuditBusinessService auditBusinessService = new AuditBusinessService();
		List<AuditLogView> auditLogViewList = auditBusinessService.getAuditLogRecords((short)2,1);
		assertEquals(1,auditLogViewList.size());
		auditLog = getAuditLog(1, (short)2);
		TestObjectFactory.cleanUp(auditLog);
	}
	
	public void testGetAuditLogRecordsPasswordChange() throws Exception {
		AuditLog auditLog = new AuditLog(1, (short)2, "Mifos", 
				new Date(System.currentTimeMillis()), (short)3);
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("Password",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		AuditBusinessService auditBusinessService = new AuditBusinessService();
		List<AuditLogView> auditLogViewList = auditBusinessService.getAuditLogRecords((short)2,1);
		assertEquals(1,auditLogViewList.size());
		AuditLogView auditLogView = auditLogViewList.get(0);
		assertEquals(AuditConstants.HIDDEN_PASSWORD,auditLogView.getOldValue());
		assertEquals(AuditConstants.HIDDEN_PASSWORD,auditLogView.getNewValue());
		auditLog = getAuditLog(1, (short)2);
		TestObjectFactory.cleanUp(auditLog);
	}
	
	private AuditLog getAuditLog(Integer entityId, Short entityType) {
		return (AuditLog) HibernateUtil.getSessionTL().createQuery(
				"from AuditLog al where al.entityId=" + entityId
						+ " and al.entityType=" + entityType).uniqueResult();
	}
}

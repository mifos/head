package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class AuditPersistence extends Persistence {

	public void save(AuditLog auditLog) {
		Session session = null;
		Transaction txn = null;
		try {
			session = HibernateUtil.openSession();
			txn = session.beginTransaction();
			session.save(auditLog);
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			throw new RuntimeException(e);
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<AuditLog> getAuditLogRecords(Short entityType, Integer entityId)
			throws PersistenceException {
		Map<Object, Object> queryParameter = new HashMap<Object, Object>();
		queryParameter.put("entityType", entityType);
		queryParameter.put("entityId", entityId);
		return executeNamedQuery(
				NamedQueryConstants.RETRIEVE_AUDIT_LOG_RECORD, queryParameter);
	}

}

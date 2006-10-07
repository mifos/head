package org.mifos.framework.components.audit.persistence;

import java.sql.Date;
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
			session = HibernateUtil.getSession();
			txn = session.beginTransaction();
			session.save(auditLog);
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			e.printStackTrace();
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
		queryParameter.put("updatedDate", new Date(System.currentTimeMillis()));
		return (List<AuditLog>) executeNamedQuery(
				NamedQueryConstants.RETRIEVE_AUDIT_LOG_RECORD, queryParameter);
	}

}

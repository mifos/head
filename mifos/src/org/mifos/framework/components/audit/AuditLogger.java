package org.mifos.framework.components.audit;

import java.util.Collection;
import java.util.Calendar;
import org.mifos.framework.components.audit.dao.AuditLogDAO;
import org.mifos.framework.components.audit.util.valueobjects.AuditLog;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * Class to create and fetch audit details.
  * @author rajitha
 */
public class AuditLogger {

	public AuditLogger() {

	}

	public void create(ValueObject valueObject) throws SystemException {

	}

	public Collection get(long featureId) {
		return null;
	}

	public void delete(long featureId, Calendar startDateTime,
			Calendar endDateTime) {
		AuditLogDAO auditLogDAO = new AuditLogDAO();
		auditLogDAO.deleteAuditLog(featureId, startDateTime, endDateTime);
	}
}
package org.mifos.framework.components.audit.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.persistence.AuditPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class AuditBusinessService extends BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<AuditLogView> getAuditLogRecords(Short entityType,
			Integer entityId) throws ServiceException {
		try {
			AuditPersistence auditPersistence = new AuditPersistence();
			List<AuditLog> auditLogRecords = auditPersistence
					.getAuditLogRecords(entityType, entityId);
			List<AuditLogView> auditLogViewList = new ArrayList<AuditLogView>();
			for (AuditLog auditLog : auditLogRecords) {
				for (AuditLogRecord auditLogRecord : auditLog
						.getAuditLogRecords()) {
					AuditLogView auditLogView = new AuditLogView();
					auditLogView.setDate(auditLog.getUpdatedDate().toString());
					auditLogView.setUser(auditLog.getModifierName());
					auditLogView.setField(auditLogRecord.getFieldName());
					auditLogView.setOldValue(auditLogRecord.getOldValue());
					auditLogView.setNewValue(auditLogRecord.getNewValue());
					auditLogViewList.add(auditLogView);
				}
			}
			return auditLogViewList;
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

}

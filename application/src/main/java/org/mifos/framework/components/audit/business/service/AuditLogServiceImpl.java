package org.mifos.framework.components.audit.business.service;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.platform.questionnaire.AuditLogService;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;

public class AuditLogServiceImpl implements AuditLogService {

    @Override
    public void addAuditLogRegistry(QuestionGroupDetails questionGroupDetails) {
        if (questionGroupDetails.getDetails().size() > 0) {
            PersonnelBusinessService pbs = new PersonnelBusinessService();
            String source = questionGroupDetails.getDetails().get(0).getEventSource().getSource();
            String event = questionGroupDetails.getDetails().get(0).getEventSource().getEvent();
            String modifierName;
            if (event.toUpperCase().equals("CREATE")) {
                try {
                    modifierName = pbs.getPersonnel((short) (questionGroupDetails.getCreatorId())).getDisplayName();
                } catch (ServiceException e) {
                    modifierName = "";
                }
                AuditLog auditLog = new AuditLog(questionGroupDetails.getEntityId(), EntityType.getEntityValue(source
                        .toUpperCase()), modifierName, new DateTimeService().getCurrentJavaSqlDate(),
                        (short) questionGroupDetails.getCreatorId());
                Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
                for (QuestionGroupDetail group : questionGroupDetails.getDetails()) {
                    auditLogRecords.add(new AuditLogRecord(group.getTitle(), "-", "-", auditLog));
                }
                auditLog.addAuditLogRecords(auditLogRecords);
                auditLog.save();
            }
        }
    }

}

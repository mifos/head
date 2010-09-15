package org.mifos.platform.questionnaire;

import org.mifos.platform.questionnaire.service.QuestionGroupDetails;

public interface AuditLogService {

    void addAuditLogRegistry(QuestionGroupDetails questionGroupDetails);

}

package org.mifos.platform.questionnaire;

import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

public interface AuditLogService {

    void addAuditLogRegistry(QuestionGroupDetail questionGroupDetail, QuestionGroupDetail oldQuestionGroupDetail,
            int creatorId, int entityId, String source, String event);

}

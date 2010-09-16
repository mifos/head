package org.mifos.platform.questionnaire.service;

import org.mifos.platform.questionnaire.AuditLogService;

public class FakeAuditLogServiceImpl implements AuditLogService {

    @Override
    public void addAuditLogRegistry(QuestionGroupDetail questionGroupDetail,
            QuestionGroupDetail oldQuestionGroupDetail, int creatorId, int entityId, String source, String event) {
    }

}

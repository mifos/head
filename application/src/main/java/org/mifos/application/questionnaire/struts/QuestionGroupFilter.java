package org.mifos.application.questionnaire.struts;

import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

import java.util.List;

public interface QuestionGroupFilter {
    List<QuestionGroupDetail> doFilter(List<QuestionGroupDetail> questionGroupDetails);
}

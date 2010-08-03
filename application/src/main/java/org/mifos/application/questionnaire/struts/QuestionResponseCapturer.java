package org.mifos.application.questionnaire.struts;

import java.util.List;

import org.mifos.platform.questionnaire.service.QuestionGroupDetail;

public interface QuestionResponseCapturer {

    /*public void setQuestionGroupDtos(List<QuestionGroupDto> questionGroups);
    public List<QuestionGroupDto> getQuestionGroupDtos();*/
    public void setQuestionGroups(List<QuestionGroupDetail> questionGroups);
    public List<QuestionGroupDetail> getQuestionGroups();


}

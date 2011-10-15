package org.mifos.application.questionnaire.struts;

import javax.servlet.http.HttpServletRequest;

import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.service.MifosServiceFactory;

public class DefaultQuestionnaireServiceFacadeLocator implements QuestionnaireServiceFacadeLocator {
    @Override
    public QuestionnaireServiceFacade getService(HttpServletRequest request) {
        return MifosServiceFactory.getQuestionnaireServiceFacade(request);
    }
}

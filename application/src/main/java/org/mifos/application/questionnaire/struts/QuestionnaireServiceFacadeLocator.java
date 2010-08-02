package org.mifos.application.questionnaire.struts;

import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;

import javax.servlet.http.HttpServletRequest;

public interface QuestionnaireServiceFacadeLocator {

    QuestionnaireServiceFacade getService(HttpServletRequest request);

}

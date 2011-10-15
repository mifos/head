package org.mifos.application.questionnaire.struts;

import javax.servlet.http.HttpServletRequest;

import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;

public interface QuestionnaireServiceFacadeLocator {

    QuestionnaireServiceFacade getService(HttpServletRequest request);

}

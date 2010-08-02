package org.mifos.platform.questionnaire.service;

import javax.servlet.http.HttpServletRequest;

public interface QuestionnaireServiceFacadeLocator {

    QuestionnaireServiceFacade getService(HttpServletRequest request);

}

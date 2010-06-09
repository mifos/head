package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;

public interface QuestionnaireService {
    QuestionResponse defineQuestion(QuestionRequest questionRequest) throws ApplicationException;
}

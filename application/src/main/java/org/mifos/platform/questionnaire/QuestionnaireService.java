package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;

import java.util.List;

public interface QuestionnaireService {
    QuestionDetail defineQuestion(QuestionDefinition questionDefinition) throws ApplicationException;

    List<QuestionDetail> getAllQuestions();
}

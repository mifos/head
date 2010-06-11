package org.mifos.platform.questionnaire.contract;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;

import java.util.List;

public interface QuestionnaireService {
    QuestionDetail defineQuestion(QuestionDefinition questionDefinition) throws ApplicationException;

    List<QuestionDetail> getAllQuestions();
}

package org.mifos.platform.questionnaire.validators;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TYPE_NOT_PROVIDED;
import static org.mifos.platform.questionnaire.contract.QuestionType.INVALID;

public class QuestionValidatorImpl implements QuestionValidator {
    @Override
    public void validate(QuestionDefinition questionDefinition) throws ApplicationException {
        validateQuestionTitle(questionDefinition);
        validateQuestionType(questionDefinition);
    }

    private void validateQuestionType(QuestionDefinition questionDefinition) throws ApplicationException {
        if (INVALID == questionDefinition.getType())
            throw new ApplicationException(QUESTION_TYPE_NOT_PROVIDED);
    }

    private void validateQuestionTitle(QuestionDefinition questionDefinition) throws ApplicationException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new ApplicationException(QUESTION_TITLE_NOT_PROVIDED);
    }
}

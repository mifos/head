package org.mifos.platform.questionnaire;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.exceptions.ApplicationException;

public class QuestionValidatorImpl implements QuestionValidator {
    @Override
    public void validate(QuestionDefinition questionDefinition) throws ApplicationException {
        if (StringUtils.isEmpty(questionDefinition.getTitle()))
            throw new ApplicationException(QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED);
    }
}

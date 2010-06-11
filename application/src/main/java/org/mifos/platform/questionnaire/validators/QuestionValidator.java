package org.mifos.platform.questionnaire.validators;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;

public interface QuestionValidator {
    void validate(QuestionDefinition questionDefinition) throws ApplicationException;
}

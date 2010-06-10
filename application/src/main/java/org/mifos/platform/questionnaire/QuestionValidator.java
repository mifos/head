package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;

public interface QuestionValidator {
    void validate(QuestionDefinition questionDefinition) throws ApplicationException;
}

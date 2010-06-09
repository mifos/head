package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;

public interface QuestionValidator {
    void validate(QuestionRequest questionRequest) throws ApplicationException;
}

package org.mifos.platform.questionnaire;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.persistence.QuestionnaireDao;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_TITLE_NOT_PROVIDED;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceTest {

    private QuestionnaireService questionnaireService;

    @Mock
    private QuestionValidator questionValidator;

    @Mock
    private QuestionnaireDao questionnaireDao;
    private static final Integer QUESTION_ID = Integer.valueOf(10);

    @Before
    public void setUp() {
        questionnaireService = new QuestionnaireServiceImpl(questionValidator, questionnaireDao);
    }

    @Test
    public void shouldDefineQuestion() throws ApplicationException {
        QuestionRequest questionRequest = new QuestionRequest("Test Question Title");
        when(questionnaireDao.create(any(org.mifos.customers.surveys.business.Question.class))).thenReturn(QUESTION_ID);
        try {
            QuestionResponse questionResponse = questionnaireService.defineQuestion(questionRequest);
            assertNotNull(questionResponse);
            assertEquals(QUESTION_ID, questionResponse.getQuestionId());
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
        verify(questionValidator).validate(questionRequest);
        verify(questionnaireDao).create(any(org.mifos.customers.surveys.business.Question.class));
    }

    @Test(expected = ApplicationException.class)
    public void shouldThrowValidationExceptionWhenQuestionTitleIsNull() throws ApplicationException {
        QuestionRequest questionRequest = new QuestionRequest(null);
        doThrow(new ApplicationException(QUESTION_TITLE_NOT_PROVIDED)).when(questionValidator).validate(questionRequest);
        questionnaireService.defineQuestion(questionRequest);
        verify(questionValidator).validate(questionRequest);
    }


}


package org.mifos.platform.questionnaire.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionRequest;
import org.mifos.platform.questionnaire.QuestionResponse;
import org.mifos.platform.questionnaire.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Test
    public void shouldDefineQuestion() {
        String questionTitle = "Test Question Title";
        QuestionRequest questionRequest = new QuestionRequest(questionTitle);
        try {
            QuestionResponse questionResponse = questionnaireService.defineQuestion(questionRequest);
            assertNotNull(questionResponse);
            Integer questionId = questionResponse.getQuestionId();
            assertNotNull(questionId);
            Question question = questionnaireDao.getDetails(questionId);
            assertNotNull(question);
            assertEquals(questionTitle, question.getShortName());
            assertEquals(questionTitle, question.getQuestionText());
        } catch (ApplicationException e) {
            fail("Should not have thrown the validation exception");
        }
    }
}

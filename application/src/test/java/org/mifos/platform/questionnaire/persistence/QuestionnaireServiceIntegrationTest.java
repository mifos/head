package org.mifos.platform.questionnaire.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.contract.QuestionDefinition;
import org.mifos.platform.questionnaire.contract.QuestionDetail;
import org.mifos.platform.questionnaire.contract.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mifos.platform.questionnaire.contract.QuestionType.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/QuestionnaireContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager = "platformTransactionManager", defaultRollback = true)
public class QuestionnaireServiceIntegrationTest {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Test
    @Transactional
    public void shouldDefineQuestion() throws ApplicationException {
        String questionTitle = "Test QuestionDetail Title";
        QuestionDetail questionDetail = defineQuestion(questionTitle);
        assertNotNull(questionDetail);
        Integer questionId = questionDetail.getId();
        assertNotNull(questionId);
        Question questionEntity = questionnaireDao.getDetails(questionId);
        assertNotNull(questionEntity);
        assertEquals(questionTitle, questionEntity.getShortName());
        assertEquals(questionTitle, questionEntity.getQuestionText());
        assertEquals(AnswerType.FREETEXT, questionEntity.getAnswerTypeAsEnum());
    }

    @Test
    @Transactional
    public void shouldGetAllQuestions() throws ApplicationException {
        List<QuestionDetail> questionDetails = questionnaireService.getAllQuestions();
        assertThat(questionDetails.size(), is(0));
        defineQuestion("Q2");
        defineQuestion("Q1");
        questionDetails = questionnaireService.getAllQuestions();
        assertThat(questionDetails.size(), is(2));
    }

    private QuestionDetail defineQuestion(String questionTitle) throws ApplicationException {
        QuestionDefinition questionDefinition = new QuestionDefinition(questionTitle, FREETEXT);
        QuestionDetail questionDetail = questionnaireService.defineQuestion(questionDefinition);
        return questionDetail;
    }
}

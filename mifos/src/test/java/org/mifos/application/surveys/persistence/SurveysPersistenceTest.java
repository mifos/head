package org.mifos.application.surveys.persistence;

import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class SurveysPersistenceTest extends MifosTestCase {
	private TestDatabase database;
	private Question question1;
	private Question question2;
	private Question question3;
	
	@Override
	@Before
	public void setUp() {
		DatabaseSetup.initializeHibernate();
		database = TestDatabase.makeStandard();
		database.installInThreadLocal();
	}

	@Override
	@Before
	public void tearDown() {
		HibernateUtil.resetDatabase();
	}
	
	@Test
	public void testPpi() throws Exception {
		createQuestions();
		SurveysPersistence persistence = new SurveysPersistence();
		
		Question question = persistence.getQuestion(question1.getQuestionId());
		assertEquals(SurveysConstants.QUESTION_TYPE_GENERAL, question.getQuestionType());
		assertEquals("question1", question.getShortName());
		
		question = persistence.getQuestion(question2.getQuestionId());
		assertEquals(SurveysConstants.QUESTION_TYPE_PPI, question.getQuestionType());
		assertEquals("this is a ppi question", question.getQuestionText());
		
		List<Question> allQuestions = persistence.retrieveAllQuestions();
		assertEquals(3, allQuestions.size());
		
		List<Question> ppiQuestions = persistence.getQuestionsByQuestionType(SurveysConstants.QUESTION_TYPE_PPI);
		assertEquals(2, ppiQuestions.size());
		
		List<Question> generalQuestions = persistence.getQuestionsByQuestionType(SurveysConstants.QUESTION_TYPE_GENERAL);
		assertEquals(1, generalQuestions.size());
	}
	
	private void createQuestions() throws PersistenceException {
		question1 = new Question("question1", "this is a non-ppi question", AnswerType.CHOICE);
		QuestionChoice regularChoice1 = new QuestionChoice("Hello World 1");
		QuestionChoice regularChoice2 = new QuestionChoice("Hello World 2");
		question1.addChoice(regularChoice1);
		question1.addChoice(regularChoice2);
		
		question2 = new Question("question2", "this is a ppi question", AnswerType.CHOICE);
		PPIChoice ppiChoice1 = new PPIChoice("Hello PPI World 1");
		PPIChoice ppiChoice2 = new PPIChoice("Hello PPI World 2");
		question2.addChoice(ppiChoice1);
		question2.addChoice(ppiChoice2);
		
		question3 = new Question("question3", "this is another ppi question", AnswerType.CHOICE);
		PPIChoice ppiChoice3 = new PPIChoice("Hello PPI World 3");
		PPIChoice ppiChoice4 = new PPIChoice("Hello PPI World 4");
		PPIChoice ppiChoice5 = new PPIChoice("Hello PPI World 5");
		question3.addChoice(ppiChoice3);
		question3.addChoice(ppiChoice4);
		question3.addChoice(ppiChoice5);
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SurveysPersistenceTest.class);
	}

}

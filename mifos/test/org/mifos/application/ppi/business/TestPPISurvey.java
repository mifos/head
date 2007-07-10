package org.mifos.application.ppi.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.MifosTestCase;

public class TestPPISurvey extends MifosTestCase {

	public void testCreateSurvey() throws Exception {
		PPISurvey survey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
				SurveyType.CLIENT, Country.INDIA);
		
		survey.setVeryPoorMin(0);
		survey.setVeryPoorMax(25);
		survey.setPoorMin(26);
		survey.setPoorMax(50);
		survey.setAtRiskMin(51);
		survey.setAtRiskMax(75);
		survey.setNonPoorMin(76);
		survey.setNonPoorMax(100);
		
		List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
		
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		Question question = new Question("Test Question 1", "What is this question?",
				AnswerType.CHOICE);
		question.setChoices(new ArrayList<QuestionChoice>());
		PPIChoice choice = new PPIChoice("First choice");
		choice.setChoiceId(1);
		choice.setPoints(20);
		question.getChoices().add(choice);
		surveyQuestion.setQuestion(question);
		questions.add(surveyQuestion);
		
		survey.setQuestions(questions);
		
		Survey regularSurvey = new Survey("NON-PPI Test Survey", SurveyState.ACTIVE,
				SurveyType.CLIENT);
		
		PPIPersistence ppiPersistence = new PPIPersistence();
		ppiPersistence.createOrUpdate(survey);
		ppiPersistence.createOrUpdate(regularSurvey);
		
		
		assertEquals(1, ppiPersistence.retrieveAllPPISurveys().size());
		assertEquals(2, ppiPersistence.retrieveAllSurveys().size());
		
		PPISurvey dbPPISurvey =  ppiPersistence.retrieveActivePPISurvey();
		assertNotNull(dbPPISurvey);
		assertEquals(survey.getQuestions().size(), dbPPISurvey.getQuestions().size());
		assertEquals(survey.getNonPoorMin(), dbPPISurvey.getNonPoorMin());
		assertEquals(survey.getName(), dbPPISurvey.getName());
		assertEquals(survey.getCountry(), dbPPISurvey.getCountry());
	}
}

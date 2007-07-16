package org.mifos.framework.util.helpers;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import junit.framework.TestCase;

import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.AnswerType;

public class PPICalculatorTest extends TestCase {

	public void testValidLimits() {
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
		
		assertTrue(PPICalculator.scoreLimitsAreValid(survey));
		
		survey.setVeryPoorMin(0);
		survey.setVeryPoorMax(26);
		survey.setPoorMin(28);
		survey.setPoorMax(29);
		survey.setAtRiskMin(53);
		survey.setAtRiskMax(99);
		survey.setNonPoorMin(97);
		survey.setNonPoorMax(98);
		
		assertFalse(PPICalculator.scoreLimitsAreValid(survey));
	}
	
	public void testCalculateScore() throws Exception{
		PPISurvey survey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
				SurveyType.CLIENT, Country.INDIA);
		
		SurveyInstance instance = new SurveyInstance();
		Set<SurveyResponse> responses = new HashSet<SurveyResponse>();
		
		List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
		
		Question question = new Question("Test Question 1", "What is this question?",
				AnswerType.CHOICE);
		SurveyQuestion surveyQuestion = survey.addQuestion(question, true);
		question.setChoices(new ArrayList<QuestionChoice>());
		PPIChoice choice = new PPIChoice("First choice");
		choice.setChoiceId(1);
		choice.setPoints(20);
		question.getChoices().add(choice);
		surveyQuestion.setQuestion(question);
		SurveyResponse response = new SurveyResponse(instance, surveyQuestion);
		response.setChoiceValue(choice);
		response.setResponseId(1);
		responses.add(response);
		questions.add(surveyQuestion);
		
		question = new Question("Test Question 2", "What is this question?",
				AnswerType.CHOICE);
		surveyQuestion = survey.addQuestion(question, true);
		choice = new PPIChoice("Second choice");
		choice.setChoiceId(2);
		choice.setPoints(15);
		question.getChoices().add(choice);
		surveyQuestion.setQuestion(question);
		response = new SurveyResponse(instance, surveyQuestion);
		response.setChoiceValue(choice);
		response.setResponseId(2);
		responses.add(response);
		questions.add(surveyQuestion);
		
		question = new Question("Test Question 3", "What is this question?",
				AnswerType.CHOICE);
		surveyQuestion = survey.addQuestion(question, true);
		choice = new PPIChoice("Third choice");
		choice.setChoiceId(3);
		choice.setPoints(3);
		question.getChoices().add(choice);
		surveyQuestion.setQuestion(question);
		response = new SurveyResponse(instance, surveyQuestion);
		response.setChoiceValue(choice);
		response.setResponseId(3);
		responses.add(response);
		questions.add(surveyQuestion);
		
		survey.setQuestions(questions);
		
		instance.setSurveyResponses(responses);
		instance.setSurvey(survey);
		
		int score = PPICalculator.calculateScore(instance);
		
		assertEquals(38, score);
	}

}

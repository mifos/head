package org.mifos.application.ppi.helpers;

import java.util.Collections;

import junit.framework.TestCase;
import org.mifos.application.ppi.business.*;
import org.mifos.application.surveys.business.*;

public class TestXmlPPIParser extends TestCase {
	private static XmlPPISurveyParser parser = new XmlPPISurveyParser();
	
	public void testUno() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		System.out.println("Survey name: " + survey.getName()
				+ ", country: " + survey.getCountryAsEnum().toString());
		Collections.sort(survey.getQuestions());
		for (SurveyQuestion sQuestion : survey.getQuestions()) {
			System.out.println("\t-" + sQuestion.getMandatory() + sQuestion.getQuestion().getShortName());
			System.out.println("\t " + sQuestion.getQuestion().getQuestionText());
			for (QuestionChoice choice : sQuestion.getQuestion().getChoices()) {
				PPIChoice ppiChoice = (PPIChoice) choice;
				System.out.println("\t\t" + ppiChoice.getChoiceText() + " - " + ppiChoice.getPoints());
			}
		}
	}
	
}

package org.mifos.application.ppi.helpers;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.w3c.dom.Document;

public class TestXmlPPIParser extends TestCase {
	private static XmlPPISurveyParser parser = new XmlPPISurveyParser();
	
	/**
	 *  Assumes {{@link #testParse()} passes -- parser#parse must work correctly
	 */
	public void testBuildXmlFrom() throws Exception {
		PPISurvey comparisonSurvey = new PPISurvey("Test PPI Survey",
				SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
		
		Question question = new Question("Test Question", "What is your question?",
				AnswerType.CHOICE);
		PPIChoice choice1 = new PPIChoice("What?");
		choice1.setPoints(0);
		PPIChoice choice2 = new PPIChoice("Why?");
		choice2.setPoints(7);
		question.addChoice(choice1);
		question.addChoice(choice2);
		comparisonSurvey.addQuestion(question, true);
		
		String xml = "<?xml version=\"1.0\"?>\n";
		xml += "<ppi country=\"INDIA\" name=\"Test PPI Survey\">\n";
		xml += "<question name=\"Test Question\" mandatory=\"true\" order=\"0\">\n";
		xml += "<text>What is your question?</text>\n";
		xml += "<choice points=\"0\">What?</choice>";
		xml += "<choice points=\"7\">Why?</choice>\n";
		xml += "</question>\n";
		xml += "</ppi>";
		PPISurvey generatedSurvey = parser.parse(
				new ByteArrayInputStream(xml.getBytes("UTF-8")));
		
		assertEquals(comparisonSurvey.toString(), generatedSurvey.toString());
	}
	
	public void testParse() throws Exception {
		String xml = "<?xml version=\"1.0\"?>\n";
		xml += "<ppi country=\"INDIA\" name=\"Test PPI Survey\">\n";
		xml += "<question name=\"Test Question\" mandatory=\"true\" order=\"0\">\n";
		xml += "<text>What is your question?</text>\n";
		xml += "<choice points=\"0\">What?</choice>";
		xml += "<choice points=\"7\">Why?</choice>\n";
		xml += "</question>\n";
		xml += "</ppi>";
		
		PPISurvey survey = parser.parse(
			new ByteArrayInputStream(xml.getBytes("UTF-8")));
		
		assertEquals(Country.INDIA, survey.getCountryAsEnum());
		assertEquals("Test PPI Survey", survey.getName());
		assertEquals(1, survey.getQuestions().size());
		assertEquals(2, survey.getQuestions().get(0).getQuestion().getChoices().size());
	}
	
	public void testChangeName() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyINDIA.xml");
		PPISurvey surveyNew = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyINDIA.xml");
		
		surveyNew.getQuestions().get(0).getQuestion()
				.setShortName("Completely new and unheard of name!");
		
		Document document = parser.buildXmlFrom(surveyNew);
		surveyNew = parser.parseInto(document, new PPISurvey());
		assertTrue(!survey.toString().equals(surveyNew.toString()));
	}
	
}

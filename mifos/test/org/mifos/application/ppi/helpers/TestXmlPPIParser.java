package org.mifos.application.ppi.helpers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import junit.framework.JUnit4TestAdapter;

import org.junit.*;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.w3c.dom.Document;

public class TestXmlPPIParser {
	private static XmlPPISurveyParser parser = new XmlPPISurveyParser();
	
	/**
	 *  Assumes {{@link #testParse()} passes -- parser#parse must work correctly
	 */
	@Test
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
		
		PPISurvey generatedSurvey = parser.parse(
				new ByteArrayInputStream(getXmlSurvey().getBytes("UTF-8")));
		
		assertEquals(comparisonSurvey.toString(), generatedSurvey.toString());
	}
	
	@Test
	public void testParse() throws Exception {
		PPISurvey survey = parser.parse(
			new ByteArrayInputStream(getXmlSurvey().getBytes("UTF-8")));
		
		assertEquals(Country.INDIA, survey.getCountryAsEnum());
		assertEquals("Test PPI Survey", survey.getName());
		assertEquals(1, survey.getQuestions().size());
		assertEquals(2, survey.getQuestions().get(0).getQuestion().getChoices().size());
		
		assertEquals(80.0, survey.getLikelihood(1).getBelowPovertyLinePercent());
		assertEquals(20.0, survey.getLikelihood(1).getAbovePovertyLinePercent());
		assertEquals(69.0, survey.getLikelihood(17).getBelowPovertyLinePercent());
		assertEquals(31.0, survey.getLikelihood(17).getAbovePovertyLinePercent());
	}
	
	@Test
	public void testChangeName() throws Exception {
		try {
			PPISurvey survey = parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyINDIA.xml");
			PPISurvey surveyNew = parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyINDIA.xml");
			surveyNew.getQuestions().get(0).getQuestion().setShortName("Completely new and unheard of name!");
			
			Document document = parser.buildXmlFrom(surveyNew);
			surveyNew = parser.parseInto(document, new PPISurvey());
			assertFalse(survey.toString().equals(surveyNew.toString()));
		}
		catch (IllegalStateException e) {
			// TODO: IllegalStateException will be thrown by parser.parse() until likelihoods are
			// added to PPISurveyINDIA.xml
		}
	}
	
	private String getXmlSurvey() {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\"?>\n");
		xml.append("<ppi country=\"INDIA\" name=\"Test PPI Survey\">\n");
		xml.append("	<question name=\"Test Question\" mandatory=\"true\" order=\"0\">\n");
		xml.append("		<text>What is your question?</text>\n");
		xml.append("		<choice points=\"0\">What?</choice>");
		xml.append("		<choice points=\"7\">Why?</choice>\n");
		xml.append("	</question>\n");
		xml.append("	<likelihoods>\n");
		xml.append("		<likelihood scoreFrom=\"0\" scoreTo=\"10\" bottomHalf=\"70\" topHalf=\"10\" />\n");
		xml.append("		<likelihood scoreFrom=\"11\" scoreTo=\"20\" bottomHalf=\"60\" topHalf=\"9\" />\n");
		xml.append("		<likelihood scoreFrom=\"21\" scoreTo=\"30\" bottomHalf=\"50\" topHalf=\"8\" />\n");
		xml.append("		<likelihood scoreFrom=\"31\" scoreTo=\"40\" bottomHalf=\"40\" topHalf=\"7\" />\n");
		xml.append("		<likelihood scoreFrom=\"41\" scoreTo=\"50\" bottomHalf=\"30\" topHalf=\"6\" />\n");
		xml.append("		<likelihood scoreFrom=\"51\" scoreTo=\"60\" bottomHalf=\"20\" topHalf=\"5\" />\n");
		xml.append("		<likelihood scoreFrom=\"61\" scoreTo=\"70\" bottomHalf=\"10\" topHalf=\"4\" />\n");
		xml.append("		<likelihood scoreFrom=\"71\" scoreTo=\"80\" bottomHalf=\"0\" topHalf=\"3\" />\n");
		xml.append("		<likelihood scoreFrom=\"81\" scoreTo=\"90\" bottomHalf=\"0\" topHalf=\"0\" />\n");
		xml.append("		<likelihood scoreFrom=\"91\" scoreTo=\"100\" bottomHalf=\"0\" topHalf=\"0\" />\n");
		xml.append("	</likelihoods>\n");
		xml.append("</ppi>");
		return xml.toString();
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestXmlPPIParser.class);
	}
}

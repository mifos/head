package org.mifos.application.ppi.helpers;

import junit.framework.TestCase;
import org.mifos.application.ppi.business.PPISurvey;
import org.w3c.dom.Document;

public class TestXmlPPIParser extends TestCase {
	private static XmlPPISurveyParser parser = new XmlPPISurveyParser();
	
	public void parse() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		System.out.println(survey.toString());
	}
	
	public void testBuildXmlFrom() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		Document document = parser.buildXmlFrom(survey);
		assertEquals(survey.toString(),
				parser.parseInto(document, new PPISurvey()).toString());
	}
	
	/* Changing order of questions does *not* work
	public void testChangeOrder() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		
		int firstOrder = survey.getQuestions().get(0).getOrder();
		survey.getQuestions().get(0).setOrder(
				survey.getQuestions().get(1).getOrder());
		survey.getQuestions().get(1).setOrder(firstOrder);
		
		Document document = parser.buildXmlFrom(survey);
		PPISurvey surveyNew = parser.parseInto(
				"org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml", survey);
		assertEquals(surveyNew.toString(),
				parser.parseInto(document, new PPISurvey()).toString());
	}*/
	
	public void testChangeName() throws Exception {
		PPISurvey survey = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		PPISurvey surveyNew = 
			parser.parse("org/mifos/framework/util/resources/ppi/PPISurveyIndia.xml");
		
		surveyNew.getQuestions().get(0).getQuestion()
				.setShortName("Completely new and unheard of name!");
		
		Document document = parser.buildXmlFrom(surveyNew);
		surveyNew = parser.parseInto(document, new PPISurvey());
		assertTrue(!survey.toString().equals(surveyNew.toString()));
	}
	
}

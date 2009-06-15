/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.ppi.helpers;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.config.GeneralConfig;

public class XmlPPIParserTest extends TestCase {
    private static final double DELTA = 0.00000001;
    private static XmlPPISurveyParser parser = new XmlPPISurveyParser();

    /**
     * Assumes {{@link #testParse()} passes -- parser#parse must work correctly
     */
    public void testBuildXmlFrom() throws Exception {
        PPISurvey comparisonSurvey = new PPISurvey("Test PPI Survey", SurveyState.ACTIVE, SurveyType.CLIENT,
                Country.INDIA);

        Question question = new Question("Test Question", "What is your question?", AnswerType.CHOICE);
        PPIChoice choice1 = new PPIChoice("What?");
        choice1.setPoints(0);
        PPIChoice choice2 = new PPIChoice("Why?");
        choice2.setPoints(7);
        question.addChoice(choice1);
        question.addChoice(choice2);
        comparisonSurvey.addQuestion(question, true);

        PPISurvey generatedSurvey = parser.parse(new ByteArrayInputStream(getMockXmlSurvey().getBytes("UTF-8")));

        assertEquals(comparisonSurvey.toString(), generatedSurvey.toString());
    }

    public void testParse() throws Exception {
        PPISurvey survey = parser.parse(new ByteArrayInputStream(getMockXmlSurvey().getBytes("UTF-8")));

        assertEquals(Country.INDIA, survey.getCountryAsEnum());
        assertEquals("Test PPI Survey", survey.getName());
        assertEquals(1, survey.getQuestions().size());
        assertEquals(2, survey.getQuestions().get(0).getQuestion().getChoices().size());

        assertEquals(80.0, survey.getLikelihood(1).getBelowPovertyLinePercent(), DELTA);
        assertEquals(20.0, survey.getLikelihood(1).getAbovePovertyLinePercent(), DELTA);
        assertEquals(69.0, survey.getLikelihood(17).getBelowPovertyLinePercent(), DELTA);
        assertEquals(31.0, survey.getLikelihood(17).getAbovePovertyLinePercent(), DELTA);
    }

    public void testMaximumPoints() throws Exception {
        try {
            parser.parse(new ByteArrayInputStream(getInvalidXmlSurvey().getBytes("UTF-8")));
            fail("should have thrown an exception");
        } catch (IllegalStateException e) {
            int nonPoorMax = GeneralConfig.getMaxPointsPerPPISurvey();
            assertEquals("org.mifos.framework.exceptions.ValidationException: Question choices amount to more than "
                    + nonPoorMax + " points.", e.getMessage());
        }
    }

    private String getMockXmlSurvey() {
        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\"?>\n");
        xml.append("<ppi country=\"INDIA\" name=\"Test PPI Survey\">\n");
        xml.append("	<questions>\n");
        xml.append("	    <question name=\"Test Question\" mandatory=\"true\" order=\"0\">\n");
        xml.append("		    <text>What is your question?</text>\n");
        xml.append("		    <choice points=\"0\">What?</choice>");
        xml.append("		    <choice points=\"7\">Why?</choice>\n");
        xml.append("	    </question>\n");
        xml.append("	</questions>\n");
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

    private String getInvalidXmlSurvey() {
        StringBuffer xml = new StringBuffer("<?xml version=\"1.0\"?>\n");
        xml.append("<ppi country=\"INDIA\" name=\"Test PPI Survey\">\n");
        xml.append("	<question name=\"Test Question1\" mandatory=\"true\" order=\"0\">\n");
        xml.append("		<text>What is your question1?</text>\n");
        xml.append("		<choice points=\"0\">What?</choice>");
        xml.append("		<choice points=\"23\">Why?</choice>\n");
        xml.append("	</question>\n");
        xml.append("	<question name=\"Test Question2\" mandatory=\"true\" order=\"1\">\n");
        xml.append("		<text>What is your question2?</text>\n");
        xml.append("		<choice points=\"10\">What?</choice>");
        xml.append("		<choice points=\"80\">Why?</choice>\n");
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

}

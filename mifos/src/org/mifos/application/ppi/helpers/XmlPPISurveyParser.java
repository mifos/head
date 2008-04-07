/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPILikelihood;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlPPISurveyParser {
	
	/** TODO: Should be private */
	public PPISurvey parseInto(String uri, PPISurvey survey)
            throws URISyntaxException, IOException, ParserConfigurationException, SAXException {
		InputStream xml = ResourceLoader.getURI(uri).toURL().openStream();
		return parseInto(xml, survey);
	}
	
	private PPISurvey parseInto(InputStream stream, PPISurvey survey)
            throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(stream);
		
		return parseInto(document, survey);
	}

    PPISurvey parseInto(Document document, PPISurvey survey) {
		Element docElement = document.getDocumentElement();
		parseSurveyName(survey, docElement);
		parseSurveyCountry(survey, docElement);
		parseSurveyQuestions(survey, docElement);
		parseSurveyLikelihoods(survey, docElement);
		return survey;
	}
    
    private void parseSurveyName(PPISurvey survey, Element docElement) {
		survey.setName(docElement.getAttribute("name"));
	}

	private void parseSurveyCountry(PPISurvey survey, Element docElement) {
		survey.setCountry(Country.valueOf(docElement.getAttribute("country")));
	}

	private void parseSurveyQuestions(PPISurvey survey, Element docElement) {
		List<SurveyQuestion> surveyQuestions = survey.getQuestions();
		boolean emptyQuestionList = surveyQuestions.size() == 0;
		
		NodeList questionNodes = docElement.getElementsByTagName("question");
		for (int i = 0; i < questionNodes.getLength(); i++) {
			Element node = (Element) questionNodes.item(i);
			String name = null;
			String mandatory = null;
			Integer order = null;
			String questionText = node.getElementsByTagName("text").item(0).getTextContent();

			if (node.hasAttributes()) {
				name = node.getAttributes().getNamedItem("name").getNodeValue();
				mandatory = node.getAttributes().getNamedItem("mandatory").getNodeValue();
				order = Integer.parseInt(node.getAttributes().getNamedItem("order").getNodeValue());
			}
			if (name == null || mandatory == null || order == null ||questionText == null)
				throw new IllegalStateException("Malformatted xml file");
			
			SurveyQuestion surveyQuestion = new SurveyQuestion();
			surveyQuestion.setSurvey(survey);
			Question question = new Question();
			if (!emptyQuestionList) {
				surveyQuestion = surveyQuestions.get(order);
				question = surveyQuestion.getQuestion();
			} else {
				surveyQuestions.add(surveyQuestion);
				surveyQuestion.setQuestion(question);
			}
			
			question.setShortName(name);
			question.setQuestionText(questionText);
			question.setAnswerType(AnswerType.CHOICE);
			
			parseQuestionChoices(node, question, emptyQuestionList);
			
			surveyQuestion.setMandatory(Boolean.parseBoolean(mandatory));
			surveyQuestion.setOrder(order);
		}
		
		survey.setQuestions(surveyQuestions);
	}

	private void parseQuestionChoices(Element questionNode, Question question, boolean emptyQuestionList) {
		NodeList choices = questionNode.getElementsByTagName("choice");
		for (int i = 0; i < choices.getLength(); i++) {
			Node node = choices.item(i);
			PPIChoice choice = new PPIChoice();
			if (!emptyQuestionList) 
				choice = (PPIChoice) question.getChoices().get(i);
			else 
				question.addChoice(choice);
			
			choice.setChoiceText(node.getTextContent());
			Integer points = Integer.parseInt(node.getAttributes().getNamedItem("points").getNodeValue());
			choice.setPoints(points);
		}
	}

	private void parseSurveyLikelihoods(PPISurvey survey, Element docElement) {
		try {
			List<PPILikelihood> likelihoodsList = new ArrayList<PPILikelihood>();
			NodeList likelihoods = docElement.getElementsByTagName("likelihood");
			for (int i = 0; i < likelihoods.getLength(); i++) {
				PPILikelihood likelihood = parseLikelihood(likelihoods.item(i));
				likelihood.setSurvey(survey);
				likelihoodsList.add(likelihood);
			}
			survey.setLikelihoods(likelihoodsList);
		} catch (ValidationException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private PPILikelihood parseLikelihood(Node node) throws ValidationException {
		int scoreFrom = Integer.parseInt(node.getAttributes().getNamedItem("scoreFrom").getNodeValue());
		int scoreTo = Integer.parseInt(node.getAttributes().getNamedItem("scoreTo").getNodeValue());
		double bottomHalfPct = Double.parseDouble(node.getAttributes().getNamedItem("bottomHalf").getNodeValue());
		double topHalfPct = Double.parseDouble(node.getAttributes().getNamedItem("topHalf").getNodeValue());
		return new PPILikelihood(scoreFrom, scoreTo, bottomHalfPct, topHalfPct);
	}

	public PPISurvey parse(String uri) throws Exception {
		return parseInto(uri, new PPISurvey());
	}
	
	/** TODO: Do not create new PPISurvey instance here, do it
	 * in the body of parseInto()
	 * TODO: This method is never used
	 */
	public PPISurvey parse(InputStream stream) throws Exception {
		return parseInto(stream, new PPISurvey());
	}
	
	public PPISurvey parse(Document document) throws Exception {
		return parseInto(document, new PPISurvey());
	}
	
	public Document buildXmlFrom(PPISurvey survey) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		
		Element docElement = document.createElement("ppi");
		docElement.setAttribute("country", survey.getCountryAsEnum().toString());
		docElement.setAttribute("name", survey.getName());
		
		List<SurveyQuestion> surveyQuestions = survey.getQuestions();
		Collections.sort(surveyQuestions);
		for (SurveyQuestion surveyQuestion : surveyQuestions) {
			Element questionNode = document.createElement("question");
			questionNode.setAttribute("name", surveyQuestion.getQuestion().getShortName());
			questionNode.setAttribute("mandatory", surveyQuestion.getMandatory() == 1 ? "true" : "false");
			questionNode.setAttribute("order", surveyQuestion.getOrder().toString());
			
			Element text = document.createElement("text");
			text.setTextContent(surveyQuestion.getQuestion().getQuestionText());
			questionNode.appendChild(text);
			
			for (QuestionChoice choice : surveyQuestion.getQuestion().getChoices()) {
				Element choiceNode = document.createElement("choice");
				choiceNode.setAttribute("points", Integer.toString(((PPIChoice)choice).getPoints()));
				choiceNode.setTextContent(choice.getChoiceText());
				questionNode.appendChild(choiceNode);
			}
			
			docElement.appendChild(questionNode);
		}
		document.appendChild(docElement);
		return document;
	}

}

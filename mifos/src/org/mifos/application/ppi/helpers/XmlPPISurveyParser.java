package org.mifos.application.ppi.helpers;

import java.io.InputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.net.URISyntaxException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
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
		DocumentBuilderFactory factory = DocumentBuilderFactory
		.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(stream);
		
		return parseInto(document, survey);
	}

    PPISurvey parseInto(Document document, PPISurvey survey) {
		Element docElement = document.getDocumentElement();
		
		Country country = Country.valueOf(docElement.getAttribute("country"));
		String surveyName = docElement.getAttribute("name");
		survey.setCountry(country);
		survey.setName(surveyName);
		
		List<SurveyQuestion> surveyQuestions = survey.getQuestions();
		boolean emptyQuestionList = surveyQuestions.size() == 0;
		NodeList questionNodeList = docElement.getElementsByTagName("question");
		for (int i = 0; i < questionNodeList.getLength(); i++) {
			Element questionNode = (Element)questionNodeList.item(i);
			String name = null;
			String mandatory = null;
			Integer order = null;
			if (questionNode.hasAttributes()) {
				name = questionNode.getAttributes().getNamedItem("name").getNodeValue();
				mandatory = questionNode.getAttributes().getNamedItem("mandatory").getNodeValue();
				order = Integer.parseInt(questionNode.getAttributes().getNamedItem("order").getNodeValue());
			}
			
			Node textNode = questionNode.getElementsByTagName("text").item(0);
			String questionText = textNode.getTextContent();
			
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
			
			NodeList choiceNodeList = questionNode.getElementsByTagName("choice");
			for (int j = 0; j < choiceNodeList.getLength(); j++) {
				Node choiceNode = choiceNodeList.item(j);
				PPIChoice choice = new PPIChoice();
				if (!emptyQuestionList) {
 					choice = (PPIChoice)question.getChoices().get(j);
				} else {
					question.addChoice(choice);
				}
				choice.setChoiceText(choiceNode.getTextContent());
				String points = choiceNode.getAttributes().getNamedItem("points").getNodeValue();
				choice.setPoints(Integer.parseInt(points));
			}
			
			surveyQuestion.setMandatory(Boolean.parseBoolean(mandatory));
			surveyQuestion.setOrder(order);
		}
		
		survey.setQuestions(surveyQuestions);
		
		return survey;
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
		DocumentBuilderFactory factory = DocumentBuilderFactory
		.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		
		Element docElement = document.createElement("ppi");
		docElement.setAttribute("country", survey.getCountryAsEnum().toString());
		docElement.setAttribute("name", survey.getName());
		
		List<SurveyQuestion> surveyQuestions = survey.getQuestions();
		Collections.sort(surveyQuestions);
		for (SurveyQuestion surveyQuestion : surveyQuestions) {
			Element questionNode = document.createElement("question");
			questionNode.setAttribute("name",
					surveyQuestion.getQuestion().getShortName());
			questionNode.setAttribute("mandatory",
					surveyQuestion.getMandatory() == 1 ? "true" : "false");
			questionNode.setAttribute("order", surveyQuestion.getOrder().toString());
			
			Element text = document.createElement("text");
			text.setTextContent(surveyQuestion.getQuestion().getQuestionText());
			questionNode.appendChild(text);
			
			for (QuestionChoice choice : surveyQuestion.getQuestion().getChoices()) {
				Element choiceNode = document.createElement("choice");
				choiceNode.setAttribute("points",
						Integer.toString(((PPIChoice)choice).getPoints()));
				choiceNode.setTextContent(choice.getChoiceText());
				questionNode.appendChild(choiceNode);
			}
			
			docElement.appendChild(questionNode);
		}
		document.appendChild(docElement);
		return document;
	}

}

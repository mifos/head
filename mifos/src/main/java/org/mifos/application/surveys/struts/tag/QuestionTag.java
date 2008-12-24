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
package org.mifos.application.surveys.struts.tag;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts.taglib.TagUtils;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.struts.tags.XmlBuilder;

public class QuestionTag extends BodyTagSupport {
	
	private class AttributeList extends LinkedList<String> {
		
		public void add(String name, String value) {
			add(name);
			add(value);
		}
		
		@Override
		public String[] toArray() {
			Object[] array = super.toArray();
			String[] results = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				results[i] = (String) array[i];
			}
			return results;
		}
	
	}
	
	private static List<String> trueValues = new LinkedList<String>();
	
	static {
		trueValues.add("true");
		trueValues.add("yes");
		trueValues.add("disabled");
	}
	
	private String questionId;
	
	private String value = "";
	
	private String isDisabled = "false";
	
	public QuestionTag() {}
	
	public QuestionTag(Integer questionId, String value, Boolean disabled) {
		this.questionId = questionId.toString();
		this.value = value;
		this.isDisabled = disabled.toString();
	}
	
	private boolean getDisabled() {
		return trueValues.contains(getIsDisabled().toLowerCase().trim());
	}
	
	public String getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(String isDisabled) {
		this.isDisabled = isDisabled;
	}
	
	@Override
	public int doStartTag() throws JspException {
		try {

			TagUtils.getInstance().write(pageContext, getQuestionMarkup());
			
		} catch (Exception e) {
			/**
			    This turns into a (rather ugly) error 500.
			    TODO: make it more reasonable.
			 */
			throw new JspException(e);
		}
		return EVAL_PAGE;	
	}
	
	public String getQuestionMarkup() {
		return getQuestionMarkup(new XmlBuilder());
	}

	public String getQuestionMarkup(XmlBuilder html) {
		SurveysPersistence persistence = new SurveysPersistence();
		int questionIdInt = Integer.parseInt(getQuestionId());
		
		Question question = persistence.getQuestion(questionIdInt);
	
		String name = "value(response_" + Integer.toString(question.getQuestionId()) + ")";
		if (question.getAnswerTypeAsEnum() == AnswerType.MULTISELECT) {
			String[] valueArray = value.split(",");
			int i = 1;
			for (QuestionChoice choice : question.getChoices()) {
				AttributeList attributes = new AttributeList();
				attributes.add("type", "checkbox");
				attributes.add("class", "surveyform singleselect fontnormal8pt");
				attributes.add("name", name);
				attributes.add("value", "1");
				if (getDisabled()) {
					attributes.add("disabled", "disabled");
				}
				
				if (i < valueArray.length && valueArray[i].equals("1")) {
					attributes.add("checked", "checked");
				}
				
				html.startTag("input", attributes.toArray());
				html.text(choice.getChoiceText());
				html.endTag("input");
				html.singleTag("br", new String[] {});
				html.newline();
				
				i++;
			}
		}
		else if (question.getAnswerTypeAsEnum() == AnswerType.FREETEXT) {
			AttributeList attributes = new AttributeList();
			attributes.add("class", "surveyform freetext fontnormal8pt");
			attributes.add("cols", "70");
			attributes.add("rows", "10");
			attributes.add("name", name);
			if (getDisabled()) {
				attributes.add("disabled", "disabled");
			}
			html.startTag("textarea", attributes.toArray());
			html.text(value);
			html.endTag("textarea");
		}
		else if (question.getAnswerTypeAsEnum() == AnswerType.NUMBER) {
			AttributeList attributes = new AttributeList();
			attributes.add("type", "text");
			attributes.add("class", "surveyform number fontnormal8t");
			attributes.add("name", name);
			attributes.add("value", getValue());
			if (getDisabled() == true) {
				attributes.add("disabled", "disabled");
			}
			html.singleTag("input", attributes.toArray());
		}
		
		else if (question.getAnswerTypeAsEnum() == AnswerType.CHOICE) {
			for (QuestionChoice choice : question.getChoices()) {
				AttributeList attributes = new AttributeList();
				attributes.add("type", "radio");
				attributes.add("class", "surveyform singleselect fontnormal8pt");
				attributes.add("name", name);
				attributes.add("value", Integer.toString(choice.getChoiceId()));
				if (getDisabled()) {
					attributes.add("disabled", "disabled");
				}
				
				if (value.equals(Integer.toString(choice.getChoiceId()))) {
					attributes.add("checked", "checked");
				}
				
				html.startTag("input", attributes.toArray());
				html.text(choice.getChoiceText());
				html.endTag("input");
				html.singleTag("br", new String[] {});
				html.newline();
			}

		}
		else if (question.getAnswerTypeAsEnum() == AnswerType.DATE) {
			String nameTemplate = "value(response_%s_%s)";
			StringTokenizer tokenizer = new StringTokenizer(getValue(), "/");
			String[] dateParts = {"", "", ""};

			for (int i = 0; i < 3; i++) {
				if (tokenizer.hasMoreTokens()) {
					dateParts[i] = tokenizer.nextToken();
				}
			}
			String disabledAttr = getDisabled() ? null : "disabled";
			html.singleTag("input", "type", "text", "maxlength", "2", "size",
					"2", "class", "surveyform dateinput fontnormal8pt", "name", String
							.format(nameTemplate, getQuestionId(), "DD", disabledAttr, "disabled"),
					"value", dateParts[0]);
			html.text(" DD ");
			html.singleTag("input", "type", "text", "maxlength", "2", "size",
					"2", "class", "surveyform dateinput fontnormal8pt", "name", String
							.format(nameTemplate, getQuestionId(), "MM", disabledAttr, "disabled"),
					"value", dateParts[1]);
			html.text(" MM ");
			html.singleTag("input", "type", "text", "maxlength", "4", "size",
					"4", "class", "surveyform dateinput fontnormal8pt", "name",  String
							.format(nameTemplate, getQuestionId(), "YY", disabledAttr, "disabled"),
					"value", dateParts[2]);
			html.text(" YYYY ");
		}
		return html.getOutput();
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

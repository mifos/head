package org.mifos.application.surveys.struts.actionforms;

import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class QuestionActionForm extends BaseActionForm {

	private String questionText;
	private String shortName;
	private String answerType;
	private String choice;
	private String questionState;

	public void clear() {
		setQuestionState(1);
		setShortName("");
		setQuestionText("");
		setAnswerType(Integer.toString(AnswerType.FREETEXT.getValue()));
		setChoice("");
	}
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String text) {
		this.questionText = text;
	}

	public String getAnswerType() {
		return answerType;
	}

	public void setAnswerType(String type) {
		this.answerType = type;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getShortName() {
		return shortName;
	}

	public void setQuestionState(String questionState) {
		this.questionState = questionState;
	}

	public void setQuestionState(int questionState) {
		this.questionState = Integer.toString(questionState);
	}

	public String getQuestionState() {
		return questionState;
	}
}

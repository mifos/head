package org.mifos.application.surveys.business;

import org.mifos.framework.business.PersistentObject;

public class Question extends PersistentObject {
	private int questionId;
	
	private int answerType;
	
	private int questionState;
	
	private String questionText;
	
	private int numericMin;
	
	private int numericMax;

	public int getAnswerType() {
		return answerType;
	}

	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}

	public int getNumericMax() {
		return numericMax;
	}

	public void setNumericMax(int numericMax) {
		this.numericMax = numericMax;
	}

	public int getNumericMin() {
		return numericMin;
	}

	public void setNumericMin(int numericMin) {
		this.numericMin = numericMin;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getQuestionState() {
		return questionState;
	}

	public void setQuestionState(int questionState) {
		this.questionState = questionState;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
}

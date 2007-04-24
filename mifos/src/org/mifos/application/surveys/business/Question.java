package org.mifos.application.surveys.business;

import java.util.LinkedList;
import java.util.List;

import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;

public class Question {
	private int questionId;
	
	private AnswerType answerType;
	
	private QuestionState questionState;
	
	private String questionText;
	
	private int numericMin;
	
	private int numericMax;
	
	private List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
	
	public Question() {
		questionState = QuestionState.ACTIVE;
	}
	
	public Question(String questionText) {
		this();
		setQuestionText(questionText);
	}
	
	public Question(String questionText, AnswerType answerType) {
		this();
		setQuestionText(questionText);
		setAnswerType(answerType);
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

	public QuestionState getQuestionStateAsEnum() {
		return questionState;
	}
	
	public int getQuestionState() {
		return questionState.getValue();
	}
	
	public void setQuestionState(int state) {
		this.questionState = QuestionState.fromInt(state);
	}

	public void setQuestionState(QuestionState questionState) {
		this.questionState = questionState;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public AnswerType getAnswerTypeAsEnum() {
		return answerType;
	}
	
	public int getAnswerType() {
		return answerType.getValue();
	}
	
	public void setAnswerType(AnswerType answerType) {
		this.answerType = answerType;
	}
	
	public void setAnswerType(int answerType) {
		this.answerType = AnswerType.fromInt(answerType);
	}

	@Override
	public String toString() {
		return "<Question " + questionId + " \"" + questionText + "\">";
	}

	public List<QuestionChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<QuestionChoice> choices) {
		this.choices = choices;
	}
	
	public void addChoice(QuestionChoice choice) {
		getChoices().add(choice);
	}
}

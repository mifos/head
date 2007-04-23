package org.mifos.application.surveys.business;

import java.math.BigDecimal;

import org.mifos.application.surveys.exceptions.SurveyExceptionConstants;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyResponse {
	private int responseId;
	
	private String value;
	
	private SurveyInstance instance;
	
	private Question question;

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public SurveyInstance getInstance() {
		return instance;
	}

	public void setInstance(SurveyInstance instance) {
		this.instance = instance;
	}

	public int getResponseId() {
		return responseId;
	}

	public void setResponseId(int responseId) {
		this.responseId = responseId;
	}

	public String getValue() {
		return value;
	}

	// TODO: this is in a currently non-working state
	public void setValue(String value) throws ApplicationException {
		if (question == null) {
			throw new ApplicationException(SurveyExceptionConstants.INVALIDACTION);
		}
		
		if (question.getAnswerTypeAsEnum() == AnswerType.FREETEXT) {
			this.value = value;
		}
		else if (question.getAnswerTypeAsEnum() == AnswerType.NUMBER) {
			try {
				BigDecimal numValue = new BigDecimal(value);
				this.value = numValue.toString();
			}
			
			catch (NumberFormatException e) {
				throw new ApplicationException(SurveyExceptionConstants.INVALIDNUMBER);
			}
		}
		else if (question.getAnswerTypeAsEnum() == AnswerType.DATE) {
			try {
				DateUtils.getDateAsSentFromBrowser(value);
				this.value = value;
			}
			catch (InvalidDateException e) {
				throw new ApplicationException(SurveyExceptionConstants.INVALIDDATE);
			}
		}
		
		else if (question.getAnswerTypeAsEnum() == AnswerType.CHOICE) {
			boolean found = false;
			for (QuestionChoice choice : question.getChoices()) {
				if (Integer.toString(choice.getChoiceId()).equals(value)) {
					found = true;
					this.value = value;
				}
			}
			
			if (!found) {
				throw new ApplicationException(SurveyExceptionConstants.INVALIDCHOICE);
			}
		}
	}

}

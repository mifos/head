package org.mifos.application.surveys.business;

import java.util.Date;

import org.mifos.application.surveys.exceptions.SurveyExceptionConstants;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.DateUtils;

public class SurveyResponse {
	private int responseId;
	
	private SurveyInstance instance;
	
	private Question question;
	
	private String freetextValue;
	
	private Date dateValue;
	
	private QuestionChoice choiceValue;
	
	private String multiSelectValue;
	
	private Double numberValue;
	
	
	public SurveyResponse(SurveyInstance instance, Question question) {
		setInstance(instance);
		setQuestion(question);
	}
	
	public SurveyResponse() {}


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

	public QuestionChoice getChoiceValue() {
		return choiceValue;
	}

	public void setChoiceValue(QuestionChoice choice) throws ApplicationException {
		if (question.getAnswerTypeAsEnum() != AnswerType.CHOICE) {
			throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
		}
		this.choiceValue = choice;
	}

	public void setMultiSelectValue(String multiSelectValue) {
		this.multiSelectValue = multiSelectValue;
	}

	public String getMultiSelectValue() {
		return multiSelectValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) throws ApplicationException {
		if (question.getAnswerTypeAsEnum() != AnswerType.DATE) {
			throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
		}
		this.dateValue = dateValue;
	}

	public String getFreetextValue() {
		return freetextValue;
	}

	public void setFreetextValue(String freetextValue) throws ApplicationException {
		if (question.getAnswerTypeAsEnum() != AnswerType.FREETEXT) {
			throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
		}
		this.freetextValue = freetextValue;
	}

	public Double getNumberValue() {
		return numberValue;
	}

	
	public void setNumberValue(double numberValue) throws ApplicationException {
		if (question.getAnswerTypeAsEnum() != AnswerType.NUMBER) {
			throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
		}
		this.numberValue = numberValue;
	}
	
	public Object getValue() {
		AnswerType answerType = question.getAnswerTypeAsEnum();
		
		if (answerType == AnswerType.FREETEXT) {
			return getFreetextValue();
		}
		
		else if (answerType == AnswerType.NUMBER) {
			return getNumberValue();
		}
		
		else if (answerType == AnswerType.DATE) {
			return getDateValue();
		}
		
		else if (answerType == AnswerType.CHOICE) {
			return getChoiceValue();
		}
		
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		AnswerType answerType = question.getAnswerTypeAsEnum();
		
		if (answerType == AnswerType.FREETEXT) {
			return getFreetextValue();
		}
		
		else if (answerType == AnswerType.NUMBER) {
			return Double.toString(numberValue);
		}
		
		else if (answerType == AnswerType.DATE) {
			return DateUtils.makeDateAsSentFromBrowser(getDateValue());
		}
		
		else if (answerType == AnswerType.CHOICE) {
			return Integer.toString(getChoiceValue().getChoiceId());
		}
		
		else {
			return null;
		}
	}
	
	public void setStringValue(String value) throws ApplicationException {
		if (question == null || question.getAnswerTypeAsEnum() == null) {
			throw new ApplicationException(SurveyExceptionConstants.NO_ANSWER_TYPE_YET);
		}
		
		AnswerType answerType = question.getAnswerTypeAsEnum();
		if (answerType == AnswerType.FREETEXT) {
			setFreetextValue(value);
		// TODO: implement date AnswerType handling
		} else if (answerType == AnswerType.DATE) {
			setDateValue(DateUtils.getDate(value));
		} else if (answerType == AnswerType.NUMBER) {
			double numberValue = Double.parseDouble(value);
			setNumberValue(numberValue);
		} else if (answerType == AnswerType.DATE) {
			Date dateValue = DateUtils.getDateAsSentFromBrowser(value);
			setDateValue(dateValue);
		} else if (answerType == AnswerType.CHOICE) {
			int choiceId = Integer.parseInt(value);
			QuestionChoice choice = null;
			for (QuestionChoice qc : question.getChoices()) {
				if (qc.getChoiceId() == choiceId) {
					choice = qc;
					break;
				}
			}
			if (choice == null)
				throw new ApplicationException(SurveyExceptionConstants.NOT_CHOICE_TYPE);
			
			setChoiceValue(choice);
		}
	}
	
	public void setValue(Object value) throws ApplicationException {
		
		if (question == null || question.getAnswerTypeAsEnum() == null) {
			throw new ApplicationException(SurveyExceptionConstants.NO_ANSWER_TYPE_YET);
		}
		
		AnswerType answerType = question.getAnswerTypeAsEnum();
	
		try {
			if (answerType == AnswerType.FREETEXT) {
				setFreetextValue((String) value);
			}
			
			else if (answerType == AnswerType.DATE) {
				setDateValue((Date) value);
			}
			
			else if (answerType == AnswerType.NUMBER) {
				setNumberValue((Double) value);
			}
			
			else if (answerType == AnswerType.CHOICE) {
				setChoiceValue((QuestionChoice) value);
			}
		}
		catch (ClassCastException e) {
			throw new ApplicationException(SurveyExceptionConstants.WRONG_RESPONSE_TYPE);
		}
	}
	
	@Override
	public final boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		
		if (!(o instanceof SurveyResponse)) {
			return false;
		}
		
		SurveyResponse response = (SurveyResponse) o;
		return response.getResponseId() == responseId;
	}
	
	@Override
	public int hashCode() {
		return new Integer(responseId).hashCode();
	}

}

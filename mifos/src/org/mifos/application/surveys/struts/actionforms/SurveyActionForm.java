package org.mifos.application.surveys.struts.actionforms;

import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class SurveyActionForm extends BaseActionForm {
	
	private String surveyId;
	
	private String name;
	
	private String appliesTo;
	
	private String questionNum;
	
	private String newQuestion; // the id of a question to be associated with the new survey

	public String getNewQuestion() {
		return newQuestion;
	}

	public void setNewQuestion(String newQuestion) {
		this.newQuestion = newQuestion;
	}

	public String getAppliesTo() {
		return appliesTo;
	}
	
	public SurveyType getAppliesToAsEnum() {
		return SurveyType.fromString(appliesTo);
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	
	public int getSurveyIdValue() {
		return getIntegerValue(surveyId);
	}

	public String getQuestionNum() {
		return questionNum;
	}

	public void setQuestionNum(String questionNum) {
		this.questionNum = questionNum;
	}
	
	/*
	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String method = request.getParameter("method");
		if (method.equals("preview")) {
			errors = super.validate(mapping, request);
		}
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		return errors;
	}
	*/

}

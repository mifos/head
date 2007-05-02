package org.mifos.application.surveys.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class SurveyActionForm extends BaseActionForm {
	
	private String surveyId;

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	
	public int getSurveyIdValue() {
		return getIntegerValue(surveyId);
	}

}

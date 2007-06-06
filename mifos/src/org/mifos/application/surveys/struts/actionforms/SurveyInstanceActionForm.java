package org.mifos.application.surveys.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class SurveyInstanceActionForm extends BaseActionForm {
	
	private String surveyId;
	
	private String officerId;
	
	private String customerId;
	
	private String dateConducted;
	
	private String instanceStatus;

	public String getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getDateConducted() {
		return dateConducted;
	}

	public void setDateConducted(String dateConducted) {
		this.dateConducted = dateConducted;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOfficerId() {
		return officerId;
	}

	public void setOfficerId(String officerId) {
		this.officerId = officerId;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

}

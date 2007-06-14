package org.mifos.application.surveys.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

import java.util.List;

public class SurveyInstanceActionForm extends BaseActionForm {
	
	private String surveyId;
	
	private String officerId;
	
	private String officerName;
	
	private String customerId;
	
	private String dateConducted;
	
	private String instanceStatus;
	
	private List<String> responses;

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

	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}

	public String getOfficerName() {
		return officerName;
	}

	public String getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}

	public void setResponses(List<String> responses) {
		this.responses = responses;
	}

	public List<String> getResponses() {
		return responses;
	}

}

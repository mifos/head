package org.mifos.application.surveys.business;

import java.util.Date;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;

public class SurveyInstance extends PersistentObject {
	
	private int instanceId;
	
	private Survey survey;
	
	private ClientBO client;
	
	private PersonnelBO officer;
	
	private Date dateConducted;
	
	private int completedStatus;

	public int getCompletedStatus() {
		return completedStatus;
	}

	public void setCompletedStatus(int completedStatus) {
		this.completedStatus = completedStatus;
	}

	public Date getDateConducted() {
		return dateConducted;
	}

	public void setDateConducted(Date dateConducted) {
		this.dateConducted = dateConducted;
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public PersonnelBO getOfficer() {
		return officer;
	}

	public void setOfficer(PersonnelBO officer) {
		this.officer = officer;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public ClientBO getClient() {
		return client;
	}

	public void setClient(ClientBO client) {
		this.client = client;
	}

}

package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;

public class PersonnelMovementEntity extends PersistentObject {

	private Short personnelMovementId;

	private Short personnelId;

	private Short officeId;
	
	private Date startDate;

	private Date endDate;

	public PersonnelMovementEntity() {
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Short getPersonnelMovementId() {
		return personnelMovementId;
	}

	public void setPersonnelMovementId(Short personnelMovementId) {
		this.personnelMovementId = personnelMovementId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}

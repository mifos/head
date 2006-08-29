package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.business.PersistentObject;

public class PersonnelMovementEntity extends PersistentObject {

	private final Short personnelMovementId;

	private final PersonnelBO personnel;

	private OfficeBO office;

	private final Date startDate;

	private Date endDate;

	protected PersonnelMovementEntity() {
		super();
		this.personnelMovementId=null;
		this.personnel=null;
		this.startDate=null;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Short getPersonnelMovementId() {
		return personnelMovementId;
	}
	public Date getStartDate() {
		return startDate;
	}

	public PersonnelMovementEntity(PersonnelBO personnel, OfficeBO office) {
		super();
		this.personnel = personnel;
		this.office = office;
		this.personnelMovementId=null;
		this.startDate=new Date(System.currentTimeMillis());
		this.createdDate=new Date(System.currentTimeMillis());
		this.createdBy=personnel.getPersonnelId();
	}
}

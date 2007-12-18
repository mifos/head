package org.mifos.application.moratorium.business;


import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.moratorium.persistence.MoratoriumPersistence;
import org.mifos.application.moratorium.util.resources.MoratoriumConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PersistenceException;

public class MoratoriumBO extends BusinessObject{

	private int moratoriumId;
	
	private String appliedTo;
		
	private Date startDate;
		
	private Date endDate;
		
	private Date liftDate;
	
	private String morCreatedBy;
	
	private String notes;
	
	private String customerId;
	
	private String officeId;
	
	private CustomerBO customer;
	
	private String startDateString;
	
	private String endDateString;
	
	public MoratoriumBO() {
		super();
		//this.moratoriumId = null;
		this.appliedTo = null;
		this.startDate = null;
		this.endDate = null;		
		this.liftDate = null;
		this.morCreatedBy = null;
		this.notes = null;
		this.customerId=null;
		this.officeId=null;
		this.customer = null;
	}

	public String getAppliedTo() {
		return appliedTo;
	}

	public void setAppliedTo(String appliedTo) {
		this.appliedTo = appliedTo;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getLiftDate() {
		return liftDate;
	}

	public void setLiftDate(Date liftDate) {
		this.liftDate = liftDate;
	}

	public int getMoratoriumId() {
		return moratoriumId;
	}

	public void setMoratoriumId(int moratoriumId) {
		this.moratoriumId = moratoriumId;
	}

	public String getMorCreatedBy() {
		return morCreatedBy;
	}

	public void setMorCreatedBy(String morCreatedBy) {
		this.morCreatedBy = morCreatedBy;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void save() throws CustomerException {
		try 
		{
			new MoratoriumPersistence().createOrUpdate(this);			
		}
		catch (PersistenceException e) 
		{
			throw new CustomerException(MoratoriumConstants.CREATE_FAILED_EXCEPTION, e);
		}
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getEndDateString() {
		return endDateString;
	}

	public void setEndDateString(String endDateString) {
		this.endDateString = endDateString;
	}

	public String getStartDateString() {
		return startDateString;
	}

	public void setStartDateString(String startDateString) {
		this.startDateString = startDateString;
	}
}

package org.mifos.application.customer.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;

public class CustSearchActionForm extends BaseActionForm {
	
	private String loanOfficerId;
	
	
	private String officeId;
	
	
	private String searchString ;


	public String getLoanOfficerId() {
		return loanOfficerId;
	}


	public void setLoanOfficerId(String loanOfficerId) {
		this.loanOfficerId = loanOfficerId;
	}


	public String getOfficeId() {
		return officeId;
	}


	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}


	public String getSearchString() {
		return searchString;
	}


	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}

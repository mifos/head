package org.mifos.application.accounting.business;

import java.util.Date;

import org.mifos.framework.business.AbstractBusinessObject;

public class ProcessUpdateBo extends AbstractBusinessObject {
	private int processUpdateId;
	private String globalOfficeNumber;
	private Date lastUpdateDate;
	
	public int getProcessUpdateId() {
		return processUpdateId;
	}

	public void setProcessUpdateId(int processUpdateId) {
		this.processUpdateId = processUpdateId;
	}

	public String getGlobalOfficeNumber() {
		return globalOfficeNumber;
	}
	
	public void setGlobalOfficeNumber(String globalOfficeNumber) {
		this.globalOfficeNumber = globalOfficeNumber;
	}
	
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}

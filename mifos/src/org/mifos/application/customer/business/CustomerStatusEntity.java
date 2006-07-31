package org.mifos.application.customer.business;

import java.util.Set;

import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.framework.business.PersistentObject;

public class CustomerStatusEntity extends PersistentObject {

	private Short statusId;

	private Short levelId;

	private Integer lookUpId;

	private Set lookUpValueLocale;

	private String statusDescription;

	private Set statusFlag;

	public CustomerStatusEntity() {
	}

	public CustomerStatusEntity(CustomerStatus customerStatus) {
		this.statusId = customerStatus.getValue();
	}
	
	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setLookUpValueLocale(Set lookUpValueLocale) {

		this.lookUpValueLocale = lookUpValueLocale;
	}

	public Set getLookUpValueLocale() {
		return lookUpValueLocale;

	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	public Short getLevelId() {
		return levelId;
	}

	public void setStatusDescription(String description) {

		this.statusDescription = description;
	}

	public String getStatusDescription() {

		return statusDescription;
	}

	public Set getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(Set statusFlag) {
		this.statusFlag = statusFlag;
	}

}

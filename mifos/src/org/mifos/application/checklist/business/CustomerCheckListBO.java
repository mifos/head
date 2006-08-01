package org.mifos.application.checklist.business;

import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;


public class CustomerCheckListBO extends CheckListBO {	
	
	private static final long serialVersionUID = 18743658743L;

	public CustomerCheckListBO() {
	}	
	
	private CustomerLevelEntity customerLevelEntity ;
	private CustomerStatusEntity customerStatus;		

	public CustomerLevelEntity getCustomerLevelEntity() {
		return customerLevelEntity;
	}

	public void setCustomerLevelEntity(CustomerLevelEntity customerLevelEntity) {
		this.customerLevelEntity = customerLevelEntity;
	}

	public CustomerStatusEntity getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatusEntity customerStatus) {
		this.customerStatus = customerStatus;
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Checklist;
	}	
	
}
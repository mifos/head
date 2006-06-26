package org.mifos.application.checklist.business;

import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStateEntity;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;


public class CustomerCheckListBO extends CheckListBO {	
	
	private static final long serialVersionUID = 18743658743L;

	public CustomerCheckListBO() {
	}	
	
	private CustomerLevelEntity customerLevelEntity ;
	private CustomerStateEntity customerStateEntity;		

	public CustomerLevelEntity getCustomerLevelEntity() {
		return customerLevelEntity;
	}

	public void setCustomerLevelEntity(CustomerLevelEntity customerLevelEntity) {
		this.customerLevelEntity = customerLevelEntity;
	}

	public CustomerStateEntity getCustomerStateEntity() {
		return customerStateEntity;
	}

	public void setCustomerStateEntity(CustomerStateEntity customerStateEntity) {
		this.customerStateEntity = customerStateEntity;
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Checklist;
	}	
	
}
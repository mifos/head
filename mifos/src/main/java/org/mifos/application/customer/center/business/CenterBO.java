/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.customer.center.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;

public class CenterBO extends CustomerBO {

    /*
     * Injected Persistence classes
     * 
     * DO NOT ACCESS THESE MEMBERS DIRECTLY!  ALWAYS USE THE GETTER!
     * 
     * The Persistence classes below are used by this class
     * and can be injected via a setter for testing purposes.
     * In order for this mechanism to work correctly, the getter
     * must be used to access them because the getter will 
     * initialize the Persistence class if it has not been injected.
     * 
     * Long term these references to Persistence classes should 
     * probably be eliminated. 
     */
    private CenterPersistence centerPersistence = null;
    
	public CenterPersistence getCenterPersistence() {
	    if (null == centerPersistence) {
	        centerPersistence = new CenterPersistence();
	    }
        return centerPersistence;
    }

    public void setCenterPersistence(CenterPersistence centerPersistence) {
        this.centerPersistence = centerPersistence;
    }

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	public CenterBO(UserContext userContext, String displayName,
			Address address, List<CustomFieldView> customFields,
			List<FeeView> fees, String externalId, Date mfiJoiningDate,
			OfficeBO office, MeetingBO meeting, PersonnelBO loanOfficer, CustomerPersistence customerPersistence)
			throws CustomerException {
		super(userContext, displayName, CustomerLevel.CENTER,
				CustomerStatus.CENTER_ACTIVE, externalId, mfiJoiningDate,
				address, customFields, fees, null, office, null, meeting,
				loanOfficer);
		validateFields(displayName, meeting, loanOfficer, office);
		int count;
		try {
			count = customerPersistence.getCustomerCountForOffice(
					CustomerLevel.CENTER, office.getOfficeId());
		} catch (PersistenceException pe) {
			throw new CustomerException(pe);
		}
		this.setSearchId("1." + ++count);
		this.setCustomerActivationDate(this.getCreatedDate());
	}

	protected CenterBO() {
		super();
	}
	
	public static CenterBO createInstanceForTest(Integer customerId, CustomerLevelEntity customerLevel,
	        PersonnelBO formedByPersonnel, PersonnelBO personnel, String displayName) {
		return new CenterBO(customerId, customerLevel, formedByPersonnel, personnel, displayName);
	}
	
	private CenterBO(Integer customerId, CustomerLevelEntity customerLevel, PersonnelBO formedByPersonnel,
	        PersonnelBO personnel, String displayName) {
		super(customerId, customerLevel, formedByPersonnel, personnel, displayName);
	}
	
	@Override
	public boolean isActive() {
		return getStatus() == CustomerStatus.CENTER_ACTIVE;
	}

	@Override
	public void updateMeeting(MeetingBO meeting) throws CustomerException{
		logger.debug("In CenterBO::updateMeeting(), customerId: "
				+ getCustomerId());
		saveUpdatedMeeting(meeting);
		this.update();
	}
	
	private void validateFields(String displayName, MeetingBO meeting,
			PersonnelBO personnel, OfficeBO office) throws CustomerException {
		try {
			if (getCenterPersistence().isCenterExists(displayName)) {
				throw new CustomerException(
					CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, 
					new Object[] { displayName });
			}
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		} 
		validateMeeting(meeting);
		validateLO(personnel);
		validateOffice(office);
	}

	@Override
	protected void validateStatusChange(Short newStatusId)
	throws CustomerException {
		logger.debug("In CenterBO::validateStatusChange(), customerId: " + getCustomerId());
		if (newStatusId.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {
			if (isAnySavingsAccountOpen()) {
				throw new CustomerException(
						CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
			}
			if (getChildren(CustomerLevel.GROUP,
			        ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED).size() > 0) {
				throw new CustomerException(
						CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
						new Object[] { MessageLookup.getInstance().lookupLabel(
								ConfigurationConstants.GROUP,
								this.getUserContext()) });
			}
		} else if (newStatusId.equals(CustomerStatus.CENTER_ACTIVE.getValue())) {
			if (getPersonnel() == null
					|| getPersonnel().getPersonnelId() == null) {
				throw new CustomerException(
						ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
			}
		}
		logger.debug("In CenterBO::validateStatusChange(), successfully validated status, customerId: " + getCustomerId());
	}
	
	public void update(UserContext userContext, Short loanOfficerId, String externalId, 
	        Date mfiJoiningDate, Address address,  List<CustomFieldView> customFields, 
	        List<CustomerPositionView> customerPositions) throws Exception {
		validateFieldsForUpdate(loanOfficerId);
		setMfiJoiningDate(mfiJoiningDate);
		updateLoanOfficer(loanOfficerId);
		super.update(userContext, externalId, address, customFields, customerPositions);
	}
	
	protected void validateFieldsForUpdate(Short loanOfficerId)throws CustomerException{
		if (isActive())
			validateLO(loanOfficerId);
	}
	
	@Override
	protected boolean isActiveForFirstTime(Short oldStatus,
			Short newStatusId) {
		return false;
	}

	@Override
	public CustomerPerformanceHistory getPerformanceHistory() {
		return null;
	}
		
	@Override
	protected void saveUpdatedMeeting(MeetingBO meeting)
	    throws CustomerException{	
		MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
		super.saveUpdatedMeeting(meeting);
		deleteMeeting(newMeeting);
	}
}

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

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
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
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class CenterBO extends CustomerBO {

	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	public CenterBO(UserContext userContext, String displayName,
			Address address, List<CustomFieldView> customFields,
			List<FeeView> fees, String externalId, Date mfiJoiningDate,
			OfficeBO office, MeetingBO meeting, PersonnelBO loanOfficer)
			throws CustomerException {
		super(userContext, displayName, CustomerLevel.CENTER,
				CustomerStatus.CENTER_ACTIVE, externalId, mfiJoiningDate,
				address, customFields, fees, null, office, null, meeting,
				loanOfficer);
		validateFields(displayName, meeting, loanOfficer, office);
		int count;
		try {
			count = new CustomerPersistence().getCustomerCountForOffice(
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
	
	public static CenterBO createInstanceForTest(Integer customerId, CustomerLevelEntity customerLevel, PersonnelBO formedByPersonnel, PersonnelBO personnel, String displayName) {
		return new CenterBO(customerId, customerLevel, formedByPersonnel, personnel, displayName);
	}
	
	private CenterBO(Integer customerId, CustomerLevelEntity customerLevel, PersonnelBO formedByPersonnel, PersonnelBO personnel, String displayName) {
		super(customerId, customerLevel, formedByPersonnel, personnel, displayName);
	}
	
	@Override
	public boolean isActive() {
		return getStatus() == CustomerStatus.CENTER_ACTIVE;
	}

	@Override
	public void updateMeeting(MeetingBO meeting,
	        CustomerPersistence customerPersistence) throws CustomerException{
		logger.debug("In CenterBO::updateMeeting(), customerId: "
				+ getCustomerId());
		saveUpdatedMeeting(meeting, customerPersistence);
		this.update(customerPersistence);
	}
	
	private void validateFields(String displayName, MeetingBO meeting,
			PersonnelBO personnel, OfficeBO office) throws CustomerException {
		try {
			if (new CenterPersistence().isCenterExists(displayName)) {
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
	protected void validateStatusChange(Short newStatusId,
	        CustomerPersistence customerPersistence, OfficePersistence officePersistence)
	throws CustomerException {
		logger.debug("In CenterBO::validateStatusChange(), customerId: " + getCustomerId());
		if (newStatusId.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {
			if (isAnySavingsAccountOpen()) {
				throw new CustomerException(
						CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
			}
			if (getChildren(CustomerLevel.GROUP,
			        ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED,
			        customerPersistence).size() > 0) {
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
	        List<CustomerPositionView> customerPositions,
	        CustomerPersistence customerPersistence,
	        PersonnelPersistence personnelPersistence) throws Exception {
		validateFieldsForUpdate(loanOfficerId);
		setMfiJoiningDate(mfiJoiningDate);
		updateLoanOfficer(loanOfficerId, customerPersistence, personnelPersistence);
		super.update(userContext, externalId, address, customFields, customerPositions, customerPersistence);
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
	protected void saveUpdatedMeeting(MeetingBO meeting, CustomerPersistence customerPersistence)
	    throws CustomerException{	
		MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
		super.saveUpdatedMeeting(meeting, customerPersistence);
		deleteMeeting(newMeeting, customerPersistence);
	}
}

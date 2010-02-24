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

package org.mifos.customers.center.business;

import java.util.Date;
import java.util.List;

import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPerformanceHistory;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;

public class CenterBO extends CustomerBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
    
    /*
     * Injected Persistence classes
     * 
     * DO NOT ACCESS THESE MEMBERS DIRECTLY! ALWAYS USE THE GETTER!
     * 
     * The Persistence classes below are used by this class and can be injected
     * via a setter for testing purposes. In order for this mechanism to work
     * correctly, the getter must be used to access them because the getter will
     * initialize the Persistence class if it has not been injected.
     * 
     * Long term these references to Persistence classes should probably be
     * eliminated.
     */
    private CenterPersistence centerPersistence = null;

    public CenterPersistence getCenterPersistence() {
        if (null == centerPersistence) {
            centerPersistence = new CenterPersistence();
        }
        return centerPersistence;
    }

    public void setCenterPersistence(final CenterPersistence centerPersistence) {
        this.centerPersistence = centerPersistence;
    }

    /**
     * default constructor for hibernate
     */
    protected CenterBO() {
        super();
    }

    /**
     * TODO - keithw - work in progress
     * 
     * minimal constructor for builder
     */
    public CenterBO(final CustomerLevel customerLevel, final CustomerStatus customerStatus, final String name,
            final OfficeBO office, final PersonnelBO loanOfficer, final CustomerMeetingEntity customerMeeting,
            final String searchId) {
        super(customerLevel, customerStatus, name, office, loanOfficer, customerMeeting, null);
        this.setSearchId(searchId);
        this.setCustomerActivationDate(this.getCreatedDate());
    }

    public CenterBO(final UserContext userContext, final String displayName, final Address address, final List<CustomFieldView> customFields,
            final List<FeeView> fees, final String externalId, final Date mfiJoiningDate, final OfficeBO office, final MeetingBO meeting,
            final PersonnelBO loanOfficer, final CustomerPersistence customerPersistence) throws CustomerException {
        super(userContext, displayName, CustomerLevel.CENTER, CustomerStatus.CENTER_ACTIVE, externalId, mfiJoiningDate,
                address, customFields, fees, null, office, null, meeting, loanOfficer);
        validateFields(displayName, meeting, loanOfficer, office);
        int count;
        try {
            count = customerPersistence.getCustomerCountForOffice(CustomerLevel.CENTER, office.getOfficeId());
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        }
        this.setSearchId("1." + ++count);
        this.setCustomerActivationDate(this.getCreatedDate());
    }

    public static CenterBO createInstanceForTest(final Integer customerId, final CustomerLevelEntity customerLevel,
            final PersonnelBO formedByPersonnel, final PersonnelBO personnel, final String displayName) {
        return new CenterBO(customerId, customerLevel, formedByPersonnel, personnel, displayName);
    }

    private CenterBO(final Integer customerId, final CustomerLevelEntity customerLevel, final PersonnelBO formedByPersonnel,
            final PersonnelBO personnel, final String displayName) {
        super(customerId, customerLevel, formedByPersonnel, personnel, displayName);
    }

    @Override
    public boolean isActive() {
        return getStatus() == CustomerStatus.CENTER_ACTIVE;
    }

    @Override
    public void updateMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In CenterBO::updateMeeting(), customerId: " + getCustomerId());
        saveUpdatedMeeting(meeting);
        this.update();
    }

    private void validateFields(final String displayName, final MeetingBO meeting, final PersonnelBO personnel, final OfficeBO office)
            throws CustomerException {
        try {
            if (getCenterPersistence().isCenterExists(displayName)) {
                throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, new Object[] { displayName });
            }
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
        validateMeeting(meeting);
        validateLO(personnel);
        validateOffice(office);
    }

    @Override
    protected void validateStatusChange(final Short newStatusId) throws CustomerException {
        logger.debug("In CenterBO::validateStatusChange(), customerId: " + getCustomerId());
        if (newStatusId.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {
            if (isAnySavingsAccountOpen()) {
                throw new CustomerException(CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
            }
            if (getChildren(CustomerLevel.GROUP, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED).size() > 0) {
                throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
                        new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
                                this.getUserContext()) });
            }
        } else if (newStatusId.equals(CustomerStatus.CENTER_ACTIVE.getValue())) {
            if (getPersonnel() == null || getPersonnel().getPersonnelId() == null) {
                throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
            }
        }
        logger.debug("In CenterBO::validateStatusChange(), successfully validated status, customerId: "
                + getCustomerId());
    }

    public void update(final UserContext userContext, final Short loanOfficerId, final String externalId, final Date mfiJoiningDate,
            final Address address, final List<CustomFieldView> customFields, final List<CustomerPositionView> customerPositions)
            throws Exception {
        validateFieldsForUpdate(loanOfficerId);
        setMfiJoiningDate(mfiJoiningDate);
        updateLoanOfficer(loanOfficerId);
        super.update(userContext, externalId, address, customFields, customerPositions);
    }

    protected void validateFieldsForUpdate(final Short loanOfficerId) throws CustomerException {
        if (isActive()) {
            validateLO(loanOfficerId);
        }
    }

    @Override
    protected boolean isActiveForFirstTime(final Short oldStatus, final Short newStatusId) {
        return false;
    }

    @Override
    public CustomerPerformanceHistory getPerformanceHistory() {
        return null;
    }

    @Override
    protected void saveUpdatedMeeting(final MeetingBO meeting) throws CustomerException {
        MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
        super.saveUpdatedMeeting(meeting);
        deleteMeeting(newMeeting);
    }
}

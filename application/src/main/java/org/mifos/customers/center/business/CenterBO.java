/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerPerformanceHistory;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;

public class CenterBO extends CustomerBO {

    public static CenterBO createNew(UserContext userContext, String centerName, DateTime mfiJoiningDate,
            MeetingBO meeting, PersonnelBO loanOfficer, OfficeBO centerOffice, int numberOfCustomersInOfficeAlready,
            List<CustomerCustomFieldEntity> customerCustomFields, Address centerAddress, String externalId, DateTime activationDate) {

        PersonnelBO formedBy = null;
        CenterBO center = new CenterBO(userContext, centerName, mfiJoiningDate, meeting, loanOfficer, centerOffice,
                numberOfCustomersInOfficeAlready, CustomerStatus.CENTER_ACTIVE, formedBy, activationDate);

        center.setExternalId(externalId);
        center.updateAddress(centerAddress);

        List<CustomerCustomFieldEntity> populatedWithCustomerReference = CustomerCustomFieldEntity.fromCustomerCustomFieldEntity(customerCustomFields, center);
        for (CustomerCustomFieldEntity customerCustomFieldEntity : populatedWithCustomerReference) {
            center.addCustomField(customerCustomFieldEntity);
        }

        return center;
    }

    /**
     * default constructor for hibernate
     */
    protected CenterBO() {
        super();
    }

    /**
     * minimal legal constructor for {@link CenterBO}.
     */
    public CenterBO(UserContext userContext, String centerName, DateTime mfiJoiningDate, MeetingBO meeting,
            PersonnelBO loanOfficer, OfficeBO office, int numberOfCustomersInOfficeAlready,
            CustomerStatus customerStatus, PersonnelBO formedBy, DateTime activationDate) {
        super(userContext, centerName, CustomerLevel.CENTER, customerStatus, mfiJoiningDate, office, meeting, loanOfficer, formedBy);

        int searchIdCustomerValue = numberOfCustomersInOfficeAlready + 1;
        this.setSearchId("1." + searchIdCustomerValue);
        this.setCustomerActivationDate(activationDate.toDate());
    }

    /**
     * @deprecated - use static factory
     */
    @Deprecated
    public CenterBO(final UserContext userContext, final String displayName, final Address address, final List<CustomFieldDto> customFields,
            final List<FeeDto> fees, final String externalId, final Date mfiJoiningDate, final OfficeBO office, final MeetingBO meeting,
            final PersonnelBO loanOfficer, final CustomerPersistence customerPersistence) throws CustomerException {
        super(userContext, displayName, CustomerLevel.CENTER, CustomerStatus.CENTER_ACTIVE, externalId, mfiJoiningDate,
                address, customFields, fees, null, office, null, meeting, loanOfficer);
        int count;
        try {
            count = customerPersistence.getCustomerCountForOffice(CustomerLevel.CENTER, office.getOfficeId());
        } catch (PersistenceException pe) {
            throw new CustomerException(pe);
        }
        this.setSearchId("1." + ++count);
        this.setCustomerActivationDate(this.getCreatedDate());
    }

    @Override
    public boolean isActive() {
        return getStatus() == CustomerStatus.CENTER_ACTIVE;
    }

    @Override
    public boolean isActiveForFirstTime(@SuppressWarnings("unused") final Short oldStatus, @SuppressWarnings("unused") final Short newStatusId) {
        return false;
    }

    @Override
    public CustomerPerformanceHistory getPerformanceHistory() {
        return null;
    }

    public void validateChangeToInActive() throws CustomerException {

        if (this.isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
        }
    }

    @Override
    public void validate() throws CustomerException {
        super.validate();

        if (this.getMfiJoiningDate() == null) {
            throw new CustomerException(CustomerConstants.MFI_JOINING_DATE_MANDATORY);
        }
    }

    public void validateMeetingAndFees(List<AccountFeesEntity> accountFees) throws CustomerException {

        if (this.getCustomerMeeting() == null || this.getCustomerMeetingValue() == null) {

            if (accountFees.size() > 0) {
                throw new CustomerException(CustomerConstants.MEETING_REQUIRED_EXCEPTION);
            }

            throw new CustomerException(CustomerConstants.ERRORS_SPECIFY_MEETING);
        }

        for (AccountFeesEntity accountFee : accountFees) {

            if (accountFee.getFees().isPeriodic()) {
                MeetingBO feeMeeting = accountFee.getFees().getFeeFrequency().getFeeMeetingFrequency();
                if (!feeMeeting.hasSameRecurrenceAs(this.getCustomerMeetingValue())) {
                    throw new CustomerException(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH);
                }

                FeeBO fee = accountFee.getFees();

                if (AccountFeesEntity.isPeriodicFeeDuplicated(accountFees, fee)) {
                    throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_PERIODIC_FEE);
                }
            }
        }
    }

    @Override
    public void updateCustomerStatus(CustomerStatus newStatus, CustomerNoteEntity customerNote,
            CustomerStatusFlagEntity customerStatusFlagEntity) {
        clearCustomerFlagsIfApplicable(getStatus(), newStatus);
        super.updateCustomerStatus(newStatus, customerNote, customerStatusFlagEntity);
    }
}
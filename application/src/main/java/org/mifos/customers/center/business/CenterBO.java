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
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerPerformanceHistory;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;

public class CenterBO extends CustomerBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);

    public static CenterBO createNew(UserContext userContext, String centerName, DateTime mfiJoiningDate,
            MeetingBO meeting, PersonnelBO loanOfficer, OfficeBO centerOffice, int numberOfCustomersInOfficeAlready,
            List<CustomerCustomFieldEntity> customerCustomFields, Address centerAddress, String externalId) {

        PersonnelBO formedBy = null;
        CenterBO center = new CenterBO(userContext, centerName, mfiJoiningDate, meeting, loanOfficer, centerOffice,
                numberOfCustomersInOfficeAlready, CustomerStatus.CENTER_ACTIVE, formedBy);

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
            CustomerStatus customerStatus, PersonnelBO formedBy) {
        super(userContext, centerName, CustomerLevel.CENTER, customerStatus, mfiJoiningDate, office, meeting, loanOfficer, formedBy);

        int searchIdCustomerValue = numberOfCustomersInOfficeAlready + 1;
        this.setSearchId("1." + searchIdCustomerValue);
        this.setCustomerActivationDate(this.getCreatedDate());
    }

    /**
     * @deprecated - use static factory {@link CenterBO#createNew(UserContext, String, DateTime, MeetingBO, PersonnelBO, OfficeBO, int, List, Address, String)}.
     */
    @Deprecated
    public CenterBO(final UserContext userContext, final String displayName, final Address address, final List<CustomFieldView> customFields,
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

    /**
     * @deprecated - pull out of domain model up towards service level.
     */
    @Deprecated
    @Override
    public void updateMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In CenterBO::updateMeeting(), customerId: " + getCustomerId());
        saveUpdatedMeeting(meeting);
        this.update();
    }

    @Override
    public boolean isActiveForFirstTime(@SuppressWarnings("unused") final Short oldStatus, @SuppressWarnings("unused") final Short newStatusId) {
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

    public void validateChangeToInActive() throws CustomerException {

        if (this.isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
        }
    }
}

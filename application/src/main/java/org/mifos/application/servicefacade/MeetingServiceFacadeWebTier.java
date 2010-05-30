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

package org.mifos.application.servicefacade;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.service.MeetingBusinessService;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.security.util.UserContext;

public class MeetingServiceFacadeWebTier implements MeetingServiceFacade {

    private final CustomerDao customerDao;

    public MeetingServiceFacadeWebTier(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public void updateCustomerMeeting(MeetingUpdateRequest meetingUpdateRequest, UserContext userContext) throws ApplicationException {
        CustomerBO customer = customerDao.findCustomerById(meetingUpdateRequest.getCustomerId());

        if (customer.getPersonnel() != null) {
            new MeetingBusinessService().checkPermissionForEditMeetingSchedule(customer.getLevel(), userContext, customer.getOffice().getOfficeId(),
                    customer.getPersonnel().getPersonnelId());
        } else {
            new MeetingBusinessService().checkPermissionForEditMeetingSchedule(customer.getLevel(), userContext, customer.getOffice().getOfficeId(), userContext.getId());
        }

        try {
            MeetingBO meeting = createMeeting(meetingUpdateRequest);
            customer.updateMeeting(meeting);
        } catch (MeetingException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private MeetingBO createMeeting(MeetingUpdateRequest form) throws MeetingException {
        MeetingBO meeting = null;
        Date startDate = new DateTimeService().getCurrentJavaDateTime();
        if (form.getRecurrenceType().equals(RecurrenceType.WEEKLY)) {
            meeting = new MeetingBO(form.getWeekDay(), form.getRecursEvery(), startDate, MeetingType.CUSTOMER_MEETING, form.getMeetingPlace());
        } else if (form.getRecurrenceType().equals(RecurrenceType.MONTHLY) && form.getDayOfMonth() != null) {
            meeting = new MeetingBO(form.getDayOfMonth(), form.getRecursEvery(), startDate, MeetingType.CUSTOMER_MEETING, form.getMeetingPlace());
        } else {
            meeting = new MeetingBO(form.getMonthWeek(), form.getRankOfDay(), form.getRecursEvery(), startDate, MeetingType.CUSTOMER_MEETING, form.getMeetingPlace());
        }
        return meeting;
    }
}
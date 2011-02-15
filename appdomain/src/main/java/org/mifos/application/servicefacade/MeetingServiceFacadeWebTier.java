/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class MeetingServiceFacadeWebTier implements MeetingServiceFacade {

    private final CustomerService customerService;
    private final CustomerDao customerDao;
    private MeetingFactory meetingFactory = new MeetingFactory();
    private UserContextFactory userContextFactory = new UserContextFactory();

    @Autowired
    public MeetingServiceFacadeWebTier(CustomerService customerService, CustomerDao customerDao) {
        this.customerService = customerService;
        this.customerDao = customerDao;
    }

    @Override
    public void updateCustomerMeeting(MeetingDto meetingUpdateRequest, Integer customerId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = userContextFactory.create(user);

        MeetingBO meeting = meetingFactory.create(meetingUpdateRequest);
        meeting.updateDetails(userContext);

        CustomerBO customer = this.customerDao.findCustomerById(customerId);
        customer.updateDetails(userContext);

        customerService.updateCustomerMeetingSchedule(meeting, customer);
    }

    public void setMeetingFactory(MeetingFactory meetingFactory) {
        this.meetingFactory = meetingFactory;
    }

    public void setUserContextFactory(UserContextFactory userContextFactory) {
        this.userContextFactory = userContextFactory;
    }
}
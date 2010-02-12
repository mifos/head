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

package org.mifos.framework.util.helpers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.TestUtils;

public class MockMifosTestObjects {

    public static CenterBO createMockCenter(String centerName, MeetingBO meeting) throws CustomerException {
        List<FeeView> fees = new ArrayList<FeeView>();
        OfficeBO officeBOMock = createMock(OfficeBO.class);
        expect(officeBOMock.getOfficeId()).andReturn(Short.valueOf("1")).anyTimes();
        replay(officeBOMock);
        PersonnelBO personnelBOMock = createMock(PersonnelBO.class);
        expect(personnelBOMock.getPersonnelId()).andReturn(Short.valueOf("1")).anyTimes();
        replay(personnelBOMock);
        return new CenterBO(TestUtils.makeUserWithLocales(), centerName, null, null, fees, null, null, officeBOMock,
                meeting, personnelBOMock, new CustomerPersistence());

    }

    public static GroupBO createMockGroup(String groupName, CustomerBO parentCustomer) throws CustomerException {
        List<FeeView> fees = new ArrayList<FeeView>();
        PersonnelBO personnelBOMock = createMock(PersonnelBO.class);
        expect(personnelBOMock.getPersonnelId()).andReturn(Short.valueOf("1")).anyTimes();
        replay(personnelBOMock);
        GroupPersistence groupPersistence = createMock(GroupPersistence.class);
        OfficePersistence officePersistence = createMock(OfficePersistence.class);
        // TODO: Is CLIENT_ACTIVE right or should this be GROUP_ACTIVE?
        return new GroupBO(TestUtils.makeUserWithLocales(), groupName, CustomerStatus.CLIENT_ACTIVE, null, false, null,
                null, TestObjectFactory.getCustomFields(), fees, personnelBOMock, parentCustomer, groupPersistence,
                officePersistence);

    }

    public static ClientBO createMockClient(String customerName, CustomerBO parentCustomer) throws CustomerException {
        OfficeBO officeBOMock = createMock(OfficeBO.class);
        expect(officeBOMock.getOfficeId()).andReturn(Short.valueOf("1")).anyTimes();
        replay(officeBOMock);
        PersonnelBO personnelBOMock = createMock(PersonnelBO.class);
        expect(personnelBOMock.getPersonnelId()).andReturn(Short.valueOf("1")).anyTimes();
        replay(personnelBOMock);
        ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        ClientNameDetailView clientNameDetailView = TestObjectFactory.clientNameView(NameType.MAYBE_CLIENT,
                customerName);
        ClientNameDetailView spouseNameDetailView = TestObjectFactory.clientNameView(NameType.SPOUSE, customerName);
        Date dateOfBirth = new Date(1222333444000L);
        CustomerStatus status = CustomerStatus.CLIENT_ACTIVE;
        return new ClientBO(TestUtils.makeUserWithLocales(), customerName, status, null, null, null, null,
                TestObjectFactory.getFeesWithMakeUser(), null, personnelBOMock, officeBOMock, parentCustomer,
                dateOfBirth, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailView, spouseNameDetailView,
                clientDetailView, null);

    }

    public static MeetingBO createMeeting() throws MeetingException {
        return new MeetingBO(RecurrenceType.WEEKLY, TestObjectFactory.EVERY_WEEK, new java.util.Date(),
                MeetingType.CUSTOMER_MEETING);
    }
    
}

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

package org.mifos.application.customer.client.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.DateTimeService;

public class ClientBoTest extends TestCase {

    public void setUp() throws Exception {
        MifosLogManager.configureLogging();
    }

    public void testAddClientAttendance() {
        ClientBO client = new ClientBO();
        Assert.assertEquals("Expecting no attendance entries on a new object", 0, client.getClientAttendances().size());
        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.addClientAttendance(getClientAttendance(meetingDate.toDate()));
        Assert.assertEquals("Expecting no attendance entries on a new object", 1, client.getClientAttendances().size());
    }

    public void testGetClientAttendanceForMeeting() {
        ClientBO client = new ClientBO();
        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.addClientAttendance(getClientAttendance(meetingDate.toDate()));
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
    }

    private ClientBO createUpdatableClient() throws PersistenceException {
        ClientBO client = new ClientBO();
        CustomerPersistence customerPersistenceMock = createMock(CustomerPersistence.class);
        expect(customerPersistenceMock.createOrUpdate(client)).andReturn(client).anyTimes();
        replay(customerPersistenceMock);
        client.setCustomerPersistence(customerPersistenceMock);

        return client;
    }

    public void testHandleAttendance() throws Exception {
        ClientBO client = createUpdatableClient();

        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.handleAttendance(meetingDate.toDate(), AttendanceType.PRESENT);
        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 1);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
        client.handleAttendance(meetingDate.toDate(), AttendanceType.ABSENT);
        Assert.assertEquals("The size of customer attendance is : ", 1, client.getClientAttendances().size());
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.ABSENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
    }

    public void testHandleAttendanceForDifferentDates() throws Exception {
        ClientBO client = createUpdatableClient();

        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.handleAttendance(meetingDate.toDate(), AttendanceType.PRESENT);

        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 1);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());

        DateMidnight offSetDate = getDateOffset(1);
        client.handleAttendance(new java.sql.Date(offSetDate.getMillis()), AttendanceType.ABSENT);

        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 2);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.ABSENT, client
                .getClientAttendanceForMeeting(offSetDate.toDate()).getAttendanceAsEnum());
    }

    private DateMidnight getDateOffset(int numberOfDays) {
        DateMidnight currentDate = new DateTimeService().getCurrentDateMidnight();
        currentDate = currentDate.minusDays(numberOfDays);
        return currentDate;
    }

    private ClientAttendanceBO getClientAttendance(java.util.Date meetingDate) {
        ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(AttendanceType.PRESENT);
        clientAttendance.setMeetingDate(meetingDate);
        return clientAttendance;
    }
}

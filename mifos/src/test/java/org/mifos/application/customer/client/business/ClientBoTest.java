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

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.DateUtils;

public class ClientBoTest extends TestCase {
	public ClientBoTest() throws SystemException, ApplicationException {
        super();
    }

 	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DatabaseSetup.configureLogging();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testAddClientAttendance() {
	    ClientBO client = new ClientBO();
	    assertEquals("Expecting no attendance entries on a new object", 0,
                client.getClientAttendances().size());
	    java.util.Date meetingDate = new DateTimeService().getCurrentJavaDateTime();
        client.addClientAttendance(getClientAttendance(meetingDate));
        assertEquals("Expecting no attendance entries on a new object", 1,
                client.getClientAttendances().size());
	}

	public void testGetClientAttendanceForMeeting() {
        ClientBO client = new ClientBO();
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.addClientAttendance(getClientAttendance(meetingDate));
		assertEquals("The value of customer attendance for the meeting : ",
				AttendanceType.PRESENT,
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendanceAsEnum());
	}

	public void testHandleAttendance() throws Exception {
        ClientBO client = new ClientBO();
        CustomerPersistence customerPersistenceMock = createMock(CustomerPersistence.class);
        expect(customerPersistenceMock.createOrUpdate(client)).andReturn(client).anyTimes();
        replay(customerPersistenceMock);

        client.setCustomerPersistence(customerPersistenceMock);
        
		java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
		client.handleAttendance(meetingDate, AttendanceType.PRESENT);
		assertEquals("The size of customer attendance is : ", client
				.getClientAttendances().size(), 1);
		assertEquals("The value of customer attendance for the meeting : ",
				AttendanceType.PRESENT,
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendanceAsEnum());
		client.handleAttendance(meetingDate, AttendanceType.ABSENT);
		assertEquals("The size of customer attendance is : ",
				1, client.getClientAttendances().size());
		assertEquals("The value of customer attendance for the meeting : ",
				AttendanceType.ABSENT,
				client.getClientAttendanceForMeeting(meetingDate)
						.getAttendanceAsEnum());
	}

    public void testHandleAttendanceForDifferentDates() throws Exception {
        ClientBO client = new ClientBO();
        CustomerPersistence customerPersistenceMock = createMock(CustomerPersistence.class);
        expect(customerPersistenceMock.createOrUpdate(client)).andReturn(client).anyTimes();
        replay(customerPersistenceMock);

        client.setCustomerPersistence(customerPersistenceMock);
        
        java.util.Date meetingDate = DateUtils.getCurrentDateWithoutTimeStamp();
        client.handleAttendance(meetingDate, AttendanceType.PRESENT);

        assertEquals("The size of customer attendance is : ", client
                .getClientAttendances().size(), 1);
        assertEquals("The value of customer attendance for the meeting : ",
                AttendanceType.PRESENT,
                client.getClientAttendanceForMeeting(meetingDate)
                        .getAttendanceAsEnum());

        Date offSetDate = getDateOffset(1);
        client.handleAttendance(offSetDate, AttendanceType.ABSENT);

        assertEquals("The size of customer attendance is : ", client
                .getClientAttendances().size(), 2);
        assertEquals("The value of customer attendance for the meeting : ",
                AttendanceType.PRESENT,
                client.getClientAttendanceForMeeting(meetingDate)
                        .getAttendanceAsEnum());
        assertEquals("The value of customer attendance for the meeting : ",
                AttendanceType.ABSENT,
                client.getClientAttendanceForMeeting(offSetDate)
                        .getAttendanceAsEnum());

    }

	
    private Date getDateOffset(int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month,
                (day - numberOfDays));
        return new Date(currentDateCalendar.getTimeInMillis());
    }
    
	private ClientAttendanceBO getClientAttendance(java.util.Date meetingDate) {
		ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
		clientAttendance.setAttendance(AttendanceType.PRESENT);
		clientAttendance.setMeetingDate(meetingDate);
		return clientAttendance;
	}

    }

	
	
	

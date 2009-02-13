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

package org.mifos.application.customer.client.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.joda.time.LocalDate;
import org.mifos.application.customer.client.business.AttendanceType;

public class StandardClientServiceTest extends TestCase {

    private int clientId1 = 123456;
    private AttendanceType client1Attendance = AttendanceType.PRESENT;
    private int clientId2 = 123457;
    private AttendanceType client2Attendance = AttendanceType.ABSENT;
    private LocalDate meetingDate = new LocalDate("2008-02-14");
	private StandardClientService clientService;

    @Override
	protected void setUp() throws Exception {
		super.setUp();
        InMemoryClientAttendanceDao clientAttendanceDao = new InMemoryClientAttendanceDao();
        clientAttendanceDao.setAttendance(clientId1, meetingDate, client1Attendance);
        clientAttendanceDao.setAttendance(clientId2, meetingDate, client2Attendance);
        clientService = new StandardClientService();
        clientService.setClientAttendanceDao(clientAttendanceDao);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetClientAttendanceNoIds() throws Exception {
	    clientService = new StandardClientService();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
		assertEquals(0, clientAttendance.size());
	}

    public void testGetClientAttendanceOneId() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(clientId1, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        assertEquals(1, clientAttendance.size());
        ClientAttendanceDto clientAttendanceDto = clientAttendance.get(clientId1);
        assertNotNull(clientAttendanceDto);
        assertEquals(clientId1, (int) clientAttendanceDto.getClientId());
        assertEquals(AttendanceType.PRESENT, clientAttendanceDto.getAttendance());
    }


    public void testGetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(clientId1, meetingDate, null));
        clientAttendanceDtos.add(getClientAttendanceDto(clientId2, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        assertEquals(2, clientAttendance.size());
        assertNotNull(clientAttendance.get(clientId1));
        assertEquals(clientId1, (int) clientAttendance.get(clientId1).getClientId());
        assertEquals(client1Attendance, clientAttendance.get(clientId1).getAttendance());
        assertNotNull(clientAttendance.get(clientId2));
        assertEquals(clientId2, (int) clientAttendance.get(clientId2).getClientId());
        assertEquals(client2Attendance, clientAttendance.get(clientId2).getAttendance());
    }

    private ClientAttendanceDto getClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }

}

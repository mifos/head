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
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.joda.time.LocalDate;
import org.mifos.application.customer.client.business.AttendanceType;

public class StandardClientServiceTest extends TestCase {

    private static final Short OFFICE1_ID = 1;
    private int client1Id = 123456;
    private AttendanceType client1Attendance = AttendanceType.PRESENT;
    private int client2Id = 123457;
    private AttendanceType client2Attendance = AttendanceType.ABSENT;
    private LocalDate meetingDate = new LocalDate("2008-02-14");
	private StandardClientService clientService;
    private InMemoryClientAttendanceDao clientAttendanceDao;

    @Override
	protected void setUp() throws Exception {
		super.setUp();
        clientAttendanceDao = new InMemoryClientAttendanceDao();
        clientAttendanceDao.setAttendance(client1Id, meetingDate, client1Attendance);
        clientAttendanceDao.setAttendance(client2Id, meetingDate, client2Attendance);
        clientService = new StandardClientService();
        clientService.setClientAttendanceDao(clientAttendanceDao);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetClientAttendanceNoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
		assertEquals(0, clientAttendance.size());
	}

    public void testGetClientAttendanceOneId() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        assertEquals(1, clientAttendance.size());
        ClientAttendanceDto clientAttendanceDto = clientAttendance.get(client1Id);
        assertNotNull(clientAttendanceDto);
        assertEquals(client1Id, (int) clientAttendanceDto.getClientId());
        assertEquals(AttendanceType.PRESENT, clientAttendanceDto.getAttendance());
    }


    public void testGetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, null));
        clientAttendanceDtos.add(getClientAttendanceDto(client2Id, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        assertEquals(2, clientAttendance.size());
        assertNotNull(clientAttendance.get(client1Id));
        assertEquals(client1Id, (int) clientAttendance.get(client1Id).getClientId());
        assertEquals(client1Attendance, clientAttendance.get(client1Id).getAttendance());
        assertNotNull(clientAttendance.get(client2Id));
        assertEquals(client2Id, (int) clientAttendance.get(client2Id).getClientId());
        assertEquals(client2Attendance, clientAttendance.get(client2Id).getAttendance());
    }

    public void testGetClientAttendanceListTwoIds() throws Exception {
        clientAttendanceDao.setAttendance(client1Id, meetingDate, client1Attendance, OFFICE1_ID);
        clientAttendanceDao.setAttendance(client2Id, meetingDate, client2Attendance, OFFICE1_ID);
        List<ClientAttendanceDto> clientAttendance = clientService.getClientAttendanceList(meetingDate.toDateMidnight().toDate(), OFFICE1_ID);
        assertEquals(2, clientAttendance.size());
        assertNotNull(clientAttendance.get(0));
        assertEquals(client1Id, (int) clientAttendance.get(0).getClientId());
        assertEquals(client1Attendance, clientAttendance.get(0).getAttendance());
        assertNotNull(clientAttendance.get(1));
        assertEquals(client2Id, (int) clientAttendance.get(1).getClientId());
        assertEquals(client2Attendance, clientAttendance.get(1).getAttendance());
    }
    
    
    public void testSetClientAttendanceNoIds() throws Exception {
        int expectedNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientService.setClientAttendance(clientAttendanceDtos);
        int actualNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        Assert.assertEquals(expectedNumberOfRecords, actualNumberOfRecords);
    }

    public void testSetClientAttendanceOneId() throws Exception {
        int expectedNumberOfRecords = clientAttendanceDao.getNumberOfRecords() + 1;
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        int client3Id = 55555;
        AttendanceType client3Attendance = AttendanceType.LATE;
        clientAttendanceDtos.add(getClientAttendanceDto(client3Id, meetingDate, client3Attendance));
        clientService.setClientAttendance(clientAttendanceDtos);
        int actualNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        Assert.assertEquals(expectedNumberOfRecords, actualNumberOfRecords);
        Map<Integer, ClientAttendanceDto> actualClientAttendanceDtos = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(client3Attendance, actualClientAttendanceDtos.get(client3Id).getAttendance());
    }

    public void testSetClientAttendanceTwoIds() throws Exception {
        int expectedNumberOfRecords = clientAttendanceDao.getNumberOfRecords() + 2;
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        int client3Id = 55555;
        AttendanceType client3Attendance = AttendanceType.LATE;
        LocalDate meetingDate3 = new LocalDate("2008-02-15");
        int client4Id = 55556;
        AttendanceType client4Attendance = AttendanceType.ABSENT;
        LocalDate meetingDate4 = new LocalDate("2008-02-16");
        
        clientAttendanceDtos.add(getClientAttendanceDto(client3Id, meetingDate3, client3Attendance));
        clientAttendanceDtos.add(getClientAttendanceDto(client4Id, meetingDate4, client4Attendance));
        clientService.setClientAttendance(clientAttendanceDtos);
        int actualNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        Assert.assertEquals(expectedNumberOfRecords, actualNumberOfRecords);
        Map<Integer, ClientAttendanceDto> actualClientAttendanceDtos = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(client3Attendance, actualClientAttendanceDtos.get(client3Id).getAttendance());
        Assert.assertEquals(meetingDate3, actualClientAttendanceDtos.get(client3Id).getMeetingDate());
        Assert.assertEquals(client4Attendance, actualClientAttendanceDtos.get(client4Id).getAttendance());
        Assert.assertEquals(meetingDate4, actualClientAttendanceDtos.get(client4Id).getMeetingDate());
    }

    public void testSetClientAttendanceReplaceOneId() throws Exception {
        int expectedNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        AttendanceType expectedAttendance = AttendanceType.APPROVED_LEAVE;
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, expectedAttendance));
        clientService.setClientAttendance(clientAttendanceDtos);
        int actualNumberOfRecords = clientAttendanceDao.getNumberOfRecords();
        Assert.assertEquals(expectedNumberOfRecords, actualNumberOfRecords);
        Map<Integer, ClientAttendanceDto> actualClientAttendanceDtos = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(expectedAttendance, actualClientAttendanceDtos.get(client1Id).getAttendance());
    }

    private ClientAttendanceDto getClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }



}

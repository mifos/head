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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardClientServiceTest {

    // class under test
    private StandardClientService clientService;

    @Mock
    private ClientAttendanceDao clientAttendanceDao;
    
    private final int client1Id = 123456;
    private final int client2Id = 123457;
    private final LocalDate meetingDate = new LocalDate("2008-02-14");

    @Before
    public void setUpSUTAndInjectMocks() {
        clientService = new StandardClientService(clientAttendanceDao);
    }

    @Test
    public void shouldGetNoClientAttendanceWhenEmptyDtoListIsPassedToService() throws Exception {
        
        final List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        
        // exercise test
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService
                .getClientAttendance(clientAttendanceDtos);
        
        assertThat(clientAttendance.size(), is(0));
    }

    @Test
    public void testGetClientAttendanceOneId() throws Exception {
        
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(createNewClientAttendanceDto(client1Id, meetingDate, null));
        
        // stubbing
        when(clientAttendanceDao.getAttendance(client1Id, meetingDate)).thenReturn(AttendanceType.PRESENT);
        
        // exercise test
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService
                .getClientAttendance(clientAttendanceDtos);
        
        assertThat(clientAttendance.size(), is(1));
        ClientAttendanceDto clientAttendanceDto = clientAttendance.get(client1Id);
        assertNotNull(clientAttendanceDto);
        assertEquals(client1Id, (int) clientAttendanceDto.getClientId());
        assertEquals(AttendanceType.PRESENT, clientAttendanceDto.getAttendance());
    }

    @Test
    public void testGetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(createNewClientAttendanceDto(client1Id, meetingDate, null));
        clientAttendanceDtos.add(createNewClientAttendanceDto(client2Id, meetingDate, null));
        
        // stubbing
        when(clientAttendanceDao.getAttendance(client1Id, meetingDate)).thenReturn(AttendanceType.PRESENT);
        when(clientAttendanceDao.getAttendance(client2Id, meetingDate)).thenReturn(AttendanceType.ABSENT);
        
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService
                .getClientAttendance(clientAttendanceDtos);
        
        assertThat(clientAttendance.size(), is(2));
        assertNotNull(clientAttendance.get(client1Id));
        assertEquals(client1Id, (int) clientAttendance.get(client1Id).getClientId());
        assertEquals(AttendanceType.PRESENT, clientAttendance.get(client1Id).getAttendance());
        
        assertNotNull(clientAttendance.get(client2Id));
        assertEquals(client2Id, (int) clientAttendance.get(client2Id).getClientId());
        assertEquals(AttendanceType.ABSENT, clientAttendance.get(client2Id).getAttendance());
    }

    private ClientAttendanceDto createNewClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }

}

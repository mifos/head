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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.test.framework.util.DatabaseTestUtils;
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations={"classpath:integration-test-context.xml"})
public class StandardClientServiceIntegrationTest extends IntegrationTestCaseBase {

	private static final String CUSTOMER_ATTENDANCE = "CUSTOMER_ATTENDANCE";
    private StandardClientService clientService;
    private int client1Id = 123456;
    private AttendanceType client1Attendance = AttendanceType.PRESENT;
    private int client2Id = 123457;
    private AttendanceType client2Attendance = AttendanceType.ABSENT;
    private LocalDate meetingDate = new LocalDate("2009-02-14");

    @Autowired
    private DriverManagerDataSource dataSource;
    
    @Autowired
    private DatabaseTestUtils databaseTestUtils;

    @Before
	public void setUp() throws Exception {
        initializeMifosSoftware();

        clientService = new StandardClientService();
        StandardClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao();
        clientService.setClientAttendanceDao(clientAttendanceDao);

        databaseTestUtils.deleteDataFromTables(dataSource, CUSTOMER_ATTENDANCE);
	}

    @After
    public void tearDown() throws Exception {
	}

    @Test
    public void testGetClientAttendanceTwoIds() throws Exception {
        initializeData();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, null));
        clientAttendanceDtos.add(getClientAttendanceDto(client2Id, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(2, clientAttendance.size());
        Assert.assertEquals(client1Id, (int) clientAttendance.get(client1Id).getClientId());
        Assert.assertEquals(client1Attendance, clientAttendance.get(client1Id).getAttendance());
        Assert.assertEquals(client2Id, (int) clientAttendance.get(client2Id).getClientId());
        Assert.assertEquals(client2Attendance, clientAttendance.get(client2Id).getAttendance());
    }

    @Test
    public void testSetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, AttendanceType.PRESENT));
        clientAttendanceDtos.add(getClientAttendanceDto(client2Id, meetingDate, AttendanceType.ABSENT));
        clientService.setClientAttendance(clientAttendanceDtos);
        databaseTestUtils.verifyTable(getAttendanceDataSet().toString(), CUSTOMER_ATTENDANCE, dataSource);
    }
    
    @Test
    public void testSetClientAttendanceReplaceOneId() throws Exception {
        initializeData();
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        AttendanceType expectedAttendance = AttendanceType.APPROVED_LEAVE;
        clientAttendanceDtos.add(getClientAttendanceDto(client1Id, meetingDate, expectedAttendance));
        clientService.setClientAttendance(clientAttendanceDtos);
        Map<Integer, ClientAttendanceDto> actualClientAttendanceDtos = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(expectedAttendance, actualClientAttendanceDtos.get(client1Id).getAttendance());
        databaseTestUtils.verifyTable(getReplacedAttendanceDataSet().toString(), CUSTOMER_ATTENDANCE, dataSource);
    }
    
    private ClientAttendanceDto getClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }
    
    private void initializeMifosSoftware() {
        DatabaseSetup.configureLogging();
        DatabaseSetup.initializeHibernate();
    }
    
    private void initializeData() throws DataSetException, IOException, SQLException, DatabaseUnitException {
        getAttendanceDataSet().insert(dataSource);
    }

    private SimpleDataSet getAttendanceDataSet() {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123456", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1", "DISCRIMINATOR=CLIENT");
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123457", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1", "DISCRIMINATOR=CLIENT");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123456", "ATTENDANCE=1");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123457", "ATTENDANCE=2");
        return attendanceDataSet;
    }

    private SimpleDataSet getReplacedAttendanceDataSet() {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=1", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123456", "ATTENDANCE=3");
        attendanceDataSet.row(CUSTOMER_ATTENDANCE, "ID=2", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123457", "ATTENDANCE=2");
        return attendanceDataSet;
    }
}

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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.mifos.test.framework.util.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations={"classpath:integration-test-context.xml"})
public class StandardClientServiceIntegrationTest extends IntegrationTestCaseBase {

	private StandardClientService clientService;
    private int clientId1 = 123456;
    private AttendanceType client1Attendance = AttendanceType.PRESENT;
    private int clientId2 = 123457;
    private AttendanceType client2Attendance = AttendanceType.ABSENT;
    private LocalDate meetingDate = new LocalDate("2009-02-14");

    @Autowired
    private DriverManagerDataSource dataSource;
    
    @Before
	public void setUp() throws Exception {
        initializeMifosSoftware();
        initializeData();
	}

    private void initializeData() throws DataSetException, IOException, SQLException, DatabaseUnitException {
        SimpleDataSet attendanceDataSet = new SimpleDataSet();
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123456", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1");
        attendanceDataSet.row("CUSTOMER", "CUSTOMER_ID=123457", "CUSTOMER_LEVEL_ID=1", "VERSION_NO=1");
        attendanceDataSet.row("CUSTOMER_ATTENDANCE", "ID=1", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123456", "ATTENDANCE=1");
        attendanceDataSet.row("CUSTOMER_ATTENDANCE", "ID=2", "MEETING_DATE=2009-02-14", "CUSTOMER_ID=123457", "ATTENDANCE=2");
        attendanceDataSet.insert(dataSource);
        
        clientService = new StandardClientService();
        StandardClientAttendanceDao clientAttendanceDao = new StandardClientAttendanceDao();
        clientService.setClientAttendanceDao(clientAttendanceDao);
    }

    @After
    public void tearDown() throws Exception {
	}

    @Test
    public void testGetClientAttendanceTwoIds() throws Exception {
        List<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        clientAttendanceDtos.add(getClientAttendanceDto(clientId1, meetingDate, null));
        clientAttendanceDtos.add(getClientAttendanceDto(clientId2, meetingDate, null));
        HashMap<Integer, ClientAttendanceDto> clientAttendance = clientService.getClientAttendance(clientAttendanceDtos);
        Assert.assertEquals(2, clientAttendance.size());
        Assert.assertEquals(clientId1, (int) clientAttendance.get(clientId1).getClientId());
        Assert.assertEquals(client1Attendance, clientAttendance.get(clientId1).getAttendance());
        Assert.assertEquals(clientId2, (int) clientAttendance.get(clientId2).getClientId());
        Assert.assertEquals(client2Attendance, clientAttendance.get(clientId2).getAttendance());
    }

    private ClientAttendanceDto getClientAttendanceDto(int clientId, LocalDate meetingDate, AttendanceType attendance) {
        return new ClientAttendanceDto(clientId, meetingDate, attendance);
    }
    
    private void initializeMifosSoftware() {
        try {
            DatabaseSetup.configureLogging();
            DatabaseSetup.initializeHibernate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to start up", e);
        }
    }
    

    private void commitTransaction() throws SQLException {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            jdbcConnection.commit();
        }
        finally {
            //jdbcConnection.close();
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }
    
    
    
}

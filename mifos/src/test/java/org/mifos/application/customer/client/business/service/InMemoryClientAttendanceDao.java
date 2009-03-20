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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.framework.exceptions.PersistenceException;

public class InMemoryClientAttendanceDao implements ClientAttendanceDao {

    private HashMap<String, AttendanceType> attendance = new HashMap<String, AttendanceType>();
    private HashMap<String, LocalDate> meetingDate = new HashMap<String, LocalDate>();
    private List<ClientAttendanceBO> attendanceByOfficeIdAndMeetingDate = new ArrayList<ClientAttendanceBO>();
    
    @Override
    public AttendanceType getAttendance(Integer clientId, LocalDate meetingDate) {
        return attendance.get(getKey(clientId, meetingDate));
    }

    @Override
    public void setAttendance(Integer clientId, LocalDate meetingDate, AttendanceType attendance) {
        String key = getKey(clientId, meetingDate);
        this.attendance.put(key, attendance);
        this.meetingDate.put(key, meetingDate);
    }

    public void setAttendance(Integer clientId, LocalDate meetingDate, AttendanceType attendance, Short officeId){
        ClientAttendanceBO clientAttendanceBO = new ClientAttendanceBO();
        clientAttendanceBO.setId(clientId);
        clientAttendanceBO.setMeetingDate(meetingDate.toDateMidnight().toDate());
        clientAttendanceBO.setAttendance(attendance);
        attendanceByOfficeIdAndMeetingDate.add(clientAttendanceBO);
    }
    
    @Override
    public List<ClientAttendanceBO> getClientAttendance(Date meetingDate, Short officeId) throws PersistenceException {
        return attendanceByOfficeIdAndMeetingDate;
    }

    private String getKey(Integer clientId, LocalDate meetingDate) {
        return clientId.toString() + "-" + meetingDate.toString();
    }
    
    public int getNumberOfRecords() {
        return attendance.size();
    }

}

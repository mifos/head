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

package org.mifos.customers.client.business.service;

import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.customers.client.business.AttendanceType;
import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class ClientAttendanceDto implements DataTransferObject {

    private final Integer clientId;
    private final LocalDate meetingDate;
    private final AttendanceType attendance;

    public ClientAttendanceDto(Integer customerId, Date meetingDate) {
        this.attendance = AttendanceType.PRESENT;
        this.meetingDate = new LocalDate(meetingDate);
        this.clientId = customerId;
    }

    public ClientAttendanceDto(Integer customerId, Date meetingDate, Short attendance) {
        this.attendance = AttendanceType.fromShort(attendance);
        this.meetingDate = new LocalDate(meetingDate);
        this.clientId = customerId;
    }

    public ClientAttendanceDto(Integer clientId, LocalDate meetingDate, AttendanceType attendance) {
        this.clientId = clientId;
        this.attendance = attendance;
        this.meetingDate = meetingDate;
    }

    public Short getAttendanceId() {
        return attendance.getValue();
    }

    public Date getActionDate() {
        return meetingDate.toDateMidnight().toDate();
    }

    public Integer getClientId() {
        return clientId;
    }

    public LocalDate getMeetingDate() {
        return this.meetingDate;
    }

    public AttendanceType getAttendance() {
        return this.attendance;
    }
}
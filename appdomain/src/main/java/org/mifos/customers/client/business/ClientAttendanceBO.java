/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.client.business;

import java.util.Date;

import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.business.AbstractBusinessObject;

/**
 * This class is the value object for storing the attendance details of the
 * customer.
 */
public class ClientAttendanceBO extends AbstractBusinessObject {

    private Integer id;
    private Date meetingDate;
    private CustomerBO customer;
    private Short attendance;

    public ClientAttendanceBO() {
        super();
    }

    public ClientAttendanceBO(final Integer id, final Date meetingDate, final Short attendance) {
        super();
        this.id = id;
        this.attendance = attendance;
        this.meetingDate = meetingDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public Short getAttendance() {
        return attendance;
    }

    public AttendanceType getAttendanceAsEnum() {
        return AttendanceType.fromShort(attendance);
    }

    public void setAttendance(final Short attendance) {
        this.attendance = attendance;
    }

    public void setAttendance(final AttendanceType type) {
        setAttendance(type.getValue());
    }

    public java.util.Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(final java.util.Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public void setCustomer(final CustomerBO customer) {
        this.customer = customer;
    }

}

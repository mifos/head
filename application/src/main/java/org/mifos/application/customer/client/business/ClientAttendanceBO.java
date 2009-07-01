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

import java.util.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.BusinessObject;

/**
 * This class is the value object for storing the attendance details of the
 * customer.
 */
public class ClientAttendanceBO extends BusinessObject {

    private static final long serialVersionUID = -6373550605583206363L;
    private Integer id;
    private Date meetingDate;
    private CustomerBO customer;
    private Short attendance;

    public ClientAttendanceBO() {
        super();
    }

    public ClientAttendanceBO(Integer id, Date meetingDate, Short attendance) {
        super();
        this.id = id;
        this.attendance = attendance;
        this.meetingDate = meetingDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getAttendance() {
        return attendance;
    }

    public AttendanceType getAttendanceAsEnum() {
        return AttendanceType.fromShort(attendance);
    }

    public void setAttendance(Short attendance) {
        this.attendance = attendance;
    }

    public void setAttendance(AttendanceType type) {
        setAttendance(type.getValue());
    }

    public java.util.Date getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(java.util.Date meetingDate) {
        this.meetingDate = meetingDate;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerBO customer) {
        this.customer = customer;
    }

}

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

package org.mifos.customers.personnel.business;

import java.util.Date;

import org.mifos.application.util.helpers.Status;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.DateTimeService;

public class PersonnelMovementEntity extends PersistentObject {

    private final Short personnelMovementId;

    private final PersonnelBO personnel;

    private OfficeBO office;

    private final Date startDate;

    private Date endDate;

    private Short status;

    public PersonnelMovementEntity(PersonnelBO personnel, Date startDate) {
        this.personnel = personnel;
        this.office = personnel.getOffice();
        this.startDate = startDate;
        this.status = Status.ACTIVE.getValue();
        this.personnelMovementId = null;
    }

    protected PersonnelMovementEntity() {
        this.personnelMovementId = null;
        this.personnel = null;
        this.office = null;
        this.startDate = null;
    }

    void updateStatus(Status status) {
        this.status = status.getValue();
    }

    public boolean isActive() {
        return status.equals(Status.ACTIVE.getValue());
    }

    public Date getEndDate() {
        return endDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Short getPersonnelMovementId() {
        return personnelMovementId;
    }

    public Date getStartDate() {
        return startDate;
    }

    void makeInActive(Short updatedBy) {
        updateStatus(Status.INACTIVE);
        setUpdatedBy(updatedBy);
        setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        setEndDate(new DateTimeService().getCurrentJavaDateTime());
    }
}

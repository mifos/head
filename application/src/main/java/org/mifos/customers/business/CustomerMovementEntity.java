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

package org.mifos.customers.business;

import java.util.Date;

import org.mifos.application.util.helpers.Status;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.DateTimeService;

public class CustomerMovementEntity extends PersistentObject {

    private final Integer customerMovementId;

    private Short status;

    private final Date startDate;

    private Date endDate;

    private final CustomerBO customer;

    private final OfficeBO office;

    private Date updatedDate;
    
    private Short updatedBy;

    public CustomerMovementEntity(CustomerBO customer, Date startDate) {
        this.customer = customer;
        this.office = customer.getOffice();
        this.startDate = startDate;
        this.status = Status.ACTIVE.getValue();
        this.customerMovementId = null;
    }

    /*
     * Adding a default constructor is hibernate's requirement and should not be
     * used to create a valid Object.
     */
    protected CustomerMovementEntity() {
        this.customerMovementId = null;
        this.customer = null;
        this.office = null;
        this.startDate = null;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public OfficeBO getOffice() {
        return office;
    }

    void updateStatus(Status status) {
        this.status = status.getValue();
    }

    public boolean isActive() {
        return status.equals(Status.ACTIVE.getValue());
    }

    void makeInactive(Short updatedBy) {
        updateStatus(Status.INACTIVE);
        setUpdatedBy(updatedBy);
        setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        setEndDate(new DateTimeService().getCurrentJavaDateTime());
    }
    
        public Short getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Short updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

}

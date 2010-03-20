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
package org.mifos.application.servicefacade;

import java.io.Serializable;
import java.util.Date;

import org.mifos.customers.business.CustomerView;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.personnel.business.PersonnelView;

/**
 * DTO for data entered on collection sheet form.
 */
public class CollectionSheetFormEnteredDataDto implements Serializable {

    private final OfficeView office;
    private final PersonnelView loanOfficer;
    private final CustomerView customer;
    private final ListItem<Short> paymentType;
    private final Date meetingDate;
    private final Date receiptDate;
    private final String receiptId;

    public CollectionSheetFormEnteredDataDto(OfficeView office, PersonnelView loanOfficer, CustomerView customer,
            ListItem<Short> paymentType, Date meetingDate, Date receiptDate, String receiptId) {
        this.office = office;
        this.loanOfficer = loanOfficer;
        this.customer = customer;
        this.paymentType = paymentType;
        this.meetingDate = meetingDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
    }

    public OfficeView getOffice() {
        return this.office;
    }

    public PersonnelView getLoanOfficer() {
        return this.loanOfficer;
    }

    public CustomerView getCustomer() {
        return this.customer;
    }

    public Date getMeetingDate() {
        return this.meetingDate;
    }

    public Date getReceiptDate() {
        return this.receiptDate;
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public ListItem<Short> getPaymentType() {
        return this.paymentType;
    }
}

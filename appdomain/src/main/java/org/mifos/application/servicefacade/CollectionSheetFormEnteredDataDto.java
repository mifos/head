/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.Date;

import org.mifos.customers.api.DataTransferObject;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

/**
 * DTO for data entered on collection sheet form.
 */
public class CollectionSheetFormEnteredDataDto implements DataTransferObject {

    private final OfficeDetailsDto office;
    private final PersonnelDto loanOfficer;
    private final CustomerDto customer;
    private final ListItem<Short> paymentType;
    private final Date meetingDate;
    private final Date receiptDate;
    private final String receiptId;
    private final CustomerDto member;
    private final CustomerDto group;

    public CollectionSheetFormEnteredDataDto(OfficeDetailsDto office, PersonnelDto loanOfficer, CustomerDto customer,
            ListItem<Short> paymentType, Date meetingDate, Date receiptDate, String receiptId,CustomerDto group,CustomerDto member) {
        this.office = office;
        this.loanOfficer = loanOfficer;
        this.customer = customer;
        this.paymentType = paymentType;
        this.meetingDate = meetingDate;
        this.receiptDate = receiptDate;
        this.receiptId = receiptId;
        this.group=group;
        this.member=member;
    }

    public OfficeDetailsDto getOffice() {
        return this.office;
    }

    public PersonnelDto getLoanOfficer() {
        return this.loanOfficer;
    }

    public CustomerDto getCustomer() {
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

	public CustomerDto getMember() {
		return member;
	}

	public CustomerDto getGroup() {
		return group;
	}
    
}

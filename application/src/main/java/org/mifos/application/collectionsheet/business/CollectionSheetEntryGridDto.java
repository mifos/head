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

package org.mifos.application.collectionsheet.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.ProductDto;

/**
 * 
 */
public class CollectionSheetEntryGridDto implements Serializable {

    private final CollectionSheetEntryView bulkEntryParent;
    private final PersonnelView loanOfficer;
    private final OfficeView office;
    private final ListItem<Short> paymentType;
    private final Date transactionDate;
    private final String receiptId;
    private final Date receiptDate;
    private List<ProductDto> loanProducts = new ArrayList<ProductDto>();
    private List<ProductDto> savingProducts = new ArrayList<ProductDto>();
    private HashMap<Integer, ClientAttendanceDto> clientAttendance = new HashMap<Integer, ClientAttendanceDto>();
    private List<CustomValueListElement> attendanceTypesList = new ArrayList<CustomValueListElement>();
    
    /**
     * used when previewing
     */
    private final int totalCustomers;

    public CollectionSheetEntryGridDto(CollectionSheetEntryView collectionSheetParent, PersonnelView loanOfficer,
            OfficeView office, ListItem<Short> paymentType, Date meetingDate, String receiptId, Date receiptDate,
            List<ProductDto> loanProductDtos, List<ProductDto> savingProductDtos,
            HashMap<Integer, ClientAttendanceDto> clientAttendance, List<CustomValueListElement> attendanceTypesList) {
        this.bulkEntryParent = collectionSheetParent;
        this.loanOfficer = loanOfficer;
        this.office = office;
        this.paymentType = paymentType;
        this.transactionDate = meetingDate;
        this.receiptId = receiptId;
        this.receiptDate = receiptDate;
        this.loanProducts = loanProductDtos;
        this.savingProducts = savingProductDtos;
        this.clientAttendance = clientAttendance;
        this.attendanceTypesList = attendanceTypesList;
        this.totalCustomers = collectionSheetParent.getCountOfCustomers();
    }

    public CollectionSheetEntryView getBulkEntryParent() {
        return bulkEntryParent;
    }

    public PersonnelView getLoanOfficer() {
        return loanOfficer;
    }

    public OfficeView getOffice() {
        return office;
    }

    public ListItem<Short> getPaymentType() {
        return paymentType;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }
    
    public List<ProductDto> getLoanProducts() {
        return this.loanProducts;
    }

    public List<ProductDto> getSavingProducts() {
        return this.savingProducts;
    }

    public HashMap<Integer, ClientAttendanceDto> getClientAttendance() {
        return this.clientAttendance;
    }

    public List<CustomValueListElement> getAttendanceTypesList() {
        return this.attendanceTypesList;
    }
}

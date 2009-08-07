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

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.ProductDto;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * NOTE: i am not a {@link BusinessObject} but a DTO.
 */
public class CollectionSheetEntryGridDto extends BusinessObject {

    private PersonnelView loanOfficer;
    private OfficeView office;
    private ListItem<Short> paymentType;
    private CollectionSheetEntryView bulkEntryParent;
    private String receiptId;
    private Date receiptDate;
    private Date transactionDate;
    private List<ProductDto> loanProducts;
    private List<ProductDto> savingProducts;
    private HashMap<Integer, ClientAttendanceDto> clientAttendance;
    private List<CustomValueListElement> attendanceTypesList;
    
    /**
     * used when previewing
     */
    private int totalCustomers;

    // TODO - keithw - refactor usage in code where extension of BusinessObject
    // is used.
    public CollectionSheetEntryGridDto() {
        super();
    }

    public CollectionSheetEntryGridDto(UserContext userContext) {
        super(userContext);
    }

    public CollectionSheetEntryView getBulkEntryParent() {
        return bulkEntryParent;
    }

    public void setBulkEntryParent(CollectionSheetEntryView bulkEntryParent) {
        this.bulkEntryParent = bulkEntryParent;
    }

    public PersonnelView getLoanOfficer() {
        return loanOfficer;
    }

    public void setLoanOfficer(PersonnelView loanOfficer) {
        this.loanOfficer = loanOfficer;
    }

    public OfficeView getOffice() {
        return office;
    }

    public void setOffice(OfficeView office) {
        this.office = office;
    }

    public ListItem<Short> getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(ListItem<Short> paymentType) {
        this.paymentType = paymentType;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    
    public List<ProductDto> getLoanProducts() {
        return this.loanProducts;
    }

    public void setLoanProducts(List<ProductDto> loanProducts) {
        this.loanProducts = loanProducts;
    }

    public List<ProductDto> getSavingProducts() {
        return this.savingProducts;
    }

    public void setSavingProducts(List<ProductDto> savingProducts) {
        this.savingProducts = savingProducts;
    }

    // TODO -keithw - refactor out of DTO
    public void setcollectionSheetDataView(CollectionSheetDataView collectionSheetDataView) {
        if (null == bulkEntryParent || null == collectionSheetDataView) {
            return;
        }
        if (bulkEntryParent.getCustomerDetail().getCustomerLevelId().equals(CustomerLevel.CENTER.getValue())) {
            populateCenter(collectionSheetDataView);
        } else {
            populateGroup(collectionSheetDataView);
        }
    }

    public void setClientAttendance(HashMap<Integer, ClientAttendanceDto> clientAttendance) {
        this.clientAttendance = clientAttendance;
    }

    public HashMap<Integer, ClientAttendanceDto> getClientAttendance() {
        return this.clientAttendance;
    }

    public List<CustomValueListElement> getAttendanceTypesList() {
        return this.attendanceTypesList;
    }

    public void setAttendanceTypesList(List<CustomValueListElement> attendanceTypesList) {
        this.attendanceTypesList = attendanceTypesList;
    }

    private void populateCenter(CollectionSheetDataView bulkEntryDataView) {
        List<CollectionSheetEntryView> bulkEntryChildrenViews = bulkEntryParent.getCollectionSheetEntryChildren();
        int rowIndex = 0;
        for (CollectionSheetEntryView bulkEntryChildernView : bulkEntryChildrenViews) {
            List<CollectionSheetEntryView> bulkEntrySubChildrenViews = bulkEntryChildernView
                    .getCollectionSheetEntryChildren();
            for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrenViews) {
                setLoanAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                        bulkEntryDataView.getDisbursementAmountEntered());
                setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                        bulkEntryDataView.getWithDrawalAmountEntered());
                setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView
                        .getCustomerAccountAmountEntered());
                setClientAttendance(bulkEntrySubChildView, bulkEntryDataView.getAttendance()[rowIndex]);
                rowIndex++;
            }
            setLoanAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                    bulkEntryDataView.getDisbursementAmountEntered());
            setSavingsAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                    bulkEntryDataView.getWithDrawalAmountEntered());
            setCustomerAccountAmountEntered(bulkEntryChildernView, rowIndex, bulkEntryDataView
                    .getCustomerAccountAmountEntered());
            rowIndex++;
        }
        setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                bulkEntryDataView.getWithDrawalAmountEntered());
        setCustomerAccountAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getCustomerAccountAmountEntered());
    }

    private void populateGroup(CollectionSheetDataView bulkEntryDataView) {
        int rowIndex = 0;
        List<CollectionSheetEntryView> bulkEntrySubChildrens = bulkEntryParent.getCollectionSheetEntryChildren();
        for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrens) {
            setLoanAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                    bulkEntryDataView.getDisbursementAmountEntered());
            setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                    bulkEntryDataView.getWithDrawalAmountEntered());
            setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView
                    .getCustomerAccountAmountEntered());
            setClientAttendance(bulkEntrySubChildView, bulkEntryDataView.getAttendance()[rowIndex]);
            rowIndex++;
        }

        setLoanAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getLoanAmountEntered(), bulkEntryDataView
                .getDisbursementAmountEntered());
        setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                bulkEntryDataView.getWithDrawalAmountEntered());
        setCustomerAccountAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getCustomerAccountAmountEntered());
    }

    private void setLoanAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[][] loanAmountsEntered, String[][] disBursementAmountEntered) {
        int columnIndex = 0;
        
        for (ProductDto prdOffering : loanProducts) {
            String enteredAmountValue = loanAmountsEntered[rowIndex][columnIndex];
            String disbursementAmountEntered = disBursementAmountEntered[rowIndex][columnIndex];
            collectionSheetEntryView.setLoanAmountsEntered(prdOffering.getId(), enteredAmountValue,
                    disbursementAmountEntered);
            columnIndex++;
        }
    }

    private void setSavingsAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[][] depositAmountsEntered, String[][] withDrawalsAmountEntered) {
        int columnIndex = 0;
        for (ProductDto prdOffering : savingProducts) {
            String depositAmountEnteredValue = depositAmountsEntered[rowIndex][columnIndex];
            String withDrawalAmountEnteredValue = withDrawalsAmountEntered[rowIndex][columnIndex];
            if (depositAmountEnteredValue != null || withDrawalAmountEnteredValue != null) {
                collectionSheetEntryView.setSavinsgAmountsEntered(prdOffering.getId(),
                        depositAmountEnteredValue, withDrawalAmountEnteredValue);
            }
            columnIndex++;
        }
    }

    private void setCustomerAccountAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[] customerAccountAmountEntered) {
        String customerAccountAmountEnteredValue = customerAccountAmountEntered[rowIndex];
        if (customerAccountAmountEnteredValue != null) {
            collectionSheetEntryView.setCustomerAccountAmountEntered(customerAccountAmountEnteredValue);
        }
    }

    private void setClientAttendance(CollectionSheetEntryView collectionSheetEntryView, String attendance) {
        if (null != attendance && attendance.length() > 0) {
            collectionSheetEntryView.setAttendence(Short.valueOf(attendance));
        }
    }
}

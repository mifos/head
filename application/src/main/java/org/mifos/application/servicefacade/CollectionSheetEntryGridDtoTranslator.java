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
package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;

/**
 *
 */
public class CollectionSheetEntryGridDtoTranslator {

    public CollectionSheetEntryGridDto translateAsCenter(
            final CollectionSheetEntryGridDto collectionSheetEntryGridDto,
            final CollectionSheetDataView dataView) {

        final CollectionSheetEntryView collectionSheetParent = collectionSheetEntryGridDto.getBulkEntryParent();
        final List<CollectionSheetEntryView> collectionSheetChildViews = collectionSheetParent
                .getCollectionSheetEntryChildren();

        int rowIndex = 0;
        for (CollectionSheetEntryView collectionSheetChild : collectionSheetChildViews) {

            final List<CollectionSheetEntryView> bulkEntrySubChildrenViews = collectionSheetChild
                    .getCollectionSheetEntryChildren();

            for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrenViews) {

                setLoanAmountEntered(bulkEntrySubChildView, rowIndex, dataView.getLoanAmountEntered(), dataView
                        .getDisbursementAmountEntered(), collectionSheetEntryGridDto.getLoanProducts());
                setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, dataView.getDepositAmountEntered(), dataView
                        .getWithDrawalAmountEntered(), collectionSheetEntryGridDto.getSavingProducts());
                setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, dataView
                        .getCustomerAccountAmountEntered());
                setClientAttendance(bulkEntrySubChildView, dataView.getAttendance()[rowIndex]);

                rowIndex++;
            }

            setLoanAmountEntered(collectionSheetChild, rowIndex, dataView.getLoanAmountEntered(), dataView
                    .getDisbursementAmountEntered(), collectionSheetEntryGridDto.getLoanProducts());

            setSavingsAmountEntered(collectionSheetChild, rowIndex, dataView.getDepositAmountEntered(), dataView
                    .getWithDrawalAmountEntered(), collectionSheetEntryGridDto.getSavingProducts());

            setCustomerAccountAmountEntered(collectionSheetChild, rowIndex, dataView.getCustomerAccountAmountEntered());

            rowIndex++;
        }

        setSavingsAmountEntered(collectionSheetParent, rowIndex, dataView.getDepositAmountEntered(), dataView
                .getWithDrawalAmountEntered(), collectionSheetEntryGridDto.getSavingProducts());

        setCustomerAccountAmountEntered(collectionSheetParent, rowIndex, dataView.getCustomerAccountAmountEntered());

        return collectionSheetEntryGridDto;
    }

    public CollectionSheetEntryGridDto translateAsGroup(
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto,
            final CollectionSheetDataView bulkEntryDataView) {

        final CollectionSheetEntryView bulkEntryParent = previousCollectionSheetEntryDto.getBulkEntryParent();
        int rowIndex = 0;
        List<CollectionSheetEntryView> bulkEntrySubChildrens = bulkEntryParent.getCollectionSheetEntryChildren();
        for (CollectionSheetEntryView bulkEntrySubChildView : bulkEntrySubChildrens) {
            setLoanAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getLoanAmountEntered(),
                    bulkEntryDataView.getDisbursementAmountEntered(), previousCollectionSheetEntryDto.getLoanProducts());
            setSavingsAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                    bulkEntryDataView.getWithDrawalAmountEntered(), previousCollectionSheetEntryDto.getSavingProducts());
            setCustomerAccountAmountEntered(bulkEntrySubChildView, rowIndex, bulkEntryDataView
                    .getCustomerAccountAmountEntered());
            setClientAttendance(bulkEntrySubChildView, bulkEntryDataView.getAttendance()[rowIndex]);
            rowIndex++;
        }

        setLoanAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getLoanAmountEntered(), bulkEntryDataView
                .getDisbursementAmountEntered(), previousCollectionSheetEntryDto.getLoanProducts());
        setSavingsAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getDepositAmountEntered(),
                bulkEntryDataView.getWithDrawalAmountEntered(), previousCollectionSheetEntryDto.getSavingProducts());
        setCustomerAccountAmountEntered(bulkEntryParent, rowIndex, bulkEntryDataView.getCustomerAccountAmountEntered());

        return previousCollectionSheetEntryDto;
    }

    private void setLoanAmountEntered(CollectionSheetEntryView collectionSheetEntryView, int rowIndex,
            String[][] loanAmountsEntered, String[][] disBursementAmountEntered, List<ProductDto> loanProducts) {
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
            String[][] depositAmountsEntered, String[][] withDrawalsAmountEntered, List<ProductDto> savingProducts) {

        int columnIndex = 0;
        for (ProductDto prdOffering : savingProducts) {

            String depositAmountEnteredValue = depositAmountsEntered[rowIndex][columnIndex];
            String withDrawalAmountEnteredValue = withDrawalsAmountEntered[rowIndex][columnIndex];

            if (depositAmountEnteredValue != null || withDrawalAmountEnteredValue != null) {
                collectionSheetEntryView.setSavinsgAmountsEntered(prdOffering.getId(), depositAmountEnteredValue,
                        withDrawalAmountEnteredValue);
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

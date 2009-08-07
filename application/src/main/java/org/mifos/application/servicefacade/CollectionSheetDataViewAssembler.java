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

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;

/**
 *
 */
public class CollectionSheetDataViewAssembler {

    public CollectionSheetDataView toDto(final HttpServletRequest request,
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto) {

        final int customers = previousCollectionSheetEntryDto.getTotalCustomers();
        final int loanProductsSize = previousCollectionSheetEntryDto.getLoanProducts().size();
        final int savingsProductSize = previousCollectionSheetEntryDto.getSavingProducts().size();

        final String enteredAmount[][] = new String[customers + 1][loanProductsSize];
        final String disbursalAmount[][] = new String[customers + 1][loanProductsSize];
        final String depositAmountEntered[][] = new String[customers + 1][savingsProductSize];
        final String withdrawalAmountEntered[][] = new String[customers + 1][savingsProductSize];
        final String attendance[] = new String[customers + 1];
        final String customerAccountAmountEntered[] = new String[customers + 1];

        for (int rowIndex = 0; rowIndex <= customers; rowIndex++) {
            attendance[rowIndex] = request.getParameter("attendanceSelected[" + rowIndex + "]");
            
            for (int columnIndex = 0; columnIndex < loanProductsSize; columnIndex++) {
                enteredAmount[rowIndex][columnIndex] = request.getParameter("enteredAmount[" + rowIndex + "]["
                        + columnIndex + "]");
                disbursalAmount[rowIndex][columnIndex] = request.getParameter("enteredAmount[" + rowIndex + "]["
                        + (loanProductsSize + savingsProductSize + columnIndex) + "]");
            }
            
            for (int columnIndex = 0; columnIndex < savingsProductSize; columnIndex++) {
                depositAmountEntered[rowIndex][columnIndex] = request.getParameter("depositAmountEntered[" + rowIndex
                        + "][" + (loanProductsSize + columnIndex) + "]");
                withdrawalAmountEntered[rowIndex][columnIndex] = request.getParameter("withDrawalAmountEntered["
                        + rowIndex + "][" + ((2 * loanProductsSize) + savingsProductSize + columnIndex) + "]");
            }
            
            customerAccountAmountEntered[rowIndex] = request.getParameter("customerAccountAmountEntered[" + rowIndex
                    + "][" + (2 * (loanProductsSize + savingsProductSize)) + "]");
        }
        
        return new CollectionSheetDataView(enteredAmount, disbursalAmount,
                depositAmountEntered, withdrawalAmountEntered, customerAccountAmountEntered, attendance);
    }

}

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

package org.mifos.application.collectionsheet.util.helpers;

/**
 *
 */
public class CollectionSheetDataView {

    private final String[][] loanAmountEntered;

    private final String[][] disbursementAmountEntered;

    private final String[][] depositAmountEntered;

    private final String[][] withDrawalAmountEntered;

    private final String[] customerAccountAmountEntered;

    private final String[] attendance;

    public CollectionSheetDataView(String[][] loanAmountEntered, String[][] disbursementAmountEntered,
            String[][] depositAmountEntered, String[][] withDrawalAmountEntered, String[] customerAccountAmountEntered,
            String[] attendance) {
                this.loanAmountEntered = loanAmountEntered;
        this.disbursementAmountEntered = disbursementAmountEntered;
        this.depositAmountEntered = depositAmountEntered;
        this.withDrawalAmountEntered = withDrawalAmountEntered;
        this.customerAccountAmountEntered = customerAccountAmountEntered;
        this.attendance = attendance;
    }

    public String[][] getLoanAmountEntered() {
        return loanAmountEntered;
    }

    public String[][] getDisbursementAmountEntered() {
        return disbursementAmountEntered;
    }

    public String[][] getDepositAmountEntered() {
        return depositAmountEntered;
    }

    public String[][] getWithDrawalAmountEntered() {
        return withDrawalAmountEntered;
    }

    public String[] getCustomerAccountAmountEntered() {
        return customerAccountAmountEntered;
    }

    public String[] getAttendance() {
        return attendance;
    }
}

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
 
package org.mifos.application.bulkentry.util.helpers;

public class BulkEntryDataView {

    private String[][] loanAmountEntered;

    private String[][] disbursementAmountEntered;

    private String[][] depositAmountEntered;

    private String[][] withDrawalAmountEntered;

    private String[] customerAccountAmountEntered;

    private String[] attendance;

    public BulkEntryDataView() {
        loanAmountEntered = new String[][] {};
        disbursementAmountEntered = new String[][] {};
        depositAmountEntered = new String[][] {};
        withDrawalAmountEntered = new String[][] {};
        customerAccountAmountEntered = new String[] {};
        attendance = new String[] {};
    }

    public String[][] getLoanAmountEntered() {
        return loanAmountEntered;
    }

    public void setLoanAmountEntered(String[][] loanAmountEntered) {
        this.loanAmountEntered = loanAmountEntered;
    }

    public String[][] getDisbursementAmountEntered() {
        return disbursementAmountEntered;
    }

    public void setDisbursementAmountEntered(String[][] disbursementAmountEntered) {
        this.disbursementAmountEntered = disbursementAmountEntered;
    }

    public String[][] getDepositAmountEntered() {
        return depositAmountEntered;
    }

    public void setDepositAmountEntered(String[][] depositAmountEntered) {
        this.depositAmountEntered = depositAmountEntered;
    }

    public String[][] getWithDrawalAmountEntered() {
        return withDrawalAmountEntered;
    }

    public void setWithDrawalAmountEntered(String[][] withDrawalAmountEntered) {
        this.withDrawalAmountEntered = withDrawalAmountEntered;
    }

    public String[] getCustomerAccountAmountEntered() {
        return customerAccountAmountEntered;
    }

    public void setCustomerAccountAmountEntered(String[] customerAccountAmountEntered) {
        this.customerAccountAmountEntered = customerAccountAmountEntered;
    }

    public String[] getAttendance() {
        return attendance;
    }

    public void setAttendance(String[] attendance) {
        this.attendance = attendance;
    }

}

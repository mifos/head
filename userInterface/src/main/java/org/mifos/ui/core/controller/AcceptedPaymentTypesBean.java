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

package org.mifos.ui.core.controller;

public class AcceptedPaymentTypesBean {

    private String feesRight="";
    private String disbursementsRight="";
    private String repaymentsRight="";
    private String withdrawalsRight="";
    private String depositsRight="";

    public String getFeesRight() {
        return this.feesRight;
    }

    public void setFeesRight(String feesRight) {
        this.feesRight = feesRight;
    }

    public String getDisbursementsRight() {
        return this.disbursementsRight;
    }

    public void setDisbursementsRight(String disbursementsRight) {
        this.disbursementsRight = disbursementsRight;
    }

    public String getRepaymentsRight() {
        return this.repaymentsRight;
    }

    public void setRepaymentsRight(String repaymentsRight) {
        this.repaymentsRight = repaymentsRight;
    }

    public String getWithdrawalsRight() {
        return this.withdrawalsRight;
    }

    public void setWithdrawalsRight(String withdrawalsRight) {
        this.withdrawalsRight = withdrawalsRight;
    }

    public String getDepositsRight() {
        return this.depositsRight;
    }

    public void setDepositsRight(String depositsRight) {
        this.depositsRight = depositsRight;
    }
}

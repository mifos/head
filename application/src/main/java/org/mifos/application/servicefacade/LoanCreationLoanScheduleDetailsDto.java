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

import java.util.List;

import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;

public class LoanCreationLoanScheduleDetailsDto {

    private final boolean isGroup;
    private final boolean isGlimApplicable;
    private final double glimLoanAmount;
    private final boolean isLoanPendingApprovalDefined;
    private final List<RepaymentScheduleInstallment> installments;
    private final List<PaymentDataHtmlBean> paymentDataBeans;

    public LoanCreationLoanScheduleDetailsDto(boolean isGroup, boolean isGlimApplicable, double glimLoanAmount, boolean isLoanPendingApprovalDefined, List<RepaymentScheduleInstallment> installments, List<PaymentDataHtmlBean> paymentDataBeans) {
        this.isGroup = isGroup;
        this.isGlimApplicable = isGlimApplicable;
        this.glimLoanAmount = glimLoanAmount;
        this.isLoanPendingApprovalDefined = isLoanPendingApprovalDefined;
        this.installments = installments;
        this.paymentDataBeans = paymentDataBeans;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public boolean isGlimApplicable() {
        return this.isGlimApplicable;
    }

    public double getGlimLoanAmount() {
        return this.glimLoanAmount;
    }

    public boolean isLoanPendingApprovalDefined() {
        return this.isLoanPendingApprovalDefined;
    }

    public List<RepaymentScheduleInstallment> getInstallments() {
        return this.installments;
    }

    public List<PaymentDataHtmlBean> getPaymentDataBeans() {
        return this.paymentDataBeans;
    }
}
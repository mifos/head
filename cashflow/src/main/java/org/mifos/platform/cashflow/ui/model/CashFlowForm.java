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

package org.mifos.platform.cashflow.ui.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.springframework.binding.validation.ValidationContext;

public class CashFlowForm implements Serializable {
    private static final long serialVersionUID = -3806820293757764245L;

    private CashFlowDetail cashFlowDetail;
    private boolean captureCapitalLiabilityInfo;
    private BigDecimal loanAmount;
    private Double indebtednessRatio;
    private BigDecimal totalRevenues;
    private BigDecimal totalExpenses;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public CashFlowForm() {
    }

    public CashFlowForm(CashFlowDetail cashFlowDetail, boolean captureCapitalLiabilityInfo, BigDecimal loanAmount, Double indebtednessRatio) {
        this.cashFlowDetail = cashFlowDetail;
        this.captureCapitalLiabilityInfo = captureCapitalLiabilityInfo;
        this.loanAmount = loanAmount;
        this.indebtednessRatio = indebtednessRatio;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        cashFlowDetail.setTotalCapital(totalCapital);
    }

    public void setTotalLiability(BigDecimal totalLiability) {
        cashFlowDetail.setTotalLiability(totalLiability);
    }

    public BigDecimal getTotalCapital() {
        BigDecimal totalCapital = BigDecimal.ZERO;
        if (cashFlowDetail != null) {
            totalCapital = cashFlowDetail.getTotalCapital();
        }
        return totalCapital;
    }

    public BigDecimal getTotalLiability() {
        BigDecimal totalLiability = BigDecimal.ZERO;
        if (cashFlowDetail != null) {
            totalLiability = cashFlowDetail.getTotalLiability();
        }
        return totalLiability;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<MonthlyCashFlowForm> getMonthlyCashFlows() {
        List<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        if (cashFlowDetail != null) {
            for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
                MonthlyCashFlowForm monthlyCashFlowForm = new MonthlyCashFlowForm(monthlyCashFlowDetail);
                monthlyCashFlows.add(monthlyCashFlowForm);
            }
        }
        return monthlyCashFlows;
    }

    public boolean isCaptureCapitalLiabilityInfo() {
        return captureCapitalLiabilityInfo;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public Double getIndebtednessRatio() {
        return indebtednessRatio;
    }

    public boolean shouldForValidateIndebtednessRate() {
        return captureCapitalLiabilityInfo && indebtednessRatio != null && indebtednessRatio > 0 &&
                loanAmount != null && cashFlowDetail != null && cashFlowDetail.shouldForValidateIndebtednessRate();
    }

    /*
     * from newer createLoanAccount.xml flow and not legacy captureCashFlow.xml flow
     */
    public void validateCaptureCashFlowDetails(ValidationContext context) {
        CashFlowValidator validator = new CashFlowValidator();
        validator.validateCaptureCashFlow(this, context);
    }

    public void validateEditCashflow(ValidationContext context) {
        validateCaptureCashFlowDetails(context);
    }

    public BigDecimal getTotalRevenues() {
        return totalRevenues;
    }

    public void setTotalRevenues(BigDecimal totalRevenues) {
        this.totalRevenues = totalRevenues;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalBalance() {
        return totalRevenues.subtract(totalExpenses);
    }

    public BigDecimal computeRepaymentCapacity(BigDecimal totalInstallmentAmount) {
        return getTotalBalance().add(loanAmount).multiply(CashFlowConstants.HUNDRED).
                divide(totalInstallmentAmount, 2, BigDecimal.ROUND_HALF_UP);
    }
}

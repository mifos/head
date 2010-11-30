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
package org.mifos.platform.cashflow.ui.model;

import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashFlowForm implements Serializable {
    private static final long serialVersionUID = -3806820293757764245L;

    private CashFlowDetail cashFlowDetail;
    private boolean captureCapitalLiabilityInfo;
    private BigDecimal loanAmount;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public CashFlowForm() {
        super();
    }

    public CashFlowForm(CashFlowDetail cashFlowDetail, boolean captureCapitalLiabilityInfo, BigDecimal loanAmount) {
        this.cashFlowDetail = cashFlowDetail;
        this.captureCapitalLiabilityInfo = captureCapitalLiabilityInfo;
        this.loanAmount = loanAmount;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        cashFlowDetail.setTotalCapital(totalCapital);
    }

    public void setTotalLiability(BigDecimal totalLiability) {
        cashFlowDetail.setTotalLiability(totalLiability);
    }

    public BigDecimal getTotalCapital() {
        return cashFlowDetail.getTotalCapital();
    }

    public BigDecimal getTotalLiability() {
        return cashFlowDetail.getTotalLiability();
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<MonthlyCashFlowForm> getMonthlyCashFlows() {
        List<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
            monthlyCashFlows.add(new MonthlyCashFlowForm(monthlyCashFlowDetail));
        }
        return monthlyCashFlows;
    }

    public boolean isCaptureCapitalLiabilityInfo() {
        return captureCapitalLiabilityInfo;
    }
}

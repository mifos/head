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

package org.mifos.platform.cashflow.service;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CashFlowDetail implements Serializable {
    
    private static final long serialVersionUID = -6731316163493318834L;
    
    private List<MonthlyCashFlowDetail> monthlyCashFlowDetails;
    private BigDecimal totalCapital;
    private BigDecimal totalLiability;

    public CashFlowDetail() {
        this(new ArrayList<MonthlyCashFlowDetail>());
    }

    public CashFlowDetail(List<MonthlyCashFlowDetail> monthlyCashFlows) {
        monthlyCashFlowDetails = monthlyCashFlows;
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : monthlyCashFlowDetails) {
            monthlyCashFlowDetail.setCashFlowDetail(this);
        }
    }

    public List<MonthlyCashFlowDetail> getMonthlyCashFlowDetails() {
        return monthlyCashFlowDetails;
    }

    public BigDecimal getCumulativeCashFlowForMonth(DateTime dateTime) {
        BigDecimal cumulative = BigDecimal.ZERO;
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : getMonthlyCashFlowDetails()) {
            if (monthlyCashFlowDetail.getDateTime().compareTo(dateTime) <= 0) {
                cumulative = cumulative.add(monthlyCashFlowDetail.getRevenue().subtract(monthlyCashFlowDetail.getExpense()));
            }
        }
        return cumulative;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }

    public void setTotalLiability(BigDecimal totalLiability) {
        this.totalLiability = totalLiability;
    }

    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public BigDecimal getTotalLiability() {
        return totalLiability;
    }

    public boolean shouldForValidateIndebtednessRate() {
        return totalCapital != null && totalCapital.compareTo(BigDecimal.ZERO) > 0 && totalLiability != null;
    }
}
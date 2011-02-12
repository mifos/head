/*
 * Copyright Grameen Foundation USA
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
package org.mifos.platform.cashflow.builder;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

public class CashFlowDetailsBuilder {
    private CashFlowDetail cashFlowDetail;

    public CashFlowDetailsBuilder() {
        cashFlowDetail = new CashFlowDetail(new ArrayList<MonthlyCashFlowDetail>());
    }

    public CashFlowDetailsBuilder withMonthlyCashFlow(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        cashFlowDetail.getMonthlyCashFlowDetails().add(monthlyCashFlowDetail);
        return this;
    }

    public CashFlowDetail build() {
        return cashFlowDetail;
    }

    public CashFlowDetailsBuilder withTotalCapital(double totalCapital) {
        cashFlowDetail.setTotalCapital(new BigDecimal(totalCapital));
        return this;
    }

    public CashFlowDetailsBuilder withTotalLiability(double totalLiability) {
        cashFlowDetail.setTotalLiability(new BigDecimal(totalLiability));
        return this;
    }
}

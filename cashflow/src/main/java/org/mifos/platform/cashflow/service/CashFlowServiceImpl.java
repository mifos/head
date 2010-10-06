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
package org.mifos.platform.cashflow.service;

import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.domain.CashFlow;
import org.mifos.platform.cashflow.domain.MonthlyCashFlow;
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.springframework.beans.factory.annotation.Autowired;

public class CashFlowServiceImpl implements CashFlowService {
    @Autowired
    CashFlowDao cashFlowDao;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public CashFlowServiceImpl() {
    }

    public CashFlowServiceImpl(CashFlowDao cashFlowDao) {
        this.cashFlowDao = cashFlowDao;
    }

    @Override
    public Integer save(CashFlowDetail cashFlowDetail) {
        return cashFlowDao.create(mapToCashFlow(cashFlowDetail));
    }

    private CashFlow mapToCashFlow(CashFlowDetail cashFlowDetail) {
        CashFlow cashFlow = new CashFlow();
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
            cashFlow.add(mapToMonthlyCashFlow(monthlyCashFlowDetail));
        }
        return cashFlow;
    }

    private MonthlyCashFlow mapToMonthlyCashFlow(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        return new MonthlyCashFlow(monthlyCashFlowDetail.getDateTime(), monthlyCashFlowDetail.getRevenue(),
                monthlyCashFlowDetail.getExpense(), monthlyCashFlowDetail.getNotes());
    }
}

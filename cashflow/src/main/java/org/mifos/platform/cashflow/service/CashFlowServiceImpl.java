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
import org.joda.time.Months;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.domain.CashFlow;
import org.mifos.platform.cashflow.domain.MonthlyCashFlow;
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mifos.platform.cashflow.CashFlowConstants.EXTRA_DURATION_FOR_CASH_FLOW_SCHEDULE;
import static org.mifos.platform.cashflow.CashFlowConstants.FIRST_DAY;

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
        // TODO: service side validation
        return cashFlowDao.create(mapToCashFlow(cashFlowDetail));
    }

    @Override
    public CashFlowDetail cashFlowFor(int startYear, int startMonth, double numberOfMonths) {
        DateTime startMonthYear = new DateTime(startYear, startMonth, 1, 1, 1, 1, 1);
        List<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
        for (int i = 0; i < numberOfMonths; i++) {
            monthlyCashFlowDetails.add(getMonthlyCashFlowDetail(startMonthYear));
            startMonthYear = startMonthYear.plusMonths(1);
        }
        return new CashFlowDetail(monthlyCashFlowDetails);
    }

    private MonthlyCashFlowDetail getMonthlyCashFlowDetail(DateTime startMonthYear) {
        return new MonthlyCashFlowDetail(startMonthYear, null, null, null);
    }

    @Override
	public CashFlowBoundary getCashFlowBoundary(DateTime firstInstallmentDueDate, DateTime lastInstallmentDueDate) {
        DateTime monthAfterLastInstallment = lastInstallmentDueDate.plusMonths(EXTRA_DURATION_FOR_CASH_FLOW_SCHEDULE)
                .withDayOfMonth(FIRST_DAY);
        DateTime monthBeforeFirstInstallment = firstInstallmentDueDate.minusMonths(EXTRA_DURATION_FOR_CASH_FLOW_SCHEDULE)
                .withDayOfMonth(FIRST_DAY);
        int numberOfMonths = Months.monthsBetween(monthBeforeFirstInstallment, monthAfterLastInstallment).getMonths() + 1;
        return new CashFlowBoundary(monthBeforeFirstInstallment.getMonthOfYear(), monthBeforeFirstInstallment.getYear(), numberOfMonths);
    }

    private CashFlow mapToCashFlow(CashFlowDetail cashFlowDetail) {
        CashFlow cashFlow = new CashFlow();
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
            cashFlow.add(mapToMonthlyCashFlow(monthlyCashFlowDetail));
            cashFlow.setTotalCapital(cashFlowDetail.getTotalCapital());
            cashFlow.setTotalLiability(cashFlowDetail.getTotalLiability());
        }
        return cashFlow;
    }

    private MonthlyCashFlow mapToMonthlyCashFlow(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        return new MonthlyCashFlow(monthlyCashFlowDetail.getDateTime(), monthlyCashFlowDetail.getRevenue(),
                monthlyCashFlowDetail.getExpense(), monthlyCashFlowDetail.getNotes());
    }
}

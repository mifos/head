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
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.mifos.platform.cashflow.persistence.CashFlowEntity;
import org.mifos.platform.cashflow.persistence.MonthlyCashFlowEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CashFlowServiceImpl implements CashFlowService {
    @Autowired
    CashFlowDao cashFlowDao;


    public CashFlowServiceImpl() {
    }

    public CashFlowServiceImpl(CashFlowDao cashFlowDao) {
        this.cashFlowDao = cashFlowDao;

    }

    @Override
    public Integer save(CashFlowDetail cashFlowDetail) {
        return cashFlowDao.create(mapToCashFlowEntity(cashFlowDetail));
    }

    private CashFlowEntity mapToCashFlowEntity(CashFlowDetail cashFlowDetail) {
        List<MonthlyCashFlowEntity> monthlyCashFlowEntities = new ArrayList<MonthlyCashFlowEntity>();
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
            monthlyCashFlowEntities.add(mapToMonthlyCashFlowEntity(monthlyCashFlowDetail));
        }
        return new CashFlowEntity(monthlyCashFlowEntities);
    }

    private MonthlyCashFlowEntity mapToMonthlyCashFlowEntity(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        return new MonthlyCashFlowEntity(monthlyCashFlowDetail.getRevenue(), monthlyCashFlowDetail.getExpense(), monthlyCashFlowDetail.getNotes());
    }


}

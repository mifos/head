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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.matchers.CashFlowEntityMatcher;
import org.mifos.platform.cashflow.persistence.CashFlowDao;
import org.mifos.platform.cashflow.persistence.CashFlowEntity;
import org.mifos.platform.cashflow.persistence.MonthlyCashFlowEntity;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CashFlowServiceTest {

    private CashFlowService cashFlowService;

    @Mock
    CashFlowDao cashFlowDao;
    private double revenue;
    private double expense;

    @Before
    public void setUp() {
        cashFlowService = new CashFlowServiceImpl(cashFlowDao);
    }

    @Test
    public void shouldSaveCashFlow() {
        Mockito.when(cashFlowDao.create(Mockito.<CashFlowEntity>any())).thenReturn(new Integer(10));
        Integer cashFlowId = cashFlowService.save(getCashFlowDetails());
        Mockito.verify(cashFlowDao, Mockito.times(1)).create(Mockito.argThat(new CashFlowEntityMatcher(getCashFlowEntity())));
        Assert.assertNotNull(cashFlowId);
        Assert.assertEquals(cashFlowId, new Integer(10));
    }

    private CashFlowDetail getCashFlowDetails() {
        revenue = 100d;
        expense = 50d;
        List<MonthlyCashFlowDetail> monthlyCashFlows = new ArrayList<MonthlyCashFlowDetail>();
        monthlyCashFlows.add(new MonthlyCashFlowDetail(revenue, expense, "my notes"));
        monthlyCashFlows.add(new MonthlyCashFlowDetail(revenue + 20, expense + 10, "my other notes"));
        return new CashFlowDetail(monthlyCashFlows);
    }

    public CashFlowEntity getCashFlowEntity() {
        List<MonthlyCashFlowEntity> monthlyCashFlowEntityArrayList = new ArrayList<MonthlyCashFlowEntity>();
        monthlyCashFlowEntityArrayList.add(new MonthlyCashFlowEntity(revenue, expense, "my notes"));
        monthlyCashFlowEntityArrayList.add(new MonthlyCashFlowEntity(revenue + 20, expense + 10, "my other notes"));
        return new CashFlowEntity(monthlyCashFlowEntityArrayList);
    }
}


/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.components.batchjobs.helpers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.framework.util.AssertionUtils.assertSameCollections;
import static org.mifos.framework.util.helpers.MoneyUtils.createMoney;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.branchreport.BranchReportBOFixture;
import org.mifos.reports.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.reports.branchreport.LoanArrearsAgingPeriod;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficecFixture;
import org.mifos.reports.business.service.BranchReportConfigService;
import org.mifos.reports.business.service.BranchReportIntegrationTestCase;
import org.mifos.reports.business.service.BranchReportService;
import org.mifos.reports.business.service.IBranchReportService;
import org.mifos.config.AccountingRules;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class BranchReportLoanArrearsAgingHelperIntegrationTest extends BranchReportIntegrationTestCase {
    public BranchReportLoanArrearsAgingHelperIntegrationTest() throws Exception {
        super();
    }

    private IBranchReportService branchReportServiceMock;
    private List<LoanArrearsAgingPeriod> expectedPeriods;
    private BranchReportConfigService branchReportConfigServiceMock;

    public void testLoanArrearsAgingHelperPopulatesAgingFields() throws Exception {
        BranchReportBO branchReportLoanArrearsBatchBO = BranchReportBOFixture.createBranchReport(Integer.valueOf(1),
                Short.valueOf("3"), DateUtils.currentDate());

        OfficeBO office = OfficecFixture.createOffice(Short.valueOf("3"));
        for (LoanArrearsAgingPeriod period : expectedPeriods) {
            expect(
                    branchReportServiceMock.extractLoanArrearsAgingInfoInPeriod(office.getOfficeId(), period,
                            DEFAULT_CURRENCY)).andReturn(BranchReportBOFixture.createLoanArrearsAging(period));
            expect(branchReportConfigServiceMock.getCurrency()).andReturn(DEFAULT_CURRENCY);
        }
        replay(branchReportServiceMock);
        replay(branchReportConfigServiceMock);
        new BranchReportLoanArrearsAgingHelper(branchReportLoanArrearsBatchBO, branchReportServiceMock,
                branchReportConfigServiceMock).populateLoanArrearsAging();
        verify(branchReportServiceMock);
        verify(branchReportConfigServiceMock);
        assertLoanArrearsAgingPopulated(branchReportLoanArrearsBatchBO.getLoanArrearsAging());
    }

    private void assertLoanArrearsAgingPopulated(Set<BranchReportLoanArrearsAgingBO> loanArrearsAgingSummaries) {
        Assert.assertNotNull(loanArrearsAgingSummaries);
        Collection foundPeriods = CollectionUtils.collect(loanArrearsAgingSummaries, new Transformer() {
            public Object transform(Object input) {
                return ((BranchReportLoanArrearsAgingBO) input).getAgingPeriod();
            }
        });

        assertSameCollections(expectedPeriods, foundPeriods);
    }

    public void testLoanArrearsContructor() throws Exception {
        Money agingAmount = createMoney(TestUtils.RUPEE, 3.3333);
        BranchReportLoanArrearsAgingBO loanArrears = new BranchReportLoanArrearsAgingBO(null, null, null, agingAmount,
                createMoney(TestUtils.RUPEE, 3.3333), createMoney(TestUtils.RUPEE, 666.70));
       Assert.assertEquals(agingAmount.getAmount().setScale(AccountingRules.getDigitsAfterDecimal(),
                RoundingMode.HALF_UP), loanArrears.getAmountAging());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        branchReportServiceMock = createMock(BranchReportService.class);
        branchReportConfigServiceMock = createMock(BranchReportConfigService.class);
        expectedPeriods = Arrays.asList(LoanArrearsAgingPeriod.values());
    }
}

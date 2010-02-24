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

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.reports.business.service.BranchReportConfigService;
import org.mifos.reports.business.service.BranchReportService;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.config.business.Configuration;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.springframework.core.io.Resource;

public class BranchReportStaffSummaryHelperIntegrationTest extends MifosIntegrationTestCase {

    public BranchReportStaffSummaryHelperIntegrationTest() throws Exception {
        super();
    }

    private static final Date RUN_DATE = DateUtils.currentDate();
    private static final Short BRANCH_ID_SHORT = Short.valueOf("2");

    public void testPopulateStaffSummary() throws BatchJobException {
        BranchReportBO branchReportBO = new BranchReportBO(BRANCH_ID_SHORT, RUN_DATE);
        BranchReportStaffSummaryHelper staffSummaryHelper = new BranchReportStaffSummaryHelper(branchReportBO,
                new BranchReportService(), getConfigServiceStub());
        staffSummaryHelper.populateStaffSummary();
        Set<BranchReportStaffSummaryBO> staffSummaries = branchReportBO.getStaffSummaries();
        Assert.assertNotNull(staffSummaries);
    }

    private BranchReportConfigService getConfigServiceStub() {
        return new BranchReportConfigService(null) {

            @Override
            protected void initConfig(Resource configResource) {
            }

            @Override
            public Integer getGracePeriodDays() {
                return Integer.valueOf(1);
            }

            @Override
            public MifosCurrency getCurrency() throws ServiceException {
                return Configuration.getInstance().getSystemConfig().getCurrency();
            }
        };
    }

}

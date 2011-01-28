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

package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.business.service.BranchReportConfigService;
import org.mifos.reports.business.service.IBranchReportService;

public class BranchReportLoanDetailsHelper {

    private final BranchReportBO branchReport;
    private final IBranchReportService branchReportService;
    private BranchReportConfigService branchReportConfigService;

    public BranchReportLoanDetailsHelper(BranchReportBO branchReport, IBranchReportService branchReportService,
            BranchReportConfigService branchReportConfigService) {
        this.branchReport = branchReport;
        this.branchReportService = branchReportService;
        this.branchReportConfigService = branchReportConfigService;
    }

    public void populateLoanDetails() throws BatchJobException {
        try {
            branchReport.addLoanDetails(branchReportService.extractLoanDetails(branchReport.getBranchId(),
                    branchReportConfigService.getCurrency()));
        } catch (ServiceException e) {
            throw new BatchJobException(e);
        }
    }

}

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

package org.mifos.reports.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.branchreport.BranchReportClientSummaryBO;
import org.mifos.reports.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.reports.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.reports.branchreport.BranchReportLoanDetailsBO;
import org.mifos.reports.branchreport.BranchReportStaffSummaryBO;
import org.mifos.reports.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.reports.branchreport.LoanArrearsAgingPeriod;
import org.mifos.reports.business.dto.BranchReportHeaderDTO;

public interface IBranchReportService {

    public BranchReportHeaderDTO getBranchReportHeaderDTO(Integer branchId, String runDate) throws ServiceException;

    public boolean isReportDataPresentForRundateAndBranchId(String branchId, String runDate);

    public boolean isReportDataPresentForRundate(Date runDate) throws ServiceException;

    public List<BranchReportLoanArrearsAgingBO> getLoanArrearsAgingInfo(Integer branchId, String runDate)
            throws ServiceException;

    public List<BranchReportClientSummaryBO> getClientSummaryInfo(Integer branchId, String runDate)
            throws ServiceException;

    public List<BranchReportStaffingLevelSummaryBO> getStaffingLevelSummary(Integer branchId, String runDate)
            throws ServiceException;

    public List<BranchReportStaffSummaryBO> getStaffSummary(Integer branchId, String runDate) throws ServiceException;

    public List<BranchReportLoanDetailsBO> getLoanDetails(Integer branchId, String runDate) throws ServiceException;

    public List<BranchReportLoanArrearsProfileBO> getLoanArrearsProfile(Integer branchId, String runDate)
            throws ServiceException;

    public void removeBranchReport(BranchReportBO branchReport) throws ServiceException;

    public List<BranchReportBO> getBranchReports(Date runDate) throws ServiceException;

    public BranchReportBO getBranchReport(Short branchId, Date runDate) throws ServiceException;

    public void removeBranchReports(List<BranchReportBO> branchReports) throws ServiceException;

    public BranchReportLoanArrearsAgingBO extractLoanArrearsAgingInfoInPeriod(Short officeId,
            LoanArrearsAgingPeriod loanArrearsAgingPeriod, MifosCurrency currency) throws ServiceException;

    public List<BranchReportStaffSummaryBO> extractBranchReportStaffSummary(Short officeId, Integer daysInArrears,
            MifosCurrency currency) throws ServiceException;

    public BigDecimal extractPortfolioAtRiskForOffice(OfficeBO office, Integer daysInArrears) throws ServiceException;

    public List<BranchReportStaffingLevelSummaryBO> extractBranchReportStaffingLevelSummaries(Short branchId)
            throws ServiceException;

    public List<BranchReportLoanDetailsBO> extractLoanDetails(Short branchId, MifosCurrency currency)
            throws ServiceException;

    public BranchReportLoanArrearsProfileBO extractLoansInArrearsCount(Short branchId, MifosCurrency currency,
            Integer daysInArrearsForRisk) throws ServiceException;

}

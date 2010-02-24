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

import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.mifos.reports.branchreport.BranchReportBO;
import org.mifos.reports.branchreport.BranchReportClientSummaryBO;
import org.mifos.reports.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.reports.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.reports.branchreport.BranchReportLoanDetailsBO;
import org.mifos.reports.branchreport.BranchReportStaffSummaryBO;
import org.mifos.reports.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.reports.branchreport.LoanArrearsAgingPeriod;
import org.mifos.reports.branchreport.persistence.BranchReportPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.reports.business.dto.BranchReportHeaderDTO;
import org.mifos.reports.util.helpers.ReportUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;

public class BranchReportService implements IBranchReportService {

    private OfficeBusinessService officeBusinessService;
    private PersonnelBusinessService personnelBusinessService;
    private BranchReportPersistence branchReportPersistence;

    public BranchReportService(OfficeBusinessService officeBusinessService,
            PersonnelBusinessService personnelBusinessService, BranchReportPersistence branchReportPersistence) {
        this.officeBusinessService = officeBusinessService;
        this.personnelBusinessService = personnelBusinessService;
        this.branchReportPersistence = branchReportPersistence;
    }

    public BranchReportService() {
        this(new OfficeBusinessService(), new PersonnelBusinessService(), new BranchReportPersistence());
    }

    public BranchReportHeaderDTO getBranchReportHeaderDTO(Integer branchId, String runDate) throws ServiceException {
        Short officeId = convertIntegerToShort(branchId);
        PersonnelBO branchManager = CollectionUtils.first(personnelBusinessService
                .getActiveBranchManagersUnderOffice(officeId));
        try {
            return new BranchReportHeaderDTO(officeBusinessService.getOffice(officeId), branchManager == null ? null
                    : branchManager.getDisplayName(), ReportUtils.parseReportDate(runDate));
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public boolean isReportDataPresentForRundateAndBranchId(String branchId, String runDate) {
        try {
            return getBranchReport(Short.valueOf(branchId), ReportUtils.parseReportDate(runDate)) != null;
        } catch (ServiceException e) {
            return false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isReportDataPresentForRundate(Date runDate) throws ServiceException {
        try {
            List<BranchReportBO> branchReports = branchReportPersistence.getBranchReport(runDate);
            return branchReports != null && !branchReports.isEmpty();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportLoanArrearsAgingBO> getLoanArrearsAgingInfo(Integer branchId, String runDate)
            throws ServiceException {
        try {
            return branchReportPersistence.getLoanArrearsAgingReport(convertIntegerToShort(branchId), ReportUtils
                    .parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportClientSummaryBO> getClientSummaryInfo(Integer branchId, String runDate)
            throws ServiceException {
        try {
            return branchReportPersistence.getBranchReportClientSummary(convertIntegerToShort(branchId), ReportUtils
                    .parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportStaffingLevelSummaryBO> getStaffingLevelSummary(Integer branchId, String runDate)
            throws ServiceException {
        try {
            List<BranchReportStaffingLevelSummaryBO> staffingLevelSummary = branchReportPersistence
                    .getBranchReportStaffingLevelSummary(convertIntegerToShort(branchId), ReportUtils
                            .parseReportDate(runDate));
            Collections.sort(staffingLevelSummary);
            return staffingLevelSummary;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportStaffSummaryBO> getStaffSummary(Integer branchId, String runDate) throws ServiceException {
        try {
            return branchReportPersistence.getBranchReportStaffSummary(convertIntegerToShort(branchId), ReportUtils
                    .parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportLoanDetailsBO> getLoanDetails(Integer branchId, String runDate) throws ServiceException {
        try {
            return branchReportPersistence.getLoanDetails(convertIntegerToShort(branchId), ReportUtils
                    .parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportLoanArrearsProfileBO> getLoanArrearsProfile(Integer branchId, String runDate)
            throws ServiceException {
        try {
            return branchReportPersistence.getLoanArrearsProfile(convertIntegerToShort(branchId), ReportUtils
                    .parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }

    }

    public void removeBranchReport(BranchReportBO branchReport) throws ServiceException {
        try {
            branchReportPersistence.delete(branchReport);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportBO> getBranchReports(Date runDate) throws ServiceException {
        try {
            return branchReportPersistence.getBranchReport(runDate);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public BranchReportBO getBranchReport(Short branchId, Date runDate) throws ServiceException {
        try {
            return CollectionUtils.first(branchReportPersistence.getBranchReport(branchId, runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public void removeBranchReports(List<BranchReportBO> branchReports) throws ServiceException {
        for (BranchReportBO branchReport : branchReports) {
            removeBranchReport(branchReport);
        }
    }

    public BranchReportLoanArrearsAgingBO extractLoanArrearsAgingInfoInPeriod(Short officeId,
            LoanArrearsAgingPeriod loanArrearsAgingPeriod, MifosCurrency currency) throws ServiceException {
        try {
            return branchReportPersistence.extractLoanArrearsAgingInfoInPeriod(loanArrearsAgingPeriod, officeId,
                    currency);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportStaffSummaryBO> extractBranchReportStaffSummary(Short officeId, Integer daysInArrears,
            MifosCurrency currency) throws ServiceException {
        try {
            return branchReportPersistence.extractBranchReportStaffSummary(officeId, daysInArrears, currency);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public BigDecimal extractPortfolioAtRiskForOffice(OfficeBO office, Integer daysInArrears) throws ServiceException {
        try {
            return branchReportPersistence.extractPortfolioAtRiskForOffice(office, daysInArrears);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchReportStaffingLevelSummaryBO> extractBranchReportStaffingLevelSummaries(Short branchId)
            throws ServiceException {
        try {
            return branchReportPersistence.extractBranchReportStaffingLevelSummary(branchId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public List<BranchReportLoanDetailsBO> extractLoanDetails(Short branchId, MifosCurrency currency)
            throws ServiceException {
        try {
            return branchReportPersistence.extractLoanDetails(branchId, currency);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public BranchReportLoanArrearsProfileBO extractLoansInArrearsCount(Short branchId, MifosCurrency currency,
            Integer daysInArrearsForRisk) throws ServiceException {
        try {
            return branchReportPersistence
                    .extractLoansArrearsProfileForBranch(branchId, currency, daysInArrearsForRisk);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

}

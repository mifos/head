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

package org.mifos.reports.business.service;

import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;
import static org.mifos.reports.util.helpers.ReportUtils.parseReportDate;

import java.text.ParseException;
import java.util.List;

import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.NumberUtils;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationReportHeader;
import org.mifos.reports.cashconfirmationreport.persistence.BranchCashConfirmationReportPersistence;

public class BranchCashConfirmationReportService implements IBranchCashConfirmationReportService {

    private BranchCashConfirmationReportPersistence branchCashConfirmationReportPersistence;
    private OfficeBusinessService officeBusinessService;

    public BranchCashConfirmationReportService(
            BranchCashConfirmationReportPersistence branchCashConfirmationReportPersistence,
            OfficeBusinessService officeBusinessService) {
        this.branchCashConfirmationReportPersistence = branchCashConfirmationReportPersistence;
        this.officeBusinessService = officeBusinessService;
    }

    // is called from CashConfirmationReport.rptdesign
    public BranchCashConfirmationReportHeader getReportHeader(Integer branchId, String runDate) throws ServiceException {
        try {
            return new BranchCashConfirmationReportHeader(officeBusinessService.getOffice(
                    NumberUtils.convertIntegerToShort(branchId)).getOfficeName(), parseReportDate(runDate));
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // is called from CashConfirmationReport.rptdesign
    public List<BranchCashConfirmationCenterRecoveryBO> getCenterRecoveries(Integer branchId, String runDate)
            throws ServiceException {
        try {
            return branchCashConfirmationReportPersistence.getCenterRecoveries(convertIntegerToShort(branchId),
                    parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // is called from CashConfirmationReport.rptdesign
    public List<BranchCashConfirmationInfoBO> getCenterIssues(Integer branchId, String runDate) throws ServiceException {
        try {
            return branchCashConfirmationReportPersistence.getCenterIssues(convertIntegerToShort(branchId),
                    parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // is called from CashConfirmationReport.rptdesign
    public List<BranchCashConfirmationDisbursementBO> getDisbursements(Integer branchId, String runDate)
            throws ServiceException {
        try {
            return branchCashConfirmationReportPersistence.getDisbursements(convertIntegerToShort(branchId),
                    parseReportDate(runDate));
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

    // is used in validate so not sure is it invoked from .rptdesign reports
    public boolean isReportDataPresentForRundateAndBranchId(String branchId, String runDate) {
        try {
            return CollectionUtils
                    .first(branchCashConfirmationReportPersistence.getBranchCashConfirmationReportsForDateAndBranch(
                            Short.valueOf(branchId), parseReportDate(runDate))) != null;
        } catch (PersistenceException e) {
            return false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
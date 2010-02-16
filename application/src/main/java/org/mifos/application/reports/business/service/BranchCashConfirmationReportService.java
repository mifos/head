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

package org.mifos.application.reports.business.service;

import static org.mifos.application.reports.util.helpers.ReportUtils.parseReportDate;
import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportHeader;
import org.mifos.application.cashconfirmationreport.persistence.BranchCashConfirmationReportPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.NumberUtils;

public class BranchCashConfirmationReportService implements IBranchCashConfirmationReportService {

    BranchCashConfirmationReportPersistence branchCashConfirmationReportPersistence;
    private OfficeBusinessService officeBusinessService;

    public BranchCashConfirmationReportService(
            BranchCashConfirmationReportPersistence branchCashConfirmationReportPersistence,
            OfficeBusinessService officeBusinessService) {
        this.branchCashConfirmationReportPersistence = branchCashConfirmationReportPersistence;
        this.officeBusinessService = officeBusinessService;
    }

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

    public List<BranchCashConfirmationReportBO> extractBranchCashConfirmationReport(Date actionDate,
            AccountTypes accountType, List<Short> prdOfferingsForRecoveries, List<Short> prdOfferingsForIssues,
            List<Short> prdOfferingsForDisbursements, MifosCurrency currency, Date runDate) throws ServiceException {
        try {
            return branchCashConfirmationReportPersistence.extractBranchCashConfirmationReport(actionDate, accountType,
                    prdOfferingsForRecoveries, prdOfferingsForIssues, prdOfferingsForDisbursements, currency, runDate);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<BranchCashConfirmationReportBO> getBranchCashConfirmationReportsForDate(Date runDate)
            throws ServiceException {
        try {
            return branchCashConfirmationReportPersistence.getBranchCashConfirmationReportsForDate(runDate);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public void deleteBranchCashConfirmationReports(List<BranchCashConfirmationReportBO> reports)
            throws ServiceException {
        try {
            for (BranchCashConfirmationReportBO report : reports)
                branchCashConfirmationReportPersistence.delete(report);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

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

    public BranchCashConfirmationReportHeader getReportHeader(Integer branchId, String runDate) throws ServiceException {
        try {
            return new BranchCashConfirmationReportHeader(officeBusinessService.getOffice(
                    NumberUtils.convertIntegerToShort(branchId)).getOfficeName(), parseReportDate(runDate));
        } catch (ParseException e) {
            throw new ServiceException(e);
        }
    }

}

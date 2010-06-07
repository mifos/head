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

package org.mifos.reports.cashconfirmationreport.persistence;

import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS;
import static org.mifos.customers.util.helpers.QueryParamConstants.BRANCH_ID;
import static org.mifos.customers.util.helpers.QueryParamConstants.RUN_DATE;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.reports.cashconfirmationreport.BranchCashConfirmationReportBO;

public class BranchCashConfirmationReportPersistence extends Persistence {

    public BranchCashConfirmationReportPersistence() {
        super();
    }

    @SuppressWarnings("unchecked")
    public List<BranchCashConfirmationCenterRecoveryBO> getCenterRecoveries(Short branchId, Date runDate)
            throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES, populateParams(branchId, runDate));
    }

    @SuppressWarnings("unchecked")
    public List<BranchCashConfirmationInfoBO> getCenterIssues(Short branchId, Date runDate) throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES, populateParams(branchId, runDate));
    }

    @SuppressWarnings("unchecked")
    public List<BranchCashConfirmationReportBO> getBranchCashConfirmationReportsForDateAndBranch(Short branchId,
            Date runDate) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(BRANCH_ID, branchId);
        params.put(RUN_DATE, runDate);
        return executeNamedQuery(NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH, params);
    }

    @SuppressWarnings("unchecked")
    public List<BranchCashConfirmationDisbursementBO> getDisbursements(Short branchId, Date runDate)
            throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS, populateParams(branchId, runDate));
    }

    private HashMap<String, Object> populateParams(Short branchId, Date runDate) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(BRANCH_ID, branchId);
        params.put(RUN_DATE, runDate);
        return params;
    }
}
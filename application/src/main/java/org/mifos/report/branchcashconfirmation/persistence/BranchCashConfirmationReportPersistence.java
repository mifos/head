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

package org.mifos.report.branchcashconfirmation.persistence;

import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE;
import static org.mifos.application.customer.util.helpers.CustomerSearchConstants.OFFICELEVELID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.ACCOUNT_TYPE_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.ACTION_DATE;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.BRANCH_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.CURRENCY_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.DISBURSEMENT_DATE;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.PRODUCT_OFFERING_IDS;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.RUN_DATE;
import static org.mifos.framework.util.helpers.MoneyFactory.createMoney;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationCenterRecoveryBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class BranchCashConfirmationReportPersistence extends Persistence {
    private PrdOfferingPersistence prdOfferingPersistence;

    public BranchCashConfirmationReportPersistence(PrdOfferingPersistence prdOfferingPersistence) {
        super();
        this.prdOfferingPersistence = prdOfferingPersistence;
    }

    public BranchCashConfirmationReportPersistence() {
        this(new PrdOfferingPersistence());
    }

    public List<BranchCashConfirmationReportBO> extractBranchCashConfirmationReport(Date actionDate,
            AccountTypes accountType, List<Short> prdOfferingIdsForRecovery, List<Short> prdOfferingIdsForIssue,
            List<Short> prdOfferingsForDisbursements, MifosCurrency currency, Date runDate) throws PersistenceException {

        List<String> prdOfferingNamesForIssue = fetchPrdOfferingForIssue(prdOfferingIdsForIssue);
        Query query = createdNamedQuery(EXTRACT_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES);
        populateQueryParams(query, actionDate, accountType, prdOfferingIdsForRecovery, currency);
        List<Object[]> resultSet = runQuery(query);
        if (resultSet == null)
            return new ArrayList<BranchCashConfirmationReportBO>();

        HashMap<Short, BranchCashConfirmationReportBO> reportMap = new HashMap<Short, BranchCashConfirmationReportBO>();
        for (Object[] result : resultSet) {
            Short officeId = (Short) result[0];
            fetchReportBO(reportMap, officeId, runDate, prdOfferingNamesForIssue, currency).addCenterRecovery(
                    createCenterRecoveryFromResult(currency, result));
        }

        List<Object[]> disbursements = extractDisbursements(currency, accountType, prdOfferingsForDisbursements,
                actionDate);
        for (Object[] disbursement : disbursements) {
            Short officeId = (Short) disbursement[0];
            fetchReportBO(reportMap, officeId, runDate, prdOfferingNamesForIssue, currency).addDisbursement(
                    createDisbursementFromResult(currency, disbursement));
        }
        return new ArrayList<BranchCashConfirmationReportBO>(reportMap.values());
    }

    private BranchCashConfirmationDisbursementBO createDisbursementFromResult(MifosCurrency currency,
            Object[] disbursement) {
        return new BranchCashConfirmationDisbursementBO((String) disbursement[1], createMoney(currency,
                (BigDecimal) disbursement[2]));
    }

    private BranchCashConfirmationCenterRecoveryBO createCenterRecoveryFromResult(MifosCurrency currency,
            Object[] result) {
        return new BranchCashConfirmationCenterRecoveryBO((String) result[1], createMoney(currency,
                (BigDecimal) result[2]), createMoney(currency, (BigDecimal) result[3]), createMoney(currency,
                (BigDecimal) result[4]));
    }

    private BranchCashConfirmationReportBO fetchReportBO(HashMap<Short, BranchCashConfirmationReportBO> reportMap,
            Short officeId, Date runDate, List<String> prdOfferingForIssue, MifosCurrency currency) {
        if (!reportMap.containsKey(officeId)) {
            BranchCashConfirmationReportBO reportBO = new BranchCashConfirmationReportBO(officeId, runDate);
            reportBO.addCenterIssues(BranchCashConfirmationInfoBO.createIssuesBO(prdOfferingForIssue, currency));
            reportMap.put(officeId, reportBO);
        }
        return reportMap.get(officeId);
    }

    private void populateQueryParams(Query query, Date actionDate, AccountTypes accountType,
            List<Short> productOfferings, MifosCurrency currency) throws PersistenceException {
        HashMap<String, Object> queryParams = new HashMap<String, Object>();
        populateExtractParams(queryParams, currency, accountType);
        queryParams.put(ACTION_DATE, actionDate);
        setParametersInQuery(query, EXTRACT_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES, queryParams);
        query.setParameterList(PRODUCT_OFFERING_IDS, productOfferings);
    }

    public List<BranchCashConfirmationCenterRecoveryBO> getCenterRecoveries(Short branchId, Date runDate)
            throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_CENTER_RECOVERIES, populateParams(branchId, runDate));
    }

    public List<BranchCashConfirmationInfoBO> getCenterIssues(Short branchId, Date runDate) throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_CENTER_ISSUES, populateParams(branchId, runDate));
    }

    public List<BranchCashConfirmationReportBO> getBranchCashConfirmationReportsForDate(Date runDate)
            throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(RUN_DATE, runDate);
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE, params);
    }

    public List<BranchCashConfirmationReportBO> getBranchCashConfirmationReportsForDateAndBranch(Short branchId,
            Date runDate) throws PersistenceException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(BRANCH_ID, branchId);
        params.put(RUN_DATE, runDate);
        return executeNamedQuery(NamedQueryConstants.GET_BRANCH_CASH_CONFIRMATION_REPORT_FOR_DATE_AND_BRANCH, params);
    }

    private HashMap<String, Object> populateParams(Short branchId, Date runDate) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(BRANCH_ID, branchId);
        params.put(RUN_DATE, runDate);
        return params;
    }

    private List<String> fetchPrdOfferingForIssue(List<Short> prdOfferingsForIssues) throws PersistenceException {
        List<String> prdOfferingNamesForIssue = new ArrayList<String>();
        for (Short prdOffering : prdOfferingsForIssues) {
            prdOfferingNamesForIssue.add(prdOfferingPersistence.getLoanPrdOffering(prdOffering).getPrdOfferingName());
        }
        return prdOfferingNamesForIssue;
    }

    List<Object[]> extractDisbursements(MifosCurrency currency, AccountTypes accountType,
            List<Short> disbursementProductOfferingIds, Date disbursementDate) throws PersistenceException {
        Query query = createdNamedQuery(EXTRACT_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS);
        HashMap<String, Object> params = new HashMap<String, Object>();
        populateExtractParams(params, currency, accountType);
        params.put(DISBURSEMENT_DATE, disbursementDate);
        setParametersInQuery(query, EXTRACT_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS, params);
        query.setParameterList(PRODUCT_OFFERING_IDS, disbursementProductOfferingIds);
        return runQuery(query);
    }

    private void populateExtractParams(HashMap<String, Object> params, MifosCurrency currency, AccountTypes accountType) {
        params.put(ACCOUNT_TYPE_ID, accountType.getValue());
        params.put(CURRENCY_ID, currency.getCurrencyId());
        params.put(OFFICELEVELID, OfficeLevel.BRANCHOFFICE.getValue());
    }

    public List<BranchCashConfirmationDisbursementBO> getDisbursements(Short branchId, Date runDate)
            throws PersistenceException {
        return executeNamedQuery(GET_BRANCH_CASH_CONFIRMATION_DISBURSEMENTS, populateParams(branchId, runDate));
    }

}

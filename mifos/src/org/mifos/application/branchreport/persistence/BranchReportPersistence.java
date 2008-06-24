/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.branchreport.persistence;

import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOAN_DETAILS;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR;
import static org.mifos.application.NamedQueryConstants.EXTRACT_BRANCH_REPORT_TOTAL_STAFFING_LEVEL_SUMMARY;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_FOR_DATE;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH;
import static org.mifos.application.NamedQueryConstants.GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH;
import static org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO.TOTAL_STAFF_ROLE_ID;
import static org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO.TOTAL_STAFF_ROLE_NAME;
import static org.mifos.application.customer.util.helpers.CustomerLevel.CLIENT;
import static org.mifos.application.customer.util.helpers.CustomerSearchConstants.OFFICEID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.BRANCH_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.CURRENCY_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.CUSTOMER_LEVEL_ID;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.DAYS_IN_ARREARS;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.LOAN_ACTIVE_IN_BAD_STANDING;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.LOAN_ACTIVE_IN_GOOD_STANDING;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.MAX_DAYS;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.MIN_DAYS;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.NOT_LESS_THAN_DAYS;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.RUN_DATE;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.TOTAL_STAFF_ROLE_ID_PARAM;
import static org.mifos.application.customer.util.helpers.QueryParamConstants.TOTAL_STAFF_ROLE_NAME_PARAM;
import static org.mifos.framework.util.helpers.MoneyFactory.createMoney;
import static org.mifos.framework.util.helpers.MoneyFactory.zero;
import static org.mifos.framework.util.helpers.NumberUtils.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportClientSummaryBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsAgingBO;
import org.mifos.application.branchreport.BranchReportLoanArrearsProfileBO;
import org.mifos.application.branchreport.BranchReportLoanDetailsBO;
import org.mifos.application.branchreport.BranchReportStaffSummaryBO;
import org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.application.branchreport.LoanArrearsAgingPeriod;
import org.mifos.application.customer.util.helpers.QueryParamConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.NumberUtils;

// Takes care of classes BranchReportBatchBO, BranchReportClientSummaryBatchBO, BranchReportLoanArrearsAgingBatchBO
public class BranchReportPersistence extends Persistence {

	public BranchReportPersistence() {
	}

	public List<BranchReportBO> getBranchReport(Date runDate)
			throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(RUN_DATE, runDate);
		return executeNamedQuery(GET_BRANCH_REPORT_FOR_DATE, params);
	}

	public List<BranchReportBO> getBranchReport(Short branchId, Date runDate)
			throws PersistenceException {
		return executeNamedQuery(GET_BRANCH_REPORT_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportClientSummaryBO> getBranchReportClientSummary(
			Short branchId, Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_CLIENT_SUMMARY_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportLoanArrearsAgingBO> getLoanArrearsAgingReport(
			Short branchId, Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_LOAN_ARREARS_AGING_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportLoanArrearsProfileBO> getLoanArrearsProfile(
			Short branchId, Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_LOAN_ARREARS_PROFILE_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportStaffSummaryBO> getBranchReportStaffSummary(
			Short branchId, Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_STAFF_SUMMARY_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportStaffingLevelSummaryBO> getBranchReportStaffingLevelSummary(
			Short branchId, Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public List<BranchReportLoanDetailsBO> getLoanDetails(Short branchId,
			Date runDate) throws PersistenceException {
		return executeNamedQuery(
				GET_BRANCH_REPORT_LOAN_DETAILS_FOR_DATE_AND_BRANCH,
				populateQueryParams(branchId, runDate));
	}

	public BranchReportLoanArrearsAgingBO extractLoanArrearsAgingInfoInPeriod(
			LoanArrearsAgingPeriod period, Short branchId,
			MifosCurrency currency) throws PersistenceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(OFFICEID, branchId);
		params.put(MAX_DAYS, period.getMaxDays());
		params.put(MIN_DAYS, period.getMinDays());
		params.put(NOT_LESS_THAN_DAYS, period.getNotLessThanDays());
		params.put(CURRENCY_ID, currency.getCurrencyId());
		params.put(CUSTOMER_LEVEL_ID, CLIENT.getValue());
		List queryResult = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_LOAN_ARREARS_IN_PERIOD, params);
		if (queryResult.isEmpty())
			return new BranchReportLoanArrearsAgingBO(period, ZERO, ZERO,
					zero(currency), zero(currency), zero(currency));
		Object[] resultSet = (Object[]) queryResult.get(0);
		return new BranchReportLoanArrearsAgingBO(period,
				(Integer) resultSet[0], (Integer) resultSet[1], createMoney(
						currency, (BigDecimal) resultSet[2]), createMoney(
						currency, (BigDecimal) resultSet[3]), createMoney(
						currency, (BigDecimal) resultSet[4]));
	}

	public List<BranchReportStaffSummaryBO> extractBranchReportStaffSummary(
			Short branchId, Integer daysInArrears, MifosCurrency currency)
			throws PersistenceException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(OFFICEID, branchId);
		List<Object[]> queryResults = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_ACTIVE_BORROWERS_LOANS,
				params);
		Map<Short, BranchReportStaffSummaryBO> staffSummaries = new HashMap<Short, BranchReportStaffSummaryBO>();
		for (Object[] queryResult : queryResults) {
			BranchReportStaffSummaryBO staffSummary = createStaffSummaryFromResultset(
					queryResult, currency);
			staffSummaries.put(staffSummary.getPersonnelId(), staffSummary);
		}
		populateCustomerCounts(staffSummaries, params);
		populateTotalClientsEnrolledByPersonnel(staffSummaries);
		populateClientsEnrolledByPersonnelThisMonth(staffSummaries);
		populateOutstandingAmounts(staffSummaries, branchId, currency);
		params.put(DAYS_IN_ARREARS, daysInArrears);
		populatePortfolioAtRiskPercentage(staffSummaries, params);
		populateLoanArrearsAmountForPersonnel(staffSummaries, currency);
		return new ArrayList<BranchReportStaffSummaryBO>(staffSummaries
				.values());
	}

	void populateTotalClientsEnrolledByPersonnel(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
		//		MySql can't handle empty list in "IN clause"
		//		See http://opensource.atlassian.com/projects/hibernate/browse/HHH-2045
		if (staffSummaries.isEmpty())
			return;
		Query query = populateQueryWithPersonnelIds(
				NamedQueryConstants.EXTRACT_BRANCH_REPORT_TOTAL_CLIENTS_ENROLLED_BY_PERSONNEL,
				staffSummaries);
		runStaffSummaryQueryClosure(query,
				new TotalClientsFormedByPersonnelClosure(staffSummaries));
	}

	void populateClientsEnrolledByPersonnelThisMonth(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
		//		MySql can't handle empty list in "IN clause"
		//		See http://opensource.atlassian.com/projects/hibernate/browse/HHH-2045		
		if (staffSummaries.isEmpty())
			return;
		Query query = populateQueryWithPersonnelIds(
				NamedQueryConstants.EXTRACT_BRANCH_REPORT_CLIENTS_ENROLLED_BY_PERSONNEL_THIS_MONTH,
				staffSummaries);
		runStaffSummaryQueryClosure(query,
				new ClientsFormedByPersonnelThisMonthClosure(staffSummaries));
	}

	void populateLoanArrearsAmountForPersonnel(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries, MifosCurrency currency) {
		//		MySql can't handle empty list in "IN clause"
		//		See http://opensource.atlassian.com/projects/hibernate/browse/HHH-2045		
		if (staffSummaries.isEmpty())
			return;
		Query query = populateQueryWithPersonnelIds(
				NamedQueryConstants.EXTRACT_BRANCH_REPORT_LOAN_ARREARS_AMOUNT_FOR_PERSONNEL,
				staffSummaries);
		runStaffSummaryQueryClosure(query, new LoanArrearsAmountForPersonnel(
				staffSummaries, currency));
	}	

	private void runStaffSummaryQueryClosure(Query query, Closure closure) {
		List<Object[]> resultSet = runQuery(query);
		for (Object[] result : resultSet) {
			closure.execute(result);
		}
	}

	private Query populateQueryWithPersonnelIds(String queryString,
			Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
		Query query = createdNamedQuery(queryString);
		query.setParameterList(QueryParamConstants.PERSONNEL_ID_LIST,
				new ArrayList<Short>(staffSummaries.keySet()));
		return query;
	}

	public BigDecimal extractPortfolioAtRiskForOffice(OfficeBO office,
			Integer daysInArrears) throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(OFFICEID, office.getOfficeId());
		params.put(DAYS_IN_ARREARS, daysInArrears);
		params.put(LOAN_ACTIVE_IN_GOOD_STANDING,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING.getValue());
		params.put(LOAN_ACTIVE_IN_BAD_STANDING,
				AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue());
		params.put(CUSTOMER_LEVEL_ID, CLIENT.getValue());
		return getCalculateValueFromQueryResult(executeNamedQuery(
				EXTRACT_BRANCH_REPORT_CLIENT_SUMMARY_PAR, params));
	}

	public List<BranchReportStaffingLevelSummaryBO> extractBranchReportStaffingLevelSummary(
			Short branchId) throws PersistenceException {
		List<BranchReportStaffingLevelSummaryBO> staffingLevelSummaries = new ArrayList<BranchReportStaffingLevelSummaryBO>();
		List<Object[]> resultSet = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_STAFFING_LEVEL_SUMMARY,
				populateQueryParams(branchId));
		HashMap<String, Object> params = populateQueryParams(branchId);
		params.put(TOTAL_STAFF_ROLE_ID_PARAM, TOTAL_STAFF_ROLE_ID);
		params.put(TOTAL_STAFF_ROLE_NAME_PARAM, TOTAL_STAFF_ROLE_NAME);
		resultSet.addAll(executeNamedQuery(
				EXTRACT_BRANCH_REPORT_TOTAL_STAFFING_LEVEL_SUMMARY, params));
		for (Object[] result : resultSet) {
			staffingLevelSummaries.add(new BranchReportStaffingLevelSummaryBO(
					(Integer) result[0], (String) result[1],
					(Integer) result[2]));
		}
		return staffingLevelSummaries;
	}

	public List<BranchReportLoanDetailsBO> extractLoanDetails(Short branchId,
			MifosCurrency currency) throws PersistenceException {
		List<Object[]> resultSet = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_LOAN_DETAILS,
				populateQueryParamsWithBranchCurrencyAndCustomerLevel(branchId, currency));
		ArrayList<BranchReportLoanDetailsBO> loanDetails = new ArrayList<BranchReportLoanDetailsBO>();
		for (Object[] result : resultSet) {
			loanDetails.add(new BranchReportLoanDetailsBO((String) result[0],
					(Integer) result[1], createMoney(currency,
							(BigDecimal) result[2]), createMoney(currency,
							(BigDecimal) result[3]), (Integer) result[4],
					createMoney(currency, (BigDecimal) result[5]), createMoney(
							currency, (BigDecimal) result[6])));
		}
		return loanDetails;
	}

	public BranchReportLoanArrearsProfileBO extractLoansArrearsProfileForBranch(
			Short branchId, MifosCurrency currency, Integer daysInArrearsForRisk)
			throws PersistenceException {

		List<Object[]> riskListResult = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_LOANS_AND_OUTSTANDING_AMOUNTS_AT_RISK,
				populateQueryParamsWithBranchCurrencyCustomerLevelAndRiskDays(branchId,
						currency, daysInArrearsForRisk));
		LoanArrearsProfileForLoansAtRisk profileForLoansAtRisk = riskListResult
				.isEmpty() ? new LoanArrearsProfileForLoansAtRisk(currency)
				: new LoanArrearsProfileForLoansAtRisk(riskListResult.get(0),
						currency);

		List<Object[]> resultListForBranch = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_LOANS_IN_ARREARS,
				populateQueryParamsWithBranchCurrencyAndCustomerLevel(branchId, currency));

		LoanArrearsProfileForBranch resultForBranch = resultListForBranch
				.isEmpty() ? new LoanArrearsProfileForBranch(currency)
				: new LoanArrearsProfileForBranch(resultListForBranch.get(0),
						currency);

		Integer clientsAtRisk = getCountFromQueryResult(executeNamedQuery(
				EXTRACT_BRANCH_REPORT_LOAN_PROFILE_CLIENTS_AT_RISK,
				populateQueryParams(branchId)));

		BranchReportLoanArrearsProfileBO loanArrearProfileForBranch = new BranchReportLoanArrearsProfileBO(
				resultForBranch.loansInArrears,
				resultForBranch.clientsInArrears,
				resultForBranch.overDueBalance, resultForBranch.unpaidBalance,
				profileForLoansAtRisk.loansAtRisk,
				profileForLoansAtRisk.outstandingAmountAtRisk,
				profileForLoansAtRisk.overdueAmountAtRisk, clientsAtRisk);
		return loanArrearProfileForBranch;
	}

	private Map populateQueryParamsWithBranchCurrencyAndCustomerLevel(Short branchId, MifosCurrency currency) {
		Map params = populateQueryParamsWithBranchAndCurrency(branchId, currency);
		params.put(CUSTOMER_LEVEL_ID, CLIENT.getValue());
		return params;
	}

	private void populatePortfolioAtRiskPercentage(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries,
			Map<String, Object> params) throws PersistenceException {
		List<Object[]> resultSet = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_PAR, params);
		for (Object[] result : resultSet) {
			BranchReportStaffSummaryBO staffSummary = staffSummaries
					.get(result[0]);
			staffSummary.setPortfolioAtRisk((BigDecimal) result[1]);
		}
	}

	void populateOutstandingAmounts(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries,
			Short branchId, MifosCurrency currency) throws PersistenceException {
		List<Object[]> resultSet = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_LOAN_AMOUNT_OUTSTANDING,
				populateQueryParamsWithBranchAndCurrency(branchId, currency));
		for (Object[] outstandingAmounts : resultSet) {
			BranchReportStaffSummaryBO staffSummary = staffSummaries
					.get(outstandingAmounts[0]);
			staffSummary.setLoanAmountOutstanding(createMoney(currency,
					(BigDecimal) outstandingAmounts[1]));
			staffSummary.setInterestAndFeesAmountOutstanding(createMoney(
					currency, (BigDecimal) outstandingAmounts[2]));
		}
	}

	private void populateCustomerCounts(
			Map<Short, BranchReportStaffSummaryBO> staffSummaries,
			Map<String, Object> params) throws PersistenceException {
		List<Object[]> loanOfficerCenterAndClientCountResultset = executeNamedQuery(
				EXTRACT_BRANCH_REPORT_STAFF_SUMMARY_CENTER_AND_CLIENT_COUNT,
				params);

		for (Object[] loanOfficerCenterAndClientCount : loanOfficerCenterAndClientCountResultset) {
			BranchReportStaffSummaryBO staffSummary = staffSummaries
					.get(loanOfficerCenterAndClientCount[0]);
			staffSummary
					.setCenterCount((Integer) loanOfficerCenterAndClientCount[1]);
			staffSummary
					.setClientCount((Integer) loanOfficerCenterAndClientCount[2]);
		}
	}

	private BranchReportStaffSummaryBO createStaffSummaryFromResultset(
			Object[] resultSet, MifosCurrency currency) {
		return new BranchReportStaffSummaryBO((Short) resultSet[0],
				(String) resultSet[1], (Date) resultSet[2],
				(Integer) resultSet[3], (Integer) resultSet[4],
				NumberUtils.ZERO, NumberUtils.ZERO, 
				zero(currency), zero(currency), BigDecimal.ZERO, Integer
						.valueOf(0), Integer.valueOf(0), zero(currency));
	}

	private Map<String, Object> populateQueryParams(Short branchId, Date runDate) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(RUN_DATE, runDate);
		params.put(BRANCH_ID, branchId);
		return params;
	}

	private HashMap<String, Object> populateQueryParams(Short branchId) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(OFFICEID, branchId);
		return params;
	}

	private Map populateQueryParamsWithBranchAndCurrency(Short branchId,
			MifosCurrency currency) {
		HashMap<String, Object> params = populateQueryParams(branchId);
		params.put(CURRENCY_ID, currency.getCurrencyId());
		return params;
	}

	private Map<String, Object> populateQueryParamsWithBranchCurrencyCustomerLevelAndRiskDays(
			Short branchId, MifosCurrency currency, Integer daysInArrearsForRisk) {
		Map<String, Object> params = populateQueryParamsWithBranchCurrencyAndCustomerLevel(
				branchId, currency);
		params.put(QueryParamConstants.DAYS_IN_ARREARS, daysInArrearsForRisk);
		return params;
	}

	private static class LoanArrearsProfileForBranch {
		private Integer loansInArrears;
		private Integer clientsInArrears;
		/**
		 * Maps to Amount in Arrears in Report
		 */
		private Money overDueBalance;
		/**
		 * Maps to Amount Outstanding In Arrears in Report
		 */
		private Money unpaidBalance;

		public LoanArrearsProfileForBranch(Object[] resultForBranch,
				MifosCurrency currency) {
			loansInArrears = (Integer) resultForBranch[0];
			clientsInArrears = (Integer) resultForBranch[1];
			overDueBalance = createMoney(currency,
					(BigDecimal) resultForBranch[2]);
			unpaidBalance = createMoney(currency,
					(BigDecimal) resultForBranch[3]);
		}

		public LoanArrearsProfileForBranch(MifosCurrency currency) {
			this(new Object[] { ZERO, ZERO, BigDecimal.ZERO, BigDecimal.ZERO },
					currency);
		}
	}

	private static class LoanArrearsProfileForLoansAtRisk {
		private Integer loansAtRisk;
		private Money outstandingAmountAtRisk;
		private Money overdueAmountAtRisk;

		public LoanArrearsProfileForLoansAtRisk(MifosCurrency currency) {
			this(new Object[] { ZERO, BigDecimal.ZERO, BigDecimal.ZERO },
					currency);
		}

		public LoanArrearsProfileForLoansAtRisk(Object[] resultSet,
				MifosCurrency currency) {
			loansAtRisk = (Integer) resultSet[0];
			outstandingAmountAtRisk = createMoney(currency,
					(BigDecimal) resultSet[1]);
			overdueAmountAtRisk = createMoney(currency,
					(BigDecimal) resultSet[2]);
		}
	}
	
	private static class TotalClientsFormedByPersonnelClosure extends
			ResultSetClosureForPersonnel {

		TotalClientsFormedByPersonnelClosure(
				Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
			super(staffSummaries);
		}

		public void execute(Object arg) {
			Object[] result = (Object[]) arg;
			staffSummaries.get(result[0]).setTotalClientsEnrolled(
					(Integer) result[1]);
		}
	}

	private static class ClientsFormedByPersonnelThisMonthClosure extends
			ResultSetClosureForPersonnel {

		ClientsFormedByPersonnelThisMonthClosure(
				Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
			super(staffSummaries);
		}

		public void execute(Object arg) {
			Object[] result = (Object[]) arg;
			staffSummaries.get(result[0]).setClientsEnrolledThisMonth(
					(Integer) result[1]);
		}
	}

	private static class LoanArrearsAmountForPersonnel extends
			ResultSetClosureForPersonnel {

		private final MifosCurrency currency;

		LoanArrearsAmountForPersonnel(
				Map<Short, BranchReportStaffSummaryBO> staffSummaries, MifosCurrency currency) {
			super(staffSummaries);
			this.currency = currency;
		}

		public void execute(Object arg) {
			Object[] result = (Object[]) arg;
			staffSummaries.get(result[0]).setLoanArrearsAmount(
					createMoney(currency,
										(BigDecimal) result[1])
					);
		}
	}

	private static abstract class ResultSetClosureForPersonnel implements
			Closure {
		protected final Map<Short, BranchReportStaffSummaryBO> staffSummaries;

		ResultSetClosureForPersonnel(
				Map<Short, BranchReportStaffSummaryBO> staffSummaries) {
			this.staffSummaries = staffSummaries;
		}
	}
}

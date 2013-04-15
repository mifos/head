/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.accounting.persistence;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.mifos.application.accounting.business.CoaBranchBO;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.accounting.business.ProcessUpdateBo;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.CoaNamesDto;
import org.mifos.dto.domain.DynamicOfficeDto;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlDetailDto;
import org.mifos.dto.domain.GlobalOfficeNumDto;
import org.mifos.dto.domain.MisProcessingTransactionsDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.RolesActivityDto;
import org.mifos.dto.domain.RowCount;
import org.mifos.dto.domain.ViewStageTransactionsDto;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.DateUtils;

public class AccountingDaoHibernate extends LegacyGenericDao implements
		AccountingDao {

	@Override
	@SuppressWarnings("unchecked")
	public List<GLCodeDto> findMainAccountCashGlCodes() {

		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.Cash", emptyqueryparameters,
				GLCodeDto.class);
		return cashGlCodes;
	}

	public List<GlBalancesBO> findExistedGlBalacesBOs(Integer officeLevelId,
			String officeId, String glCodeValue, Integer financialYearId) {
		List<GlBalancesBO> balancesBOs = null;
		Query query = createdNamedQuery("getExistedGlBalancesBO");
		query.setInteger("OFFICE_LEVEL", officeLevelId);
		query.setString("OFFICE_ID", officeId);
		query.setString("GLCODEVALUE", glCodeValue);
		query.setInteger("FINANCIALYEARID", financialYearId);
		balancesBOs = query.list();
		return balancesBOs;
	}

	public List<RolesActivityDto> findrolesActivity(int activityid) {
		final Map<String, Integer> queryparameters = new HashMap<String, Integer>();
		queryparameters.put("ACTIVITYID", activityid);
		final List<RolesActivityDto> rolesactivity = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.RolesActivityId", queryparameters,
				RolesActivityDto.class);
		return rolesactivity;
	}

	public List<GLCodeDto> findMainAccountHeadGlCodes(String glname) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("GLNAME", glname);
		final List<GLCodeDto> glcodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.mainaccountheadglcodes",
				queryparameters, GLCodeDto.class);
		return glcodes;
	}

	public List<GlobalOfficeNumDto> findGlobalDiplayNumandname(String officename) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICENAME", officename);
		final List<GlobalOfficeNumDto> glnum = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.globalnum", queryparameters,
				GlobalOfficeNumDto.class);
		return glnum;
	}

	@Override
	public List<GLCodeDto> findDebitAccounts() {

		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.DebitAccountGlCodes",
				emptyqueryparameters, GLCodeDto.class);
		return cashGlCodes;
	}

	@Override
	public List<GLCodeDto> findCreditAccounts(String glCode) {

		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("Gl_Code", glCode);
		final List<GLCodeDto> cashGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.CreditAccountGlCodes",
				queryparameters, GLCodeDto.class);
		return cashGlCodes;
	}

	@Override
	public List<GLCodeDto> findMainAccountBankGlCodes() {
		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> bankGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.Bank", emptyqueryparameters,
				GLCodeDto.class);
		return bankGlCodes;
	}

	@Override
	public List<GLCodeDto> findAccountHeadGlCodes(String glCode) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("Gl_Code", glCode);
		final List<GLCodeDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.AccountHeadGlCodes", queryparameters,
				GLCodeDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<GLCodeDto> findCoaBranchAccountHeadGlCodes() {
		final Map<String, Object> queryparametersempty = new HashMap<String, Object>();

		final List<GLCodeDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.coaBranchAccountHeadGlCodes",queryparametersempty,
				GLCodeDto.class);
		return accountHeadGlCodes;
	}
	@Override
	public List<DynamicOfficeDto> getListOfOffices(String officeId,
			String officeLevelId) {

		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICE_ID", officeId);
		queryparameters.put("OFFICE_LEVEL_ID", officeLevelId);
		final List<DynamicOfficeDto> listOfOffices = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.getListOfOffices", queryparameters,
				DynamicOfficeDto.class);
		return listOfOffices;

	}

	@Override
	public boolean savingGeneralLedgerTransaction(GlMasterBO glMasterBO) {
		boolean result = false;
		try {
			GlMasterBO o = (GlMasterBO) save(glMasterBO);
			if (o.getTransactionMasterId() > 0)
				result = true;

		} catch (Exception e) {
			throw new MifosRuntimeException(e);
		}
		return result;
	}

	@Override
	public FinancialYearBO savingFinancialYearBO(FinancialYearBO financialYearBO) {
		FinancialYearBO bo = null;
		try {
			bo = (FinancialYearBO) save(financialYearBO);
		} catch (Exception e) {
			throw new MifosRuntimeException(e);
		}
		return bo;
	}

	@Override
	public boolean savingOpenBalancesTransaction(GlBalancesBO balancesBO) {
		boolean result = false;
		try {
			GlBalancesBO o = (GlBalancesBO) save(balancesBO);
			if (o.getGlBalancesId() > 0)
				result = true;
		} catch (Exception e) {
			throw new MifosRuntimeException(e);
		}
		return result;
	}

	@Override
	public <T extends Object> T getPersistedObject(final Class<T> clazz,
			final Serializable persistentObjectId) throws PersistenceException {
		return getPersistentObject(clazz, persistentObjectId);
	}


	@Override
	public List<GlBalancesBO> getResultantGlBalancesBO(GlBalancesBO glBalancesBO) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICEID", glBalancesBO.getOfficeId());
		queryparameters.put("OFFICELEVEL", glBalancesBO.getOfficeLevel());
		queryparameters.put("COAID", glBalancesBO.getGlCodeValue());
		queryparameters.put("FINANCIALYEARID", glBalancesBO
				.getFinancialYearBO().getFinancialYearId());
		final List<GlBalancesBO> glBalancesBOs = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.GetExistedOpenBalance",
				queryparameters, GlBalancesBO.class);
		return glBalancesBOs;
	}

	@Override
	public List<GlBalancesBO> getYearEndGlBalancesBOs(String querystring,
			int oldFinancialYearId) {
		List<GlBalancesBO> balancesBOs = null;
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("FINANCIALYEARID", oldFinancialYearId);
		balancesBOs = executeNamedQueryWithResultTransformer(querystring,
				queryparameters, GlBalancesBO.class);
		return balancesBOs;
	}

	@Override
	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short levelId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICE_LEVEL_ID", levelId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.office", queryparameters,
				OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}
	@Override
	public List<CoaNamesDto> findCoaNames(String globalofficenum) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("GLOBAL_OFFICE_NUM",  globalofficenum);
		final List<CoaNamesDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.coa_Branch", queryparameters,
				CoaNamesDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<GLCodeDto> findRemainingCoaNames(String globalofficenum) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("GLOBAL_OFFICE_NUM_WITH_COA_NAME",  globalofficenum);
		final List<GLCodeDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.globalOfficeNumWithCoaName", queryparameters,
				GLCodeDto.class);
		return accountHeadGlCodes;
	}
	@Override
	public int deletegGlobalNumCoaNames(String deletecoaname) {

		final int accountHeadGlCodes = deleteNamedQueryWithResultTransformer(deletecoaname);
		return accountHeadGlCodes;
	}

	@Override
	public List<GLCodeDto> findCoaBranchNames(String coaname) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("GLOBAL_OFFICE_NUM",  coaname);
		final List<GLCodeDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.coa_Branch", queryparameters,
				GLCodeDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public String findLastProcessingUpdatedDate(String namedQueryString,
			String globalOfficeNumber) {
		String lastProcessingDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Query query = createdNamedQuery(namedQueryString);
		query.setString("GLOBAL_OFFICE_NUMBER", globalOfficeNumber);
		List<ProcessUpdateBo> list = query.list();
		
		if(list.size()==0){
			Query q = createdNamedQuery("getConfigurationKeyValueByKey");
			q.setString("KEY", "MisProcessing");
			List<ConfigurationKeyValue> value = q.list();
			if (value.size() > 0) {
				ConfigurationKeyValue configurationKeyValue = value.get(0);
				lastProcessingDate = configurationKeyValue.getValue();
				
			}

		}
		else if (list.size() > 0 && !"".equals(list.get(0).getLastUpdateDate())) {
			ProcessUpdateBo processUpdateBo = list.get(0);
			if (processUpdateBo.getLastUpdateDate() != null) {
				lastProcessingDate = simpleDateFormat.format(processUpdateBo.getLastUpdateDate());
			}
			
		}

		return lastProcessingDate;
	}

	@Override
	public List<CoaNamesDto> findCoaNamesWithGlcodeValues(String coaname) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("COA_NAME",  coaname);
		final List<CoaNamesDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.coa_Name", queryparameters,
				CoaNamesDto.class);
		return accountHeadGlCodes;
	}
	@Override
	public List<OfficeGlobalDto> findCustomersWithGlobalNum(Short levelId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("CUSTOMER_LEVEL_ID", levelId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.customer", queryparameters,
				OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<FinancialYearBO> findFinancialYear() {
		Query query = createdNamedQuery("getStartDateByStatus");
		List<FinancialYearBO> list = query.list();
		return list;
	}

	@Override
	public List<GLCodeDto> findGlCodes() {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> glCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalGlCodes", queryparameters,
				GLCodeDto.class);
		return glCodes;
	}

	@Override
	public List<ViewTransactionsDto> findAccountingTransactions(
			Date toTrxnDate, Date fromTrxnDate, int startRecord,
			int numberOfRecords) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TO_TRANSACTION_DATE", toTrxnDate);
		queryparameters.put("FROM_TRANSACTION_DATE", fromTrxnDate);
		queryparameters.put("START_RECORD", startRecord);
		queryparameters.put("NUMBER_OF_RECORDS", numberOfRecords);
		final List<ViewTransactionsDto> viewAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewTransactions", queryparameters,
				ViewTransactionsDto.class);
		return viewAccountingTransactions;
	}

	public List<RowCount> findTotalNumberOfRecords(Date toTrxnDate,
			Date fromTrxnDate) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TO_TRANSACTION_DATE", toTrxnDate);
		queryparameters.put("FROM_TRANSACTION_DATE", fromTrxnDate);
		final List<RowCount> rowCountList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalNumberOfRecords",
				queryparameters, RowCount.class);
		return rowCountList;
	}

	@Override
	public String findLastProcessingDate(String namedQueryString) {
		String lastProcessingDate = null;
		Query query = createdNamedQuery(namedQueryString);
		query.setString("KEY", "MisProcessing");
		List<ConfigurationKeyValue> list = query.list();
		if (list.size() > 0 && !"".equals(list.get(0).getValue())) {
			ConfigurationKeyValue configurationKeyValue = list.get(0);
			lastProcessingDate = configurationKeyValue.getValue();
		}
		return lastProcessingDate;
	}

	@Override
	public String findLastProcessingDateFirstTime(String namedQueryString) {
		String lastProcessingDate = null;
		Query query = createdNamedQuery(namedQueryString);
		List<FinancialYearBO> list = query.list();
		if (list.size() > 0) {
			FinancialYearBO bo = list.get(0);
			lastProcessingDate = DateUtils.format(bo
					.getFinancialYearStartDate());
		}
		return lastProcessingDate;
	}
	@Override
	public List<MisProcessingTransactionsDto> processMisPostings(
			Date lastProcessDate, String officeId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("LAST_PROCESSING_DATE", lastProcessDate);
		queryparameters.put("GLOBAL_OFFICE_NUMBER", officeId);
		final List<MisProcessingTransactionsDto> misProcessingTransactionDtosList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ProcessAccountingList",
				queryparameters, MisProcessingTransactionsDto.class);

		return misProcessingTransactionDtosList;
	}

	@Override
	public List<MisProcessingTransactionsDto> processMisPostings(
			Date lastProcessDate) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("LAST_PROCESSING_DATE", lastProcessDate);
		final List<MisProcessingTransactionsDto> misProcessingTransactionDtosList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ProcessAccountingList",
				queryparameters, MisProcessingTransactionsDto.class);

		return misProcessingTransactionDtosList;
	}

	@Override
	public void updateLastProcessUpdatedDate(Date lastProcessDate,
			String globalOfficeNumber) {
		Query q = createdNamedQuery("getLastProcessUpdateDate");
		q.setString("GLOBAL_OFFICE_NUMBER", globalOfficeNumber);
		List<ProcessUpdateBo> list = q.list();
		if (list.size() > 0) {
			ProcessUpdateBo processUpdateBo = list.get(0);
			processUpdateBo.setLastUpdateDate(lastProcessDate);
			try {
				createOrUpdate(processUpdateBo);
			} catch (PersistenceException e) {
				throw new MifosRuntimeException(e);
			}
		} else {
			try {
				ProcessUpdateBo processUpdateBo = new ProcessUpdateBo();
				processUpdateBo.setGlobalOfficeNumber(globalOfficeNumber);
				processUpdateBo.setLastUpdateDate(lastProcessDate);
				save(processUpdateBo);
			} catch (PersistenceException e) {
				throw new MifosRuntimeException(e);
			}
		}

	}
	public void updateLastProcessDate(Date lastProcessDate) {
		Query q = createdNamedQuery("getConfigurationKeyValueByKey");
		q.setString("KEY", "MisProcessing");
		List<ConfigurationKeyValue> list = q.list();
		if (list.size() > 0) {
			ConfigurationKeyValue configurationKeyValue = list.get(0);
			configurationKeyValue.setValue(DateUtils.format(lastProcessDate));
			try {
				createOrUpdate(configurationKeyValue);
			} catch (PersistenceException e) {
				throw new MifosRuntimeException(e);
			}
		}
	}

	// Hugo Technologies view stage transaction DtoHibernate
	@Override
	public List<ViewStageTransactionsDto> findStageAccountingTransactions(
			String stage, int startRecord, int numberOfRecords) {

		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("START_RECORD", startRecord);
		queryparameters.put("NUMBER_OF_RECORDS", numberOfRecords);
		final List<ViewStageTransactionsDto> viewStageAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewStageTransactions",
				queryparameters, ViewStageTransactionsDto.class);
		return viewStageAccountingTransactions;
	}

	@Override
	public List<RowCount> findTotalNumberOfStageRecords() {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		final List<RowCount> rowCountList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalNumberOfStageRecords",
				queryparameters, RowCount.class);
		return rowCountList;
	}

	// Hugo Technologies approve stage transaction
	@Override
	public void updateStage(int transactionNO, int stage) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		GlMasterBO glMasterBo = null;
		queryparameters.put("STAGE", stage);
		queryparameters.put("TRANSACTION_NO", transactionNO);

		try {
			executeNamedQueryForUpdate(
					"ChartOfAccountsForMifos.updateStageTransactions",
					queryparameters);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Hugo Technologies Edit staged transaction
	@Override
	public List<ViewStageTransactionsDto> findStagedAccountingTransactionOnId(
			int transactionNO) {

		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TRANSACTION_NO", transactionNO);
		final List<ViewStageTransactionsDto> viewStageAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.EditStageTransactions",
				queryparameters, ViewStageTransactionsDto.class);
		return viewStageAccountingTransactions;
	}

	// Hugo Technologies Inter Office Transfer
	@Override
	public List<GLCodeDto> findInterBankDebitAccounts() {

		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> interOfficeGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.CashAndBank", emptyqueryparameters,
				GLCodeDto.class);
		return interOfficeGlCodes;
	}

	@Override
	public List<GLCodeDto> findAuditGlCodes() {
		final Map<String, Object> emptyqueryparameters = new HashMap<String, Object>();
		final List<GLCodeDto> auditGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.AuditGlCodes", emptyqueryparameters,
				GLCodeDto.class);
		return auditGlCodes;
	}

	@Override
	public List<OfficeGlobalDto> findCustomersWithGlobalNum(Short levelId,
			String officeId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("CUSTOMER_LEVEL_ID", levelId);
		queryparameters.put("OFFICE_ID", officeId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.customerExcludeOfficeId",
				queryparameters, OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short officeLevelId,
			String officeId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("OFFICE_LEVEL_ID", officeLevelId);
		queryparameters.put("OFFICE_ID", officeId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.officeExcludeOfficeId",
				queryparameters, OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public void addComments(String transactionId, String audit,
			String auditComments) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TRANSACTION_NO", transactionId);
		queryparameters.put("AUDIT", audit);
		queryparameters.put("AUDIT_COMMENTS", auditComments);

		try {
			executeNamedQueryForUpdate(
					"ChartOfAccountsForMifos.auditTransactions",
					queryparameters);
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<GlDetailDto> findChequeDetails(int transactionNo) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("TRANSACTION_NO", transactionNo);
		final List<GlDetailDto> glDetailDto = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.chequeDetails", queryparameters,
				GlDetailDto.class);

		return glDetailDto;
	}

	@Override
	public List<ViewStageTransactionsDto> findStageAccountingTransactions(
			Date date1, Date date2, int startRecord, int numberOfRecords) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("FROM_DATE", date1);
		queryparameters.put("TO_DATE", date2);
		queryparameters.put("START_RECORD", startRecord);
		queryparameters.put("NUMBER_OF_RECORDS", numberOfRecords);
		final List<ViewStageTransactionsDto> viewStageAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewAuditTransactions",
				queryparameters, ViewStageTransactionsDto.class);
		return viewStageAccountingTransactions;
	}

	@Override
	public List<RowCount> findTotalNumberOfStageRecords(Date date1, Date date2) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("FROM_DATE", date1);
		queryparameters.put("TO_DATE", date2);
		final List<RowCount> rowCountList = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.TotalNumberOfAuditRecords",
				queryparameters, RowCount.class);
		return rowCountList;
	}

	@Override
	public List<OfficeGlobalDto> findDynamicCustomersWithGlobalNum(
			String officeId, String officLevelId) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("CUSTOMER_LEVEL_ID", officeId);
		queryparameters.put("CUSTOMER_LEVEL_ID", officeId);
		final List<OfficeGlobalDto> accountHeadGlCodes = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.customer", queryparameters,
				OfficeGlobalDto.class);
		return accountHeadGlCodes;
	}

	@Override
	public boolean savingCoaBranchTransaction(CoaBranchBO coaBranchBO) {
	boolean result = false;
			try {
				CoaBranchBO  o = (CoaBranchBO) save(coaBranchBO);
				if (o.getCoaid() > 0)
					result = true;
	 
			} catch (Exception e) {
				throw new MifosRuntimeException(e);
			}
			return result;
		}

	/*@Override
	public List<ViewStageTransactionsDto> findConsolidatedAccountingTransactions(
			String branchoffice, int startRecord, int numberOfRecords) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("BRANCH_OFFICE", branchoffice);
		queryparameters.put("START_RECORD", startRecord);
		queryparameters.put("NUMBER_OF_RECORDS", numberOfRecords);
		final List<ViewStageTransactionsDto> viewStageAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewConsolidatedTransactions",
				queryparameters, ViewStageTransactionsDto.class);
		return viewStageAccountingTransactions;
	}*/
	@Override
	public List<ViewStageTransactionsDto> findConsolidatedAccountingTransactions(
			String branchoffice) {
		final Map<String, Object> queryparameters = new HashMap<String, Object>();
		queryparameters.put("BRANCH_OFFICE", branchoffice);
		final List<ViewStageTransactionsDto> viewStageAccountingTransactions = executeNamedQueryWithResultTransformer(
				"ChartOfAccountsForMifos.ViewConsolidatedTransactions",
				queryparameters, ViewStageTransactionsDto.class);
		return viewStageAccountingTransactions;
	}
}

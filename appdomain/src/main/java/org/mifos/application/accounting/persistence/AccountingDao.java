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
import java.util.Date;
import java.util.List;

import org.mifos.application.accounting.business.CoaBranchBO;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlMasterBO;
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

public interface AccountingDao {


	public List<GLCodeDto> findMainAccountCashGlCodes();

	public List<GlBalancesBO> findExistedGlBalacesBOs(Integer officeLevelId,
			String officeId, String glCodeValue,Integer financialYearId);

	public List<GLCodeDto> findDebitAccounts();
	public List<RolesActivityDto> findrolesActivity(int activityid);
	public List<GLCodeDto> findCreditAccounts(String glCode);
	public List<GLCodeDto> findMainAccountBankGlCodes();
	public List<GLCodeDto> findMainAccountHeadGlCodes(String glname );

	public List<GLCodeDto> findAccountHeadGlCodes(String glCode);

	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short levelId);

	public List<OfficeGlobalDto> findCustomersWithGlobalNum(Short levelId);
	public List<GlobalOfficeNumDto>findGlobalDiplayNumandname(String officename);

	public List<ViewTransactionsDto> findAccountingTransactions(Date toTrxnDate,Date fromTrxnDate,
			int startRecord, int numberOfRecords);

	public List<RowCount> findTotalNumberOfRecords(Date toTrxnDate,Date fromTrxnDate);

	public String findLastProcessingDate(String namedQueryString);

	public String findLastProcessingDateFirstTime(String namedQueryString);
	//public List<MisProcessingTransactionsDto> processMisPostings(Date lastProcessDate,String officeId);

	public List<MisProcessingTransactionsDto> processMisPostings(Date lastProcessDate);

	public List<FinancialYearBO> findFinancialYear();

	public List<GLCodeDto> findGlCodes();

	//public String findLastProcessingUpdatedDate(String string, String globalOfficeNumber);

	public void updateLastProcessDate(Date lastProcessDate);

	//public void updateLastProcessUpdatedDate(Date lastProcessDate, String globalOfficeNumber);
	public boolean savingGeneralLedgerTransaction(GlMasterBO glMasterBO);
	public boolean savingCoaBranchTransaction(CoaBranchBO CoaBranchBO);
	
	public FinancialYearBO savingFinancialYearBO(FinancialYearBO financialYearBO);

	public boolean savingOpenBalancesTransaction(GlBalancesBO balancesBO);

	public <T extends Object> T getPersistedObject(final Class<T> clazz,
			final Serializable persistentObjectId) throws PersistenceException;

	public List<GlBalancesBO> getResultantGlBalancesBO(GlBalancesBO glBalancesBO);
	
	public List<GlBalancesBO> getYearEndGlBalancesBOs(String querystring,int oldFinancialYearId );

	List<ViewStageTransactionsDto> findStageAccountingTransactions(
			String stage, int startRecord, int numberOfRecords);
	//List<ViewStageTransactionsDto> findConsolidatedAccountingTransactions(String branchoffice, int startRecord, int numberOfRecords);

	List<ViewStageTransactionsDto> findConsolidatedAccountingTransactions(String branchoffice);
	
	List<RowCount> findTotalNumberOfStageRecords();

	public void updateStage(int transactionNo, int stage);

	public List<ViewStageTransactionsDto> findStagedAccountingTransactionOnId(
			int transactionNo);

	public List<GLCodeDto> findInterBankDebitAccounts();

	public List<GLCodeDto> findAuditGlCodes();

	public List<OfficeGlobalDto> findCustomersWithGlobalNum(
			Short customerLevelId, String officeId);

	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short officeLevelId,
			String officeId);

	public void addComments(String transactionId, String audit,
			String auditComments);

	public List<GlDetailDto> findChequeDetails(int transactionNo);

	public List<ViewStageTransactionsDto> findStageAccountingTransactions(
			Date date1, Date date2, int iPageNo, int noOfRecordsPerPage);

	public List<RowCount> findTotalNumberOfStageRecords(Date date1, Date date2);

	public List<DynamicOfficeDto> getListOfOffices(String officeId,
			String officeLevelId);

	public List<MisProcessingTransactionsDto> processMisPostings(Date lastProcessDate, String officeId);

	public String findLastProcessingUpdatedDate(String string, String globalOfficeNumber);

	public void updateLastProcessUpdatedDate(Date lastProcessDate, String globalOfficeNumber);

	public List<OfficeGlobalDto> findDynamicCustomersWithGlobalNum(
			String officeId, String officLevelId);
	
	public List<GLCodeDto> findCoaBranchAccountHeadGlCodes();
	public List<CoaNamesDto> findCoaNames(String globalofficenum);
	public List<GLCodeDto> findRemainingCoaNames(String globalofficenum);
	public int deletegGlobalNumCoaNames(String deletecoaname);
	public List<GLCodeDto> findCoaBranchNames(String coaname);
	public List<CoaNamesDto> findCoaNamesWithGlcodeValues(String coaname);

	
}

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
package org.mifos.application.servicefacade;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounting.business.CoaBranchBO;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.dto.domain.CoaNamesDto;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlDetailDto;
import org.mifos.dto.domain.GlobalOfficeNumDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.RolesActivityDto;
import org.mifos.dto.domain.ViewStageTransactionsDto;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.security.util.UserContext;

/**
 * Service facade for Mifos Accounting functionality.
 */
public interface AccountingServiceFacade {

	List<OfficeGlobalDto> loadOfficesForLevel(Short officeLevelId);

	GlBalancesBO loadExistedGlBalancesBO(Integer officeLevelId,
			String officeId, String glCodeValue, Integer finantialYearId);

	List<OfficeGlobalDto> loadCustomerForLevel(Short customerLevelId);

	List<GLCodeDto> mainAccountForCash();

	List<GLCodeDto> loadDebitAccounts();

	List<GLCodeDto> loadCreditAccounts(String glCode);

	List<GLCodeDto> findMainAccountHeadGlCodes(String glname);

	List<GlobalOfficeNumDto> findGlobalDiplayNum(String officename);

	List<GLCodeDto> mainAccountForBank();

	List<RolesActivityDto> glloadRolesActivity();

	List<RolesActivityDto> jvloadRolesActivity();

	List<GLCodeDto> accountHead(String glCode);

	List<ViewTransactionsDto> getAccountingTransactions(Date toTrxnDate,
			Date fromTrxnDate, int startRecord, int numberOfRecords);

	int getNumberOfTransactions(Date toTrxnDate, Date fromTrxnDate);

	String getLastProcessDate();

	boolean processMisPostings(Date lastProcessDate, Date processTillDate,
			Short createdBy);

	FinancialYearBO getFinancialYear();

	FinancialYearBO updateFinancialYear(FinancialYearBO financialYearBO,
			UserContext userContext);

	List<GLCodeDto> findTotalGlAccounts();

	FinancialYearBO getFinancialYearBO(int financialYearId);

	boolean savingAccountingTransactions(GlMasterBO bo);

	boolean savingStageAccountingTransactions(GlMasterBO bo);

	boolean savingOpeningBalances(GlBalancesBO bo);

	public void processYearEndBalances(UserContext userContext,
			FinancialYearBO oldFinancialYearBO);

	//List<ViewStageTransactionsDto> getStageAccountingTransactions(Object object, int iPageNo, int noOfRecordsPerPage);

	List<ViewStageTransactionsDto> getStageAccountingTransactions(String stage,
			int startRecord, int numberOfRecords);
	//List<ViewStageTransactionsDto> getConsolidatedTransactions(String branchoffice,int startRecord, int numberOfRecords);
 
	List<ViewStageTransactionsDto> getConsolidatedTransactions(String branchoffice);
	int getNumberOfStageTransactions();

	void approveStageAccountingTransactions(int transactionNo, int stage);

	ViewStageTransactionsDto getstagedAccountingTransactions(int transactionNo);

	List<GLCodeDto> loadInterOfficeDebitAccounts();

	public List<GLCodeDto> auditAccountHeads();

	public List<OfficeGlobalDto> loadCustomerForLevel(Short short1,
			String fromOffice);

	public List<OfficeGlobalDto> loadOfficesForLevel(Short valueOf,
			String fromOffice);

	public void addAuditComments(String stageTransactionNo, String audit,
			String auditComments);

	public GlDetailDto getChequeDetails(int stageTransactionNo);

	public List<ViewStageTransactionsDto> getStageAccountingTransactions(
			Date date, Date date2, int iPageNo, int noOfRecordsPerPage);

	public int getNumberOfStageTransactions(Date date, Date date2);

	public List getOfficeDetails(String officeId, String officLevelId);

	public boolean processMisPostings(Date lastProcessDate,
			Date processTillDate, Short createdBy, String officeId);

	public String getLastProcessUpdatedDate(String globalOfficeNumber);

	public List<OfficeGlobalDto> loadDynamicCustomerForLevel(String officeId,
			String officLevelId);

	List<GLCodeDto> coaBranchAccountHead();

	List<GLCodeDto> loadRemainingCoaNames(String globalnumwithcoaname);

	int deleteGlobalNumRelatedCoaNames(String deletecoaname);

	List<CoaNamesDto> loadCoaNames(String globalofficenum);

	List<GLCodeDto> loadCoaBranchNames(String coaName);

	List<CoaNamesDto> loadCoaNamesWithGlcodeValues(String coaname);
	boolean savingCoaBranchTransactions(CoaBranchBO co);
}

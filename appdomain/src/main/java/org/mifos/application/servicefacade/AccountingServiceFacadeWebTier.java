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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.mifos.application.accounting.business.CoaBranchBO;
import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.accounting.persistence.AccountingDao;
import org.mifos.application.accounting.persistence.AccountingDaoHibernate;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.CoaNamesDto;
import org.mifos.dto.domain.DynamicOfficeDto;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.GlDetailDto;
import org.mifos.dto.domain.MisProcessingTransactionsDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.RolesActivityDto;
import org.mifos.dto.domain.RowCount;
import org.mifos.dto.domain.ViewStageTransactionsDto;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;

/**
 * Default implementation of {@link AccountingServiceFacade}.
 */
public class AccountingServiceFacadeWebTier implements AccountingServiceFacade {

	private AccountingDao accountingDao = new AccountingDaoHibernate();
	private final HibernateTransactionHelper hibernateTransactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

	@Override
	public List<RolesActivityDto> glloadRolesActivity() {
		List<RolesActivityDto> rolesactivityDtos = null;

		rolesactivityDtos = accountingDao.findrolesActivity(302);

		return rolesactivityDtos;
	}



	@Override
	public List<RolesActivityDto> jvloadRolesActivity() {
		List<RolesActivityDto> rolesactivityDtos = null;

		rolesactivityDtos = accountingDao.findrolesActivity(303);

		return rolesactivityDtos;
	}


	@Override
	public List<OfficeGlobalDto> loadOfficesForLevel(Short officeLevelId) {
		List<OfficeGlobalDto> detailsDtos = null;
		detailsDtos = accountingDao.findOfficesWithGlobalNum(officeLevelId);

		return detailsDtos;
	}

	public GlBalancesBO loadExistedGlBalancesBO(Integer officeLevelId,
			String officeId, String glCodeValue,Integer financialYearId) {
		GlBalancesBO balancesBO = null;
		List<GlBalancesBO> list = accountingDao.findExistedGlBalacesBOs(
				officeLevelId, officeId, glCodeValue,financialYearId);
		if (list.size() > 0) {
			balancesBO = list.get(0);
		}
		return balancesBO;
	}

	public List<OfficeGlobalDto> loadCustomerForLevel(Short customerLevelId) {
		List<OfficeGlobalDto> detailsDtos = null;
		detailsDtos = accountingDao.findCustomersWithGlobalNum(customerLevelId);
		return detailsDtos;
	}

	@Override
	public List<GLCodeDto> mainAccountForCash() {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findMainAccountCashGlCodes();
		return accountingDtos;
	}

	@Override
	public List<GLCodeDto> loadDebitAccounts() {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findDebitAccounts();
		return accountingDtos;
	}

	@Override
	public List<GLCodeDto> loadCreditAccounts(String glCode) {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findCreditAccounts(glCode);
		return accountingDtos;
	}

	@Override
	public List<GLCodeDto> mainAccountForBank() {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findMainAccountBankGlCodes();
		return accountingDtos;
	}

	@Override
	public List<GLCodeDto> accountHead(String glCode) {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findAccountHeadGlCodes(glCode);
		return accountingDtos;
	}

	@Override
	public List<ViewTransactionsDto> getAccountingTransactions(Date toTrxnDate,Date fromTrxnDate,
			int startRecord, int numberOfRecords) {
		List<ViewTransactionsDto> accountingTransactions = null;
		accountingTransactions = accountingDao.findAccountingTransactions(
				toTrxnDate,fromTrxnDate, startRecord, numberOfRecords);
		return accountingTransactions;
	}

	public int getNumberOfTransactions(Date toTrxnDate,Date fromTrxnDate) {
		int totalNumberOfRecords = 0;
		List<RowCount> rowCountList = accountingDao
				.findTotalNumberOfRecords(toTrxnDate,fromTrxnDate);
		if (rowCountList != null && rowCountList.size() > 0) {
			totalNumberOfRecords = rowCountList.get(0)
					.getTotalNumberOfRecords();
		}
		return totalNumberOfRecords;
	}

	@Override
	public String getLastProcessDate() {
		String lastProcessDate = null;
		lastProcessDate = accountingDao
				.findLastProcessingDate("getConfigurationKeyValueByKey");
		if (lastProcessDate == null) {
			lastProcessDate = accountingDao
					.findLastProcessingDateFirstTime("getStartDateByStatus");
		}
		return lastProcessDate;
	}

	@Override
	public boolean processMisPostings(Date lastProcessDate,
			Date processTillDate, Short createdBy) {
		boolean flag = false;
		while (lastProcessDate.compareTo(processTillDate) < 0) {
			Calendar c = Calendar.getInstance();
			c.setTime(lastProcessDate);
			c.add(Calendar.DATE, 1);
			Date newDate = c.getTime();
			lastProcessDate = newDate;
			processListOfTransactions(
					accountingDao.processMisPostings(lastProcessDate),
					createdBy);
			accountingDao.updateLastProcessDate(lastProcessDate);
		}
		return flag;
	}

	public boolean processListOfTransactions(
			List<MisProcessingTransactionsDto> list, Short createdBy) {
		boolean flag = false;
		for (MisProcessingTransactionsDto dto : list) {
			savingAccountingTransactions(getGlMasterBO(dto, createdBy));
		}
		return flag;
	}

	GlMasterBO getGlMasterBO(MisProcessingTransactionsDto dto, Short createdBy) {
		GlMasterBO bo = new GlMasterBO();
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
		glDetailBOList.add(new GlDetailBO(dto.getGlCredit(), dto.getAmount(),
				"credit", "", null, "", "",dto.getTransactionNotes()));
		bo.setGlDetailBOList(glDetailBOList);
		bo.setTransactionDate(dto.getPostedDate());
		bo.setTransactionType(dto.getVoucherType());
		bo.setFromOfficeLevel(dto.getOfficeLevel());
		bo.setFromOfficeId(dto.getGlobalOfficeNum());
		bo.setToOfficeLevel(dto.getOfficeLevel());
		bo.setToOfficeId(dto.getGlobalOfficeNum());
		bo.setMainAccount(dto.getGlDebit());//
		bo.setTransactionAmount(dto.getAmount());
		bo.setAmountAction("debit");//
		bo.setTransactionNarration("Mis Processing");//
		bo.setStatus("");// default value
		bo.setTransactionBy(1); // default value
		bo.setCreatedBy(createdBy);
		bo.setCreatedDate(dto.getVoucherDate());
		return bo;
	}

	@Override
	public FinancialYearBO getFinancialYear() {
		FinancialYearBO financialYearBO = null;
		List<FinancialYearBO> financialYearList = null;
		financialYearList = accountingDao.findFinancialYear();
		if (financialYearList.size() > 0) {
			financialYearBO = financialYearList.get(0);
		}
		return financialYearBO;
	}
	
	@Override
	public FinancialYearBO updateFinancialYear(FinancialYearBO oldFinancialYearBO,UserContext context) {
		FinancialYearBO newFinancialYearBO=null;
		accountingDao.savingFinancialYearBO(oldFinancialYearBO); //updating current financial year as Inactive
		this.hibernateTransactionHelper.flushSession();
		//creating new Finincial year
		Calendar calendar=new GregorianCalendar();
		newFinancialYearBO =new FinancialYearBO();
		newFinancialYearBO.setFinancialYearStartDate(nextYear(oldFinancialYearBO.getFinancialYearStartDate(),calendar));
		newFinancialYearBO.setFinancialYearEndDate(nextYear(oldFinancialYearBO.getFinancialYearEndDate(),calendar));
		try {
			newFinancialYearBO.setCreatedDate(DateUtils.getLocaleDate(context.getPreferredLocale(),
					DateUtils.getCurrentDate(context.getPreferredLocale())));
		} catch (InvalidDateException e) {
			throw new MifosRuntimeException(e);
		}
		newFinancialYearBO.setCreatedBy(context.getId());
		newFinancialYearBO.setStatus(SimpleAccountingConstants.ACTIVE);
		
		
		calendar.setTime(newFinancialYearBO.getFinancialYearStartDate());
		newFinancialYearBO.setFinancialYearId(Integer.parseInt(calendar.get(calendar.YEAR)+""+calendar.get(calendar.YEAR)));
		newFinancialYearBO=accountingDao.savingFinancialYearBO(newFinancialYearBO);
		this.hibernateTransactionHelper.flushSession();
		return newFinancialYearBO;
	}
	
	public Date nextYear(Date date,Calendar calendar){
	      calendar.setTime(date);
	      calendar.add(Calendar.YEAR, 1);
	     return calendar.getTime();
	}

	@Override
	public List<GLCodeDto> findTotalGlAccounts() {
		List<GLCodeDto> accountingDtos = null;
		accountingDtos = accountingDao.findGlCodes();
		return accountingDtos;
	}

	public FinancialYearBO getFinancialYearBO(int financialYearId) {
		FinancialYearBO financialYearBO = null;
		try {
			financialYearBO = accountingDao.getPersistedObject(
					FinancialYearBO.class, financialYearId);
		} catch (PersistenceException e) {
			throw new MifosRuntimeException(e);
		}
		return financialYearBO;
	}

	@Override
	public boolean savingAccountingTransactions(GlMasterBO bo)
			throws MifosRuntimeException {
		boolean flag = false;

		this.hibernateTransactionHelper.startTransaction();
		flag = accountingDao.savingGeneralLedgerTransaction(bo);
		//
		if (flag == true) {
			updateGlBalancesBO(bo.getMainAccount(), bo.getAmountAction(), bo);
			updateGlBalancesBO(bo.getGlDetailBOList().get(0).getSubAccount(),
					bo.getGlDetailBOList().get(0).getAmountAction(), bo);
		}
		//
		this.hibernateTransactionHelper.commitTransaction();

		return flag;
	}

	public boolean updateGlBalancesBO(String accountglCode, String action,
			GlMasterBO bo) {
		boolean flag = false;
		GlBalancesBO balancesBOTemp = new GlBalancesBO();
		balancesBOTemp.setOfficeId(bo.getFromOfficeId());
		balancesBOTemp.setOfficeLevel(bo.getFromOfficeLevel());
		balancesBOTemp.setCreatedBy(bo.getCreatedBy());
		balancesBOTemp.setCreatedDate(bo.getCreatedDate());
		
		FinancialYearBO financialYearBO=getFinancialYear();
		if(financialYearBO!=null)
		balancesBOTemp.setFinancialYearBO(financialYearBO);
		else
			throw new MifosRuntimeException("no financial year defined");
		balancesBOTemp.setGlCodeValue(accountglCode);

		//
		GlBalancesBO balancesBO = null;
		List<GlBalancesBO> balancesBOs = accountingDao
				.getResultantGlBalancesBO(balancesBOTemp);
		if (balancesBOs.size() > 0) { // to check whether the row is existed or
										// not
			balancesBO = balancesBOs.get(0);
			balancesBO = getResultantGlBalancesBOForAccountingTransactions(
					balancesBO, bo.getTransactionAmount(), action);
		} else {
			balancesBO = balancesBOTemp;
			balancesBO = getResultantGlBalancesBOForAccountingTransactions(
					balancesBO, bo.getTransactionAmount(), action);
		}
		this.hibernateTransactionHelper.flushAndClearSession();
		flag = accountingDao.savingOpenBalancesTransaction(balancesBO);

		return flag;
	}

	@Override
	public boolean savingOpeningBalances(GlBalancesBO bo)
			throws MifosRuntimeException {
		boolean flag = false;
		List<GlBalancesBO> balancesBOs = accountingDao
				.getResultantGlBalancesBO(bo);
		GlBalancesBO balancesBO = null;
		if (balancesBOs.size() > 0) {
			balancesBO = balancesBOs.get(0);
			balancesBO = getResultantGlBalancesBO(balancesBO, bo);
		} else {
			balancesBO = bo;
		}
		this.hibernateTransactionHelper.startTransaction();
		flag = accountingDao.savingOpenBalancesTransaction(balancesBO);
		this.hibernateTransactionHelper.flushSession();
		return flag;
	}
	
	public void savingYearEndBalances(FinancialYearBO oldFinancialYearBO,FinancialYearBO newFinancialYearBO){
        List<GlBalancesBO> assetsGlBalanceBOs;
        List<GlBalancesBO> liabilitiesGlBalanceBOs;
        assetsGlBalanceBOs=accountingDao.getYearEndGlBalancesBOs("ChartOfAccountsForMifos.YearEndProcessAssets",oldFinancialYearBO.getFinancialYearId());
        liabilitiesGlBalanceBOs=accountingDao.getYearEndGlBalancesBOs("ChartOfAccountsForMifos.YearEndProcessLiabilities",oldFinancialYearBO.getFinancialYearId());
        assetsGlBalanceBOs.addAll(liabilitiesGlBalanceBOs);
        for(GlBalancesBO balancesBO:assetsGlBalanceBOs){
        	balancesBO.setFinancialYearBO(newFinancialYearBO);
        	savingOpeningBalances(balancesBO);
        }
		
	}
	
	public void processYearEndBalances(UserContext userContext,FinancialYearBO oldFinancialYearBO){
		this.hibernateTransactionHelper.startTransaction();
		FinancialYearBO newFinancialYearBO=processingFinancialYear(userContext,oldFinancialYearBO);
		savingYearEndBalances(oldFinancialYearBO,newFinancialYearBO);
		this.hibernateTransactionHelper.commitTransaction();
	}
	
	public FinancialYearBO processingFinancialYear(UserContext userContext,FinancialYearBO oldFinancialYearBO){
		FinancialYearBO newFinancialYearBO=null;
		oldFinancialYearBO.setStatus(SimpleAccountingConstants.INACTIVE);
		newFinancialYearBO=updateFinancialYear(oldFinancialYearBO,userContext);
		return newFinancialYearBO;
	}

	public GlBalancesBO getResultantGlBalancesBO(GlBalancesBO fromDatabase,
			GlBalancesBO fromScreen) {
		BigDecimal result = fromDatabase.getTransactionDebitSum().subtract(
				fromDatabase.getTransactionCreditSum());
		fromDatabase.setClosingBalance((fromScreen.getOpeningBalance()
				.add(result)));
		fromDatabase.setOpeningBalance(fromScreen.getOpeningBalance());
		fromDatabase.setCreatedBy(fromScreen.getCreatedBy());
		fromDatabase.setCreatedDate(fromScreen.getCreatedDate());
		fromDatabase.setFinancialYearBO(fromScreen.getFinancialYearBO());
		return fromDatabase;
	}

	public GlBalancesBO getResultantGlBalancesBOForAccountingTransactions(
			GlBalancesBO fromDatabase, BigDecimal amount, String action) {
		FinancialYearBO financialYearBO = getFinancialYear();
		fromDatabase.setFinancialYearBO(financialYearBO);
		if (action.equals("debit"))
			fromDatabase.setTransactionDebitSum(fromDatabase
					.getTransactionDebitSum().add(amount));
		else if (action.equals("credit"))
			fromDatabase.setTransactionCreditSum(fromDatabase
					.getTransactionCreditSum().add(amount));
		BigDecimal result = fromDatabase.getTransactionDebitSum().subtract(
				fromDatabase.getTransactionCreditSum());
		fromDatabase.setClosingBalance((fromDatabase.getOpeningBalance()
				.add(result)));
		return fromDatabase;
	}

	@Override
	public List<ViewStageTransactionsDto> getStageAccountingTransactions(
			Object object, int iPageNo, int noOfRecordsPerPage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfStageTransactions() {
		int totalNumberOfRecords = 0;
		List<RowCount> rowCountList = accountingDao
				.findTotalNumberOfStageRecords();
		if (rowCountList != null && rowCountList.size() > 0) {
			totalNumberOfRecords = rowCountList.get(0)
					.getTotalNumberOfRecords();
		}
		return totalNumberOfRecords;
	}
	@Override
	public void approveStageAccountingTransactions(int transactionNo, int stage) {

		accountingDao.updateStage(transactionNo, stage);

	}

	// Hugo Technologies Edit Stage Transactions
		@Override
		public ViewStageTransactionsDto getstagedAccountingTransactions(
				int transactionNo) {
			ViewStageTransactionsDto viewStageTransactionsDto = null;
			List<ViewStageTransactionsDto> stageAccountingTransactions = null;
			stageAccountingTransactions = accountingDao
					.findStagedAccountingTransactionOnId(transactionNo);
			if (stageAccountingTransactions.size() > 0) {
				viewStageTransactionsDto = stageAccountingTransactions.get(0);
			}
			return viewStageTransactionsDto;
		}

		@Override
		public GlDetailDto getChequeDetails(int transactionNo) {
			GlDetailDto glDetailDto = null;

			List<GlDetailDto> listOfGlDetailDto = accountingDao.findChequeDetails(transactionNo);
			if(listOfGlDetailDto.size()>0){
				glDetailDto = listOfGlDetailDto.get(0);
			}
			return glDetailDto;
		}
		@Override
		public List<GLCodeDto> auditAccountHeads() {
			List<GLCodeDto> accountingDtos = null;
			accountingDtos = accountingDao.findAuditGlCodes();
			return accountingDtos;
		}

		public boolean savingStageAccountingTransactions(GlMasterBO bo) {
			boolean flag = false;
			this.hibernateTransactionHelper.startTransaction();
			flag = accountingDao.savingGeneralLedgerTransaction(bo);
			this.hibernateTransactionHelper.commitTransaction();

			return flag;
		}
		@Override
		public List<ViewStageTransactionsDto> getStageAccountingTransactions(
				Date date1, Date date2, int iPageNo, int noOfRecordsPerPage) {

			List<ViewStageTransactionsDto> stageAccountingTransactions = null;
			stageAccountingTransactions = accountingDao
					.findStageAccountingTransactions(date1,date2, iPageNo,
							noOfRecordsPerPage);
			return stageAccountingTransactions;
		}
		@Override
		public int getNumberOfStageTransactions(Date date1, Date date2) {
			int totalNumberOfRecords = 0;
			List<RowCount> rowCountList = accountingDao
					.findTotalNumberOfStageRecords(date1,date2);
			if (rowCountList != null && rowCountList.size() > 0) {
				totalNumberOfRecords = rowCountList.get(0)
						.getTotalNumberOfRecords();
			}
			return totalNumberOfRecords;
		}
		@Override
		public void addAuditComments(String transactionId, String audit,
				String auditComments) {

			accountingDao.addComments(transactionId, audit, auditComments);

		}
		// Hugo Technologies View Stage Transactions
		@Override
		public List<ViewStageTransactionsDto> getStageAccountingTransactions(
				String stage, int startRecord, int numberOfRecords) {
			List<ViewStageTransactionsDto> stageAccountingTransactions = null;
			stageAccountingTransactions = accountingDao
					.findStageAccountingTransactions(stage, startRecord,
							numberOfRecords);
			return stageAccountingTransactions;
		}

		@Override
		public List<GLCodeDto> loadInterOfficeDebitAccounts() {
			List<GLCodeDto> accountingDtos = null;
			accountingDtos = accountingDao.findInterBankDebitAccounts();
			return accountingDtos;

		}
		@Override
		public List<OfficeGlobalDto> loadCustomerForLevel(Short customerLevelId,
				String officeId) {
			List<OfficeGlobalDto> detailsDtos = null;
			detailsDtos = accountingDao.findCustomersWithGlobalNum(customerLevelId,
					officeId);
			return detailsDtos;
		}

		@Override
		public List<OfficeGlobalDto> loadOfficesForLevel(Short officeLevelId,
				String officeId) {
			List<OfficeGlobalDto> detailsDtos = null;
			detailsDtos = accountingDao.findOfficesWithGlobalNum(officeLevelId,
					officeId);

			return detailsDtos;
		}


		//operational dto transaction
		@Override
		public List getOfficeDetails(String officeId, String officeLevelId) {
			List<DynamicOfficeDto> listOfOffices = null;

			listOfOffices = accountingDao.getListOfOffices(officeId,officeLevelId);

			return listOfOffices;
		}

		@Override
		public String getLastProcessUpdatedDate(String globalOfficeNumber) {
			String lastProcessDate = null;
			lastProcessDate = accountingDao
					.findLastProcessingUpdatedDate("getLastProcessUpdateDate",globalOfficeNumber);
			if (lastProcessDate == null) {
				lastProcessDate = accountingDao
						.findLastProcessingDateFirstTime("getStartDateByStatus");
			}
			return lastProcessDate;
		}
		@Override
		public boolean processMisPostings(Date lastProcessDate,
				Date processTillDate, Short createdBy,String globalOfficeNumber) {
			boolean flag = false;
			while (lastProcessDate.compareTo(processTillDate) < 0) {
				Calendar c = Calendar.getInstance();
				c.setTime(lastProcessDate);
				c.add(Calendar.DATE, 1);
				Date newDate = c.getTime();
				lastProcessDate = newDate;
				processListOfTransactions(
						accountingDao.processMisPostings(lastProcessDate,globalOfficeNumber),
						createdBy);
				//accountingDao.updateLastProcessDate(lastProcessDate);
				accountingDao.updateLastProcessUpdatedDate(lastProcessDate,globalOfficeNumber);

			}
			return flag;
		}

		//coa mapping

		@Override
		public List<GLCodeDto> coaBranchAccountHead() {
			List<GLCodeDto> accountingDtos = null;
			accountingDtos = accountingDao.findCoaBranchAccountHeadGlCodes();
			return accountingDtos;
		}

		@Override
		public List<GLCodeDto> loadRemainingCoaNames(String globalnumwithcoaname) {
			List<GLCodeDto> detailsDtos = null;
			detailsDtos = accountingDao.findRemainingCoaNames(globalnumwithcoaname);

			return detailsDtos;

		}

		@Override
		public int deleteGlobalNumRelatedCoaNames(String deletecoaname) {
			int detailsDtos = 0;
			detailsDtos = accountingDao.deletegGlobalNumCoaNames(deletecoaname);
			return detailsDtos;
		}

		@Override
		public List<GLCodeDto> loadCoaBranchNames(String coaName ) {
			List<GLCodeDto> detailsDtos = null;
			detailsDtos = accountingDao.findCoaBranchNames(coaName);

			return detailsDtos;
		}

		@Override
		public boolean savingCoaBranchTransactions(CoaBranchBO co) {
			boolean flag = false;
			// TODO Auto-generated method stub
			this.hibernateTransactionHelper.startTransaction();
			flag = accountingDao.savingCoaBranchTransaction(co);
			return flag ;
		}

		//voucher

		@Override
		public List<CoaNamesDto> loadCoaNamesWithGlcodeValues(String coaname) {
			List<CoaNamesDto> detailsDtos = null;
			detailsDtos = accountingDao.findCoaNamesWithGlcodeValues(coaname);

			return detailsDtos;
		}

		//consolidated

		@Override
		public List<ViewStageTransactionsDto> getConsolidatedTransactions(
				String branchoffice) {
			List<ViewStageTransactionsDto> stageAccountingTransactions = null;
			stageAccountingTransactions = accountingDao
					.findConsolidatedAccountingTransactions(branchoffice);
			return stageAccountingTransactions;
		}



		@Override
		public List<RolesActivityDto> multipleglloadRolesActivity() {
			List<RolesActivityDto> rolesactivityDtos = null;

			rolesactivityDtos = accountingDao.findrolesActivity(312);

			return rolesactivityDtos;
		}


}
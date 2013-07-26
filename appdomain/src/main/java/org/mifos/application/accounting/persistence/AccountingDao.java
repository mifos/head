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

import org.mifos.application.accounting.business.FinancialYearBO;
import org.mifos.application.accounting.business.GlBalancesBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.dto.domain.GLCodeDto;
import org.mifos.dto.domain.MisProcessingTransactionsDto;
import org.mifos.dto.domain.OfficeGlobalDto;
import org.mifos.dto.domain.RowCount;
import org.mifos.dto.domain.ViewTransactionsDto;
import org.mifos.framework.exceptions.PersistenceException;

public interface AccountingDao {

	public List<GLCodeDto> findMainAccountCashGlCodes();

	public List<GlBalancesBO> findExistedGlBalacesBOs(Integer officeLevelId,
			String officeId, String glCodeValue,Integer financialYearId);

	public List<GLCodeDto> findDebitAccounts();

	public List<GLCodeDto> findCreditAccounts(String glCode);

	public List<GLCodeDto> findMainAccountBankGlCodes();

	public List<GLCodeDto> findAccountHeadGlCodes(String glCode);

	public List<OfficeGlobalDto> findOfficesWithGlobalNum(Short levelId);

	public List<OfficeGlobalDto> findCustomersWithGlobalNum(Short levelId);

	public List<ViewTransactionsDto> findAccountingTransactions(Date toTrxnDate,Date fromTrxnDate,
			int startRecord, int numberOfRecords);

	public List<RowCount> findTotalNumberOfRecords(Date toTrxnDate,Date fromTrxnDate);

	public String findLastProcessingDate(String namedQueryString);

	public String findLastProcessingDateFirstTime(String namedQueryString);

	public List<MisProcessingTransactionsDto> processMisPostings(
			Date lastProcessDate);

	public List<FinancialYearBO> findFinancialYear();

	public List<GLCodeDto> findGlCodes();

	public void updateLastProcessDate(Date lastProcessDate);

	public boolean savingGeneralLedgerTransaction(GlMasterBO glMasterBO);
	
	public FinancialYearBO savingFinancialYearBO(FinancialYearBO financialYearBO);

	public boolean savingOpenBalancesTransaction(GlBalancesBO balancesBO);

	public <T extends Object> T getPersistedObject(final Class<T> clazz,
			final Serializable persistentObjectId) throws PersistenceException;

	public List<GlBalancesBO> getResultantGlBalancesBO(GlBalancesBO glBalancesBO);
	
	public List<GlBalancesBO> getYearEndGlBalancesBOs(String querystring,int oldFinancialYearId );
	
}

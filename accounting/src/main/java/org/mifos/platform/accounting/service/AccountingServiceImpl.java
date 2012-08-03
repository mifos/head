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

package org.mifos.platform.accounting.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.dao.AccountingDao;
import org.mifos.platform.accounting.tally.TallyXMLGenerator;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountingServiceImpl implements AccountingService {

    private final AccountingDataCacheManager cacheManager;

    private final AccountingDao accountingDao;

    @Autowired
    public AccountingServiceImpl(AccountingDataCacheManager cacheManager, AccountingDao accountingDao) {
        this.cacheManager = cacheManager;
        this.accountingDao = accountingDao;
    }

    @Override
    public final String getExportOutput(LocalDate startDate, LocalDate endDate) {
        String fileName = getExportOutputFileName(startDate, endDate);

        List<AccountingDto> accountingData = getExportDetails(startDate, endDate);

        String output = "NO DATA";
        if (!accountingData.isEmpty()) {
            output = TallyXMLGenerator.getTallyXML(accountingData, fileName);
        }
        return output;
    }

    @Override
    public final List<AccountingDto> getExportDetails(LocalDate startDate, LocalDate endDate) {
        String fileName = cacheManager.getCacheFileName(startDate, endDate);
        if (!cacheManager.isAccountingDataAlreadyInCache(fileName)) {
            if(!writeToCache(startDate, endDate)) {
                return new ArrayList<AccountingDto>();
            }
        }
        return cacheManager.getExportDetails(fileName);
    }

    private boolean writeToCache(LocalDate startDate, LocalDate endDate) {
        List<AccountingDto> accountingData = accountingDao.getAccountingDataByDate(startDate, endDate);
        if (!accountingData.isEmpty()) {
            cacheManager.writeAccountingDataToCache(accountingData, cacheManager.getCacheFileName(startDate, endDate));
            return true;
        }
        return false;
    }

    @Override
    public final String getExportOutputFileName(LocalDate startDate, LocalDate endDate) {
        return cacheManager.getTallyOutputFileName(startDate, endDate);
    }

    @Override
    public final Boolean deleteCacheDir() {
        return cacheManager.deleteCacheDir();
    }

    @Override
    public final Boolean hasAlreadyRanQuery(LocalDate startDate, LocalDate endDate) {
        String fileName = cacheManager.getCacheFileName(startDate, endDate);
        return cacheManager.isAccountingDataAlreadyInCache(fileName);
    }

    @Override
    public List<ExportFileInfo> getLastTenExports(Integer offset) {
        List<ExportFileInfo> exports = new ArrayList<ExportFileInfo>();
        LocalDate startDate = accountingDao.getStartDateOfFinancialTransactions();
        LocalDate endDate = new LocalDate().minusDays(1);
        List<LocalDate> tenTrxn = accountingDao.getTenTxnDate(startDate, endDate, offset);
        for (LocalDate date : tenTrxn) {
            ExportFileInfo export;
            if(hasAlreadyRanQuery(date, date)) {
                export = cacheManager.getExportFileInfoFromCache(date, date);
            } else {
                export = getNotGeneratedExportFileInfo(date, date);
            }
            exports.add(export);
        }
        return exports;
    }

    private ExportFileInfo getNotGeneratedExportFileInfo(LocalDate startDate, LocalDate endDate) {
        String fileName = cacheManager.getExportFileName(startDate, endDate);
        String lastModified = new DateTime().toString("yyyy-MMM-dd HH:mm z");
        Boolean existInCache = false;
        ExportFileInfo export = new ExportFileInfo(lastModified, fileName, startDate.toString(), endDate.toString(), existInCache);
        return export;
    }

    @Override
    public Integer getNumberDaysFromStartOfFinancialTransactions() {
        LocalDate startDate = accountingDao.getStartDateOfFinancialTransactions();
        LocalDate endDate = new LocalDate();
        return accountingDao.getNumberOfTrxnByDate(startDate, endDate);   
        }
}

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

package org.mifos.platform.accounting.service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalDate;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.dao.IAccountingDao;
import org.mifos.platform.accounting.tally.TallyXMLGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountingServiceImpl implements IAccountingService {

    private static final Logger logger = LoggerFactory.getLogger(AccountingServiceImpl.class);

    private AccountingDataCacheManager cacheManager;

    private IAccountingDao accountingDao;

    @Autowired
    public AccountingServiceImpl(AccountingDataCacheManager cacheManager, IAccountingDao accountingDao) {
        this.cacheManager = cacheManager;
        this.accountingDao = accountingDao;
    }

    @Override
    public String getTallyOutputFor(LocalDate startDate, LocalDate endDate) throws Exception {
        String fileName = getTallyOutputFileName(startDate, endDate);

        List<AccountingDto> accountingData = getAccountingDataFor(startDate, endDate);

        String output = "NO DATA";
        if (!accountingData.isEmpty()) {
            output = TallyXMLGenerator.getTallyXML(accountingData, fileName);
        }
        return output;
    }

    @Override
    public List<AccountingDto> getAccountingDataFor(LocalDate startDate, LocalDate endDate) throws Exception {
        String fileName = cacheManager.getCacheFileName(startDate, endDate);

        if (cacheManager.isAccountingDataAlreadyInCache(fileName)) {
            return getAccoutingDataFromCache(fileName);
        }
        List<AccountingDto> accountingData = new ArrayList<AccountingDto>();
        try {
        accountingData = getAccoutningDataFromDatabaseAfterCaching(startDate, endDate);
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage(), e);
        }
        return accountingData;
    }

    private List<AccountingDto> getAccoutningDataFromDatabaseAfterCaching(LocalDate startDate, LocalDate endDate) throws Exception {
        List<AccountingDto> accountingData = accountingDao.getAccountingData(startDate, endDate);
        if (!accountingData.isEmpty()) {
            cacheManager.writeAccountingDataToCache(accountingData, cacheManager.getCacheFileName(startDate, endDate));
        }
        return cacheManager.getAccoutingDataFromCache(cacheManager.getCacheFileName(startDate, endDate));
    }

    private List<AccountingDto> getAccoutingDataFromCache(String fileName) throws Exception {
        return cacheManager.getAccoutingDataFromCache(fileName);
    }

    @Override
    public String getTallyOutputFileName(LocalDate startDate, LocalDate endDate) throws Exception{
       return cacheManager.getTallyOutputFileName(startDate, endDate);
    }

    @Override
    public List<AccountingCacheFileInfo> getAccountingDataCacheInfo() throws Exception{
        return cacheManager.getAccountingDataCacheInfo();
    }

    @Override
    public Boolean deleteCacheDir() {
        return cacheManager.deleteCacheDir();
    }

    @Override
    public Boolean hasAlreadyRanQuery(LocalDate startDate, LocalDate endDate) {
        String fileName = cacheManager.getCacheFileName(startDate, endDate);
        return cacheManager.isAccountingDataAlreadyInCache(fileName);
    }

}

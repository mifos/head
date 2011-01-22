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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.platform.accounting.AccountingDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccountingDataCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(AccountingDataCacheManager.class);

    private String accountingDataPath;

    public List<AccountingDto> getAccoutingDataFromCache(String fileName) throws Exception {

        String accountingDataLocation = getAccoutingDataCachePath();

        File file = new File(accountingDataLocation + fileName);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        // skip first line
        br.readLine();

        List<AccountingDto> accountingData = new ArrayList<AccountingDto>();

        while ((line = br.readLine()) != null) {
            accountingData.add(parseLine(line));
        }
        br.close();
        return accountingData;
    }

    private String getAccoutingDataCachePath() {
        if (accountingDataPath == null) {
            ConfigurationLocator configurationLocator = new ConfigurationLocator();
            accountingDataPath = configurationLocator.getConfigurationDirectory() + "/accounting/data/";
        }
        File path = new File(accountingDataPath);
        if (!(path.exists() && path.isDirectory())) {
            path.mkdirs();
        }
        return accountingDataPath;
    }

    public Boolean deleteCacheDir() {
        try {
            FileUtils.deleteDirectory(new File(getAccoutingDataCachePath()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private int getDigitsAfterDecimal() {
       ConfigurationLocator configurationLocator = new ConfigurationLocator();
       String customApplicationPropertyFile = configurationLocator.getConfigurationDirectory() + "/applicationConfiguration.custom.properties";
       File appConfigFile = new File(customApplicationPropertyFile);
       Properties properties = new Properties();
       if(appConfigFile.exists() && appConfigFile.isFile()) {
           try {
            properties.load(new FileReader(appConfigFile));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
       }
       String digitsAfterDecimal = "1";
       digitsAfterDecimal = properties.getProperty("AccountingRules.DigitsAfterDecimal", digitsAfterDecimal);
       return Integer.parseInt(digitsAfterDecimal);
    }

    protected void setAccoutingDataCachePath(String path) {
        accountingDataPath = path;
    }

    private AccountingDto parseLine(String line) {
        StringTokenizer st = new StringTokenizer(line, ";");
        String branchname = st.nextToken().trim();
        String voucherdate = st.nextToken().trim();
        String vouchertype = st.nextToken().trim();
        String glcode = st.nextToken().trim();
        String glname = st.nextToken().trim();
        String debit = parseNumber(st.nextToken().trim());
        String credit = parseNumber(st.nextToken().trim());
        return new AccountingDto(branchname, voucherdate, vouchertype, glcode, glname, debit, credit);
    }

    private String parseNumber(String number) {
        //FIXME should use this from common util
        StringBuilder pattern = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat();
        for (Short i = 0; i < 14; i++) {
            pattern.append('#');
        }
        pattern.append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
        for (short i = 0; i < getDigitsAfterDecimal(); i++) {
            pattern.append('#');
        }
        decimalFormat.applyLocalizedPattern(pattern.toString());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        decimalFormat.setMinimumFractionDigits(getDigitsAfterDecimal());
        return  decimalFormat.format(Double.parseDouble(number));
    }

    public boolean isAccountingDataAlreadyInCache(String fileName) {
        return new File(getAccoutingDataCachePath() + fileName).isFile();
    }

    public void writeAccountingDataToCache(List<AccountingDto> accountingData, String cacheFileName) throws Exception {
        File file = new File(getAccoutingDataCachePath() + cacheFileName);
        PrintWriter out = new PrintWriter(file);
        out.println("branchname;voucherdate;vouchertype;glcode;glname;debit;credit");
        for (AccountingDto instance : accountingData) {
            out.println(instance.toString());
        }
        out.flush();
        out.close();
    }

    public String getCacheFileName(LocalDate startDate, LocalDate endDate) {
        return startDate + " to " + endDate;
    }

    public String getTallyOutputFileName(LocalDate startDate, LocalDate endDate) {
        return getFilePrefixDefinedByMFI() + getCacheFileName(startDate, endDate) + ".xml";
    }

    public String getFilePrefixDefinedByMFI() {
        return "Mifos Accounting Export ";
    }

    public List<AccountingCacheFileInfo> getAccountingDataCacheInfo() {
        List<AccountingCacheFileInfo> info = new ArrayList<AccountingCacheFileInfo>();
        File directory = new File(getAccoutingDataCachePath());
        for (File file : directory.listFiles()) {
            info.add(new AccountingCacheFileInfo(new DateTime(file.lastModified()), getFilePrefixDefinedByMFI(), file
                    .getName()));
        }
        return info;
    }

}

/*
 * Copyright Grameen Foundation USA
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.platform.accounting.AccountingDto;
import org.mifos.platform.accounting.AccountingRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccountingDataCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountingDataCacheManager.class);

    // hardcoded FIXME there should be a common way of sharing application wide constants across modules
    private static final int DIGITS_BEFORE_DECIMAL = 14;

    private static final String EXPORT_FILENAME_PREFIX = "Mifos Accounting Export ";

    private String accountingDataPath;
    private Integer digitsAfterDecimal;

    public final List<AccountingDto> getExportDetails(String fileName) {

        String accountingDataLocation = getAccoutingDataCachePath();
        File file = new File(accountingDataLocation + fileName);
        return accountingDataFromCache(file);
    }

    public List<AccountingDto> accountingDataFromCache(File file) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            LOGGER.error(file.toString(), e);
            throw new AccountingRuntimeException(file.toString(), e);
        }
        String line = null;

        // skip first line
        try {
            br.readLine();
        } catch (IOException e) {
            LOGGER.error("skipping header line", e);
            throw new AccountingRuntimeException("skipping header line", e);
        }

        List<AccountingDto> accountingData = new ArrayList<AccountingDto>();

        try {
            while ((line = br.readLine()) != null) {
                accountingData.add(parseLine(line));
            }
            br.close();
        } catch (IOException e) {
            throw new AccountingRuntimeException("reading line" + line, e);
        }

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

    public final Boolean deleteCacheDir() {
        try {
            FileUtils.deleteDirectory(new File(getAccoutingDataCachePath()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private int getDigitsAfterDecimal() {
        if (digitsAfterDecimal != null) {
            // Already read, avoid reading again to reduce processing
            return digitsAfterDecimal;
        }
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        String customApplicationPropertyFile = configurationLocator.getConfigurationDirectory()
                + "/applicationConfiguration.custom.properties";
        File appConfigFile = new File(customApplicationPropertyFile);
        Properties properties = new Properties();
        if (appConfigFile.exists() && appConfigFile.isFile()) {
            try {
                properties.load(new FileReader(appConfigFile));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            // FIXME hardcoded default value, using property file only for custom value
            // There should be a way to read application properties across modules
            digitsAfterDecimal = 1;
        }
        digitsAfterDecimal = Integer.parseInt(properties.getProperty("AccountingRules.DigitsAfterDecimal", "1"));
        return digitsAfterDecimal;
    }

    protected final void setAccoutingDataCachePath(String path) {
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
        // FIXME should use this from common util
        StringBuilder pattern = new StringBuilder();
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.ENGLISH);
        for (Short i = 0; i < DIGITS_BEFORE_DECIMAL; i++) {
            pattern.append('#');
        }
        pattern.append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
        for (short i = 0; i < getDigitsAfterDecimal(); i++) {
            pattern.append('#');
        }
        decimalFormat.applyLocalizedPattern(pattern.toString());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        decimalFormat.setMinimumFractionDigits(getDigitsAfterDecimal());
        return decimalFormat.format(Double.parseDouble(number));
    }

    public final boolean isAccountingDataAlreadyInCache(String fileName) {
        return new File(getAccoutingDataCachePath() + fileName).isFile();
    }

    public final void writeAccountingDataToCache(List<AccountingDto> accountingData, String cacheFileName) {
        File file = new File(getAccoutingDataCachePath() + cacheFileName);
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            LOGGER.error(file.toString(), e);
            throw new AccountingRuntimeException(file.toString(), e);
        }
        out.println("branchname;voucherdate;vouchertype;glcode;glname;debit;credit");
        for (AccountingDto instance : accountingData) {
            out.println(instance.toString());
        }
        out.flush();
        out.close();
    }

    public final String getCacheFileName(LocalDate startDate, LocalDate endDate) {
        return new StringBuffer().append(startDate).append(" to ").append(endDate).toString();
    }

    public final String getTallyOutputFileName(LocalDate startDate, LocalDate endDate) {
        return new StringBuffer().append(getExportFileName(startDate, endDate)).append(".xml").toString();
    }

    public final String getExportFileName(LocalDate startDate, LocalDate endDate) {
        return new StringBuffer().append(EXPORT_FILENAME_PREFIX).append(getCacheFileName(startDate, endDate)).toString();
    }

    public final ExportFileInfo getExportFileInfoFromCache(LocalDate startDate, LocalDate endDate) {
        String fileName = getCacheFileName(startDate, endDate);
        File export = new File(getAccoutingDataCachePath() + "/" + fileName);
        return getExportFileInfo(export);
    }

    public final List<ExportFileInfo> getGeneratedExports() {
        List<ExportFileInfo> info = new ArrayList<ExportFileInfo>();
        File directory = new File(getAccoutingDataCachePath());

        for (File file : directory.listFiles()) {
            info.add(getExportFileInfo(file));
        }
        return info;
    }

    protected ExportFileInfo getExportFileInfo(File file) {
        String startDate = file.getName().split(" to ")[0];
        String endDate = file.getName().split(" to ")[1];
        String fileName = new StringBuffer().append(EXPORT_FILENAME_PREFIX).append(file.getName()).toString();
        String lastModified = new DateTime(file.lastModified()).toString("yyyy-MMM-dd HH:mm z");
        Boolean existInCache = true;
        ExportFileInfo export= new ExportFileInfo(lastModified, fileName,  startDate, endDate, existInCache);
        return export;
    }

}

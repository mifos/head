/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;

public class Localization {

    private static Map<String, SupportedLocalesEntity> localeCache;
    private static Locale mainLocale; // the Java locale to match with the
    // config defined locale
    private static Short localeId = -1;
    private static ConfigLocale configLocale; // class with country code and

    // language defined in the config
    // file

    private Localization() {
        localeCache = new ConcurrentHashMap<String, SupportedLocalesEntity>();
        configLocale = new ConfigLocale();
    }

    // After a new configLocale is set, refresh is called to refresh
    // supportedLocale,locale and localeId
    public void refresh() {
        loadMembers();
    }

    private void loadMembers() {
        mainLocale = getLocaleFromConfig();
    }

    // this init has to be called when Mifos starts
    public void init() {
        initializeLocaleCache();
        loadMembers();
        setDefaultLocaleToConfigLocale();
    }

    public Locale getMainLocale() {
        if (mainLocale == null)
            mainLocale = getLocaleFromConfig();
        return mainLocale;
    }

    private static final Localization localization = new Localization();

    public static Localization getInstance() {
        return localization;
    }

    public String getCountryCode() {
        if (mainLocale != null)
            return mainLocale.getCountry();
        else {
            mainLocale = getConfiguredLocale();
            return mainLocale.getCountry();
        }

    }

    // default locale is the machine locale
    private void setDefaultLocaleToConfigLocale() {
        Locale.setDefault(mainLocale);
    }

    // for the testing purpose
    public void clearCountryCodeLanguageCodeFromConfigFile() {
        configLocale.clearCountryCode();
        configLocale.clearLanguageCode();
        configLocale = null;
    }

    // for the testing purpose
    public void setCountryCodeLanguageCodeToConfigFile(ConfigLocale locale) {
        configLocale = locale;
        configLocale.setCountryCodeToConfigFile();
        configLocale.setLanguageCodeToConfigFile();
        refresh();
    }

    public String getLanguageCode() {
        if (mainLocale != null)
            return mainLocale.getLanguage();
        else {
            mainLocale = getConfiguredLocale();
            return mainLocale.getLanguage();
        }

    }

    public String getLanguageName() {

        if (mainLocale != null)
            return mainLocale.getDisplayLanguage();
        else {
            mainLocale = getConfiguredLocale();
            return mainLocale.getDisplayLanguage();
        }
    }

    public String getCountryName() {

        if (mainLocale != null)
            return mainLocale.getDisplayCountry();
        else {
            mainLocale = getConfiguredLocale();
            return mainLocale.getDisplayCountry();
        }

    }

    public Short getLocaleId() {
        if (localeId > -1)
            return localeId;
        localeId = getConfiguredLocaleId();
        return localeId;
    }

    public Locale getConfiguredLocale() {
        if (mainLocale != null)
            return mainLocale;
        else {
            mainLocale = getLocaleFromConfig();
            return mainLocale;
        }

    }

    private short getConfiguredLocaleId() {
        short localeId = -1;
        Object[] locales = localeCache.values().toArray();
        if (locales.length == 0)
            localeId = 1; // default to English at the beginning when cache is
        // not ready
        for (int i = 0; i < locales.length; i++) {
            SupportedLocalesEntity localeEntity = (SupportedLocalesEntity) locales[i];
            if (localeEntity.getCountryCode().equalsIgnoreCase(configLocale.getCountryCode())
                    && localeEntity.getLanguageCode().equalsIgnoreCase(configLocale.getLanguageCode())) {
                localeId = localeEntity.getLocaleId();
                break;
            }
        }
        return localeId;

    }

    /**
     * from the language code and country code defined in the config return the
     * java Locale class instantiated using the language code and country code
     */
    private Locale getLocaleFromConfig() {

        if (configLocale == null)
            configLocale = new ConfigLocale();
        // need to check if this configLocale is supported by Mifos
        if ((localeId = getConfiguredLocaleId()) == -1)
            throw new RuntimeException("This configured locale: language code " + configLocale.getLanguageCode()
                    + ", country code " + configLocale.getCountryCode() + " is not supported by Mifos.");
        Locale locale = new Locale(configLocale.getLanguageCode().toLowerCase(), configLocale.getCountryCode()
                .toUpperCase());
        return locale;

    }

    public ArrayList<Short> getSupportedLocaleIds() {
        ArrayList<Short> localeIds = new ArrayList<Short>();
        Object[] locales = localeCache.values().toArray();
        for (int i = 0; i < locales.length; i++) {
            SupportedLocalesEntity entity = (SupportedLocalesEntity) locales[i];
            localeIds.add(entity.getLocaleId());
        }
        return localeIds;
    }

    private void initializeLocaleCache() {
        List<SupportedLocalesEntity> locales = new ApplicationConfigurationPersistence().getSupportedLocale();
        localeCache.clear();
        for (SupportedLocalesEntity locale : locales) {
            localeCache.put(locale.getLanguage().getLanguageShortName().toLowerCase() + "_"
                    + locale.getCountry().getCountryShortName().toUpperCase(), locale);
        }

    }

    public static ConfigLocale getConfigLocale() {
        return configLocale;
    }

    public void setConfigLocale(ConfigLocale locale) {
        if (configLocale.equals(locale) == false) {
            Localization.configLocale = locale;
            refresh();
        }
    }

}

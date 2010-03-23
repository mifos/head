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

package org.mifos.config;

public class ConfigLocale {

    public static final String DEFAULT_DIRECTION = "auto";

    private static final String LocalizationCountryCode = "Localization.CountryCode";
    private static final String LocalizationLanguageCode = "Localization.LanguageCode";
    private static final String LocalizationDirection = "Localization.Direction";

    private String countryCode;
    private String languageCode;
    private String direction;

    public ConfigLocale(String languageCode, String countryCode) {
        this.countryCode = countryCode;
        this.languageCode = languageCode;
    }

    public ConfigLocale() {
        load();
    }

    public void clearCountryCode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.clearProperty(LocalizationCountryCode);
    }

    public void clearLanguageCode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.clearProperty(LocalizationLanguageCode);
    }

    public void clearDirection() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.clearProperty(LocalizationDirection);
    }

    public void setCountryCodeToConfigFile() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.addProperty(LocalizationCountryCode, countryCode);
    }

    public void setLanguageCodeToConfigFile() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.addProperty(LocalizationLanguageCode, languageCode);
    }

    public void setDirectionToConfigFile() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.addProperty(LocalizationDirection, direction);
    }

    private void load() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(LocalizationCountryCode)) {
            countryCode = configMgr.getString(LocalizationCountryCode);
        } else {
            throw new RuntimeException("The country code is not defined in the config file.");
        }
        if (configMgr.containsKey(LocalizationLanguageCode)) {
            languageCode = configMgr.getString(LocalizationLanguageCode);
        } else {
            throw new RuntimeException("The language code is not defined in the config file.");
        }
        direction = configMgr.getString(LocalizationDirection, DEFAULT_DIRECTION);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

}

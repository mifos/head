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

package org.mifos.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ListElement;

import edu.emory.mathcs.backport.java.util.Collections;

public class Localization {

    public static final short ENGLISH_LOCALE_ID = 1;
    public static Locale SPANISH = new Locale("es", "ES");
    private final Map<Short, Locale> LOCALE_MAP = new HashMap<Short, Locale>();
    protected final Short newLocaleId = 14;
    private LocaleSetting localeSetting;
    private Locale configuredLocale; // the Java locale to match with the
    private Short configuredLocaleId;
    private static Localization localization;

    private Localization() {
        localeSetting = new LocaleSetting();
        setLocaleMap();
        readLoacaleSetting();
    }

    public static final Localization getInstance() {
        if(localization == null) {
            localization = new Localization();
        }
        return localization;
    }

    public String getDisplayName(Short localeId) {
        Locale locale = LOCALE_MAP.get(localeId);
        String country = locale.getDisplayCountry();
        if(StringUtils.isBlank(country)) {
            return locale.getDisplayLanguage() +" - ("+locale.getDisplayLanguage(locale) +") ["+locale+"]";
        }
        return locale.getDisplayLanguage() +" - ("+locale.getDisplayLanguage(locale) +") : "+ country +" ["+locale+"]";
    }

    public Short getConfiguredLocaleId() {
        return configuredLocaleId;
    }

    public Locale getConfiguredLocale() {
        return configuredLocale;
    }

    public List<ValueListElement> getLocaleForUI() {
        List<ValueListElement> localeForUI = new ArrayList<ValueListElement>();
        for(Short key: LOCALE_MAP.keySet()) {
           String displayName = getDisplayName(key);
           ValueListElement localeValue =  new BusinessActivityEntity(key.intValue(), displayName, displayName);
           localeForUI.add(localeValue);
           Collections.sort(localeForUI, new ValueListElementSortByName());
          }
        return localeForUI;
    }

    public List<ListElement> getLocaleList() {
        List<ListElement> localeForUI = new ArrayList<ListElement>();
        for(Short key: LOCALE_MAP.keySet()) {
           String displayName = getDisplayName(key);
           ListElement localeValue =  new ListElement(key.intValue(), displayName);
           localeForUI.add(localeValue);
           Collections.sort(localeForUI, new ListElementSortByName());
          }
        return localeForUI;
    }

    public Set<Short> getLocaleIdSet() {
        return LOCALE_MAP.keySet();
    }

    public void setConfigLocale(LocaleSetting localeSetting) {
        this.localeSetting = localeSetting;
        LOCALE_MAP.clear();
        setLocaleMap();
        readLoacaleSetting();
    }

    public Short getLocaleId(Locale locale) {
            for (Short key : LOCALE_MAP.keySet()) {
                if (LOCALE_MAP.get(key).equals(locale)) {
                    return key;
                }
            } // this might never be thrown
            throw new IllegalArgumentException("No language configured for "+locale);
    }

    private void readLoacaleSetting() {
        String lang = localeSetting.getLanguageCode().toLowerCase();
        String country = localeSetting.getCountryCode().toUpperCase();
        Locale locale = new Locale(lang, country);

        if(!LOCALE_MAP.containsValue(locale)) {
            configuredLocaleId = newLocaleId;
            configuredLocale = locale;
            LOCALE_MAP.put(newLocaleId, locale);
        } else {
            configuredLocaleId = getLocaleId(locale);
            configuredLocale = locale;
        }
    }

    private synchronized void setLocaleMap() {
        if (LOCALE_MAP.isEmpty()) {
            LOCALE_MAP.put((short) 1, Locale.UK);  // These were the options in personnel language list (pre Mifos 2.2)
            LOCALE_MAP.put((short) 2, new Locale("is", "IS"));  // Icelandic
            LOCALE_MAP.put((short) 3, SPANISH);  // Spanish
            LOCALE_MAP.put((short) 4, Locale.FRANCE);  // French
            LOCALE_MAP.put((short) 5, Locale.SIMPLIFIED_CHINESE);  // Chinese
            LOCALE_MAP.put((short) 6, new Locale("sw", "KE"));  // Swahili
            LOCALE_MAP.put((short) 7, new Locale("ar", "DZ")); // Arabic
            LOCALE_MAP.put((short) 8, new Locale("pt", "AO")); // Portuguese
            LOCALE_MAP.put((short) 9, new Locale("km", "KH")); // Khmer
            LOCALE_MAP.put((short) 10, new Locale("lo", "LA")); // Lola
            LOCALE_MAP.put((short) 11, new Locale("hu", "HU")); // Hungarian
            LOCALE_MAP.put((short) 12, new Locale("te", "IN")); // Tegulu
            LOCALE_MAP.put((short) 13, new Locale("hi", "IN")); // Hindi
        }
    }

    public Locale getLocaleById(Short id) {
        return LOCALE_MAP.get(id);
    }

    class ValueListElementSortByName implements Comparator<ValueListElement>{
        @Override
        public int compare(ValueListElement o1, ValueListElement o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    class ListElementSortByName implements Comparator<ListElement>{
        @Override
        public int compare(ListElement o1, ListElement o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}

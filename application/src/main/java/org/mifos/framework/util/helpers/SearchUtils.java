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

package org.mifos.framework.util.helpers;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.WordUtils;
import org.mifos.framework.util.DateTimeService;

public class SearchUtils {
    private static final short LookUpNameLength = 100;

    public SearchUtils() {
    }

    public static String getMessageWithSubstitution(String bundleName, Locale userLocale, String key, Object[] args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName, userLocale);
        String label = resourceBundle.getString(key);
        if (args != null) {
            MessageFormat formatter = new MessageFormat(label);
            label = formatter.format(args);
        }
        return label;
    }

    public static String normalizeSearchString(String searchString) {
        String searchStr = searchString.trim();
        if (searchString.contains("%") && searchString.length() > 1)
            return searchStr.replace("%", "\\%");
        return searchStr;
    }

    /*
     * Create a camelcase token from a string containing multiple whitespace
     * separated tokens.
     * 
     * For example "aBc dEF_gh-iJ  k.l" becomes "AbcDef_ghIjKL"
     */
    protected static String camelCase(String string) {
        return org.apache.commons.lang.StringUtils.deleteWhitespace(WordUtils.capitalize(string.toLowerCase()
                .replaceAll("\\W", " ")));
    }

    // add test case
    public static String generateLookupName(String type, String newElementText) {
        String name = type + "." + camelCase(newElementText) + "."
                + new DateTimeService().getCurrentDateTime().getMillis();
        if (name.length() > LookUpNameLength)
            name = name.substring(0, LookUpNameLength);
        return name;
    }
}

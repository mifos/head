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

package org.mifos.framework.persistence;

import org.apache.commons.lang.StringUtils;

public class HardcodedValues {

    private static final String LOOKUP_VALUE = "insert into lookup_value";
    private static final String LOOKUP_ID = "lookup_id";
    private static final String LOOKUP_VALUE_LOCALE = "insert into lookup_value_locale";
    private static final String LOCALE_ID = "locale_id";

    /*
     * This checks that a sql script does not try to insert a hardcoded
     * lookup_id into the lookup_value table
     */
    public static boolean checkLookupValue(String sql) {
        if (StringUtils.containsIgnoreCase(sql, LOOKUP_VALUE) && StringUtils.containsIgnoreCase(sql, LOOKUP_ID)) {
            return false;
        }
        return true;
    }

    /*
     * This checks that a sql script does not try to insert a hardcoded
     * locale_id into the lookup_value_locale table
     */
    public static boolean checkLookupValueLocale(String sql) {
        if (StringUtils.containsIgnoreCase(sql, LOOKUP_VALUE_LOCALE) && StringUtils.containsIgnoreCase(sql, LOCALE_ID)) {
            return false;
        }
        return true;
    }
}

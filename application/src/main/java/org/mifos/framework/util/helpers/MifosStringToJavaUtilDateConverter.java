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

import java.util.Locale;

import org.apache.commons.beanutils.Converter;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.InvalidDateException;

public class MifosStringToJavaUtilDateConverter implements Converter {

    private Locale locale = null;

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public MifosStringToJavaUtilDateConverter() {
    }

    public Object convert(Class type, Object value) {
        java.util.Date date = null;

        if (locale != null && value != null && type != null && !value.equals("")) {
            try {
                date = new java.util.Date(DateUtils.getLocaleDate(locale, ((String) value)).getTime());
            } catch (InvalidDateException e) {
                throw new MifosRuntimeException(e);
            }
        }
        return date;
    }

}

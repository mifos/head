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
package org.mifos.reports.pentaho.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReflectionUtil {

    public static Object parseStringToClass(String string, Class<?> clazz) throws ReflectionException {
        Object val = null;
        if (string != null && !string.equals("")) {
            try {
                Constructor<?> constr;
                if (clazz.equals(Number.class)) {
                    constr = BigDecimal.class.getConstructor(String.class);
                } else {
                    constr = clazz.getConstructor(String.class);
                }
                val = constr.newInstance(string);
            } catch (Exception ex) {
                throw new ReflectionException(clazz, string);
            }
        }
        return val;
    }

    public static Object parseStringsToClass(List<String> strings, Class<?> clazz) throws ReflectionException {
        Object array = null;
        if (strings != null) {
            array = Array.newInstance(clazz, strings.size());
            for (int i = 0; i < strings.size(); i++) {
                Object val = parseStringToClass(strings.get(i), clazz);
                Array.set(array, i, val);
            }
        }
        return array;
    }

    public static Object parseStringsToClass(String[] strings, Class<?> clazz) throws ReflectionException {
        Object val = null;
        if (strings != null) {
            val = parseStringsToClass(Arrays.asList(strings), clazz);
        }
        return val;
    }

    public static Object parseDateToClass(Date date, Class<?> clazz) throws ReflectionException {
        Object val = null;
        if (date != null) {
            try {
                Constructor<?> constr = clazz.getConstructor(long.class);
                val = constr.newInstance(date.getTime());
            } catch (Exception ex) {
                throw new ReflectionException(clazz, date.toString());
            }
        }
        return val;
    }
}

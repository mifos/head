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

package org.mifos.framework.hibernate.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;

public class HibernateHelperIntegrationTest extends MifosIntegrationTestCase {

    public HibernateHelperIntegrationTest() throws Exception {
        super();
    }

    public void testDTOBuilderCapitalize() {
        DTOBuilder dtoBuilder = new DTOBuilder();
       Assert.assertEquals("String", dtoBuilder.capitalize("string"));
       Assert.assertEquals("", dtoBuilder.capitalize(""));
    }

    public void testDTOBuilderGetParameterTypes() throws Exception {
        DTOBuilder dtoBuilder = new DTOBuilder();
        Class[] classParams = dtoBuilder.getParameterTypes("long");
       Assert.assertTrue(classParams[0].equals(Long.TYPE));
        classParams = dtoBuilder.getParameterTypes("integer");
       Assert.assertTrue(classParams[0].equals(Integer.TYPE));
        classParams = dtoBuilder.getParameterTypes("string");
       Assert.assertTrue(classParams[0].equals(new String("").getClass()));
        classParams = dtoBuilder.getParameterTypes("date");
       Assert.assertTrue(classParams[0].equals(new java.util.GregorianCalendar().getClass().getSuperclass()));
        classParams = dtoBuilder.getParameterTypes("double");
       Assert.assertTrue(classParams[0].equals(Double.TYPE));
        classParams = dtoBuilder.getParameterTypes("short");
       Assert.assertTrue(classParams[0].equals(Short.TYPE));
        classParams = dtoBuilder.getParameterTypes("CHAR");
       Assert.assertTrue(classParams[0].equals(Character.TYPE));
        classParams = dtoBuilder.getParameterTypes("TIMESTAMP");
       Assert.assertTrue(classParams[0].equals(new java.sql.Timestamp(System.currentTimeMillis()).getClass()));
        classParams = dtoBuilder.getParameterTypes("TIME");
       Assert.assertTrue(classParams[0].equals(new java.sql.Time(System.currentTimeMillis()).getClass()));
        classParams = dtoBuilder.getParameterTypes("invalidDataType");
        Assert.assertNull(classParams);
    }

    public void testDTOBuilderGetValue() {
        DTOBuilder dtoBuilder = new DTOBuilder();
       Assert.assertEquals("string", dtoBuilder.getValue("string", "string"));
        Long l = System.currentTimeMillis();
        Date date = new Date(l);
        Calendar c = new GregorianCalendar();
        Calendar cal = (Calendar) dtoBuilder.getValue(new Date(l), "date");
        c.setTime(date);
       Assert.assertEquals(c.getTime(), cal.getTime());
    }

}

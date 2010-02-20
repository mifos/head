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

public class MifosSqlDateConverter implements Converter {

    private Locale locale = null;

    /**
     * @param locale locale.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public MifosSqlDateConverter() {
    }

    public Object convert(Class type, Object value) {
        java.sql.Date date = null;
        /*
         * TODO: Get rid of this comment
         * 
         * if(locale!=null && value!=null && type!=null && !value.equals("")){
         * try{
         * 
         * SimpleDateFormat sdf =
         * (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT,
         * locale); String userfmt = ((SimpleDateFormat) sdf).toPattern();
         * String[]
         * dayMnthYear=DateHelper.getDayMonthYear((String)value,DateHelper
         * .convertToDateTagFormat(userfmt)); //java.util.Date parsedDate =
         * sdf.parse((String)value); date = new java.sql.Date(new
         * GregorianCalendar(Integer.parseInt(dayMnthYear[2]),
         * Integer.parseInt(dayMnthYear[1])-1,Integer.parseInt(
         * dayMnthYear[0])).getTimeInMillis()); String
         * dbDate=DateHelper.convertUserToDbFmt((String)value,userfmt);
         * date=java.sql.Date.valueOf(dbDate);
         * 
         * }catch(Exception parsee){ //TODO Exception handling and remove print
         * stack trace parsee.printStackTrace(); //date= new java.sql.Date(0l);
         * } } return date;
         */
        if (locale != null && value != null && type != null && !value.equals("")) {
            try {
                date = DateUtils.getLocaleDate(locale, ((String) value));
            } catch (InvalidDateException ide) {
                throw new MifosRuntimeException(ide);
            }
        }
        return date;
    }

}

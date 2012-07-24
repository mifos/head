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
package org.mifos.reports.pentaho.params;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

public class PentahoDateParameter extends AbstractPentahoParameter {
    private String dateDD;
    private String dateMM;
    private String dateYY;

    public String getDateDD() {
        return dateDD;
    }

    public void setDateDD(String dateDD) {
        this.dateDD = dateDD;
    }

    public String getDateMM() {
        return dateMM;
    }

    public void setDateMM(String dateMM) {
        this.dateMM = dateMM;
    }

    public String getDateYY() {
        return dateYY;
    }

    public void setDateYY(String dateYY) {
        this.dateYY = dateYY;
    }

    public LocalDate getDate() {
        LocalDate date = null;
        if (isDateEntered()) {
            Integer day = Integer.parseInt(dateDD);
            Integer month = Integer.parseInt(dateMM);
            Integer year = Integer.parseInt(dateYY);
            date = new LocalDate(year, month, day);
        }
        return date;
    }

    public void setDate(LocalDate date) {
        this.dateDD = String.valueOf(date.getDayOfMonth());
        this.dateMM = String.valueOf(date.getMonthOfYear());
        this.dateYY = String.valueOf(date.getYear());
    }

    public boolean isDateEntered() {
        return StringUtils.isNotBlank(dateDD) || StringUtils.isNotBlank(dateMM) || StringUtils.isNotBlank(dateYY);
    }

    @Override
    public Object getParamValue() {
        return getDate();
    }
}

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

import org.joda.time.LocalDate;

public class PentahoDateParameter extends AbstractPentahoParameter {
    private Integer dateDD;
    private Integer dateMM;
    private Integer dateYY;

    public Integer getDateDD() {
        return dateDD;
    }

    public void setDateDD(Integer dateDD) {
        this.dateDD = dateDD;
    }

    public Integer getDateMM() {
        return dateMM;
    }

    public void setDateMM(Integer dateMM) {
        this.dateMM = dateMM;
    }

    public Integer getDateYY() {
        return dateYY;
    }

    public void setDateYY(Integer dateYY) {
        this.dateYY = dateYY;
    }

    public LocalDate getDate() {
        LocalDate result = null;
        if (dateDD != null && dateMM != null && dateYY != null) {
            result = new LocalDate(dateYY, dateMM, dateDD);
        }
        return result;
    }

    public void setDate(LocalDate date) {
        this.dateDD = date.getDayOfMonth();
        this.dateMM = date.getMonthOfYear();
        this.dateYY = date.getYear();
    }

    public boolean isdDateValid() {
        boolean result = true;
        try {
            getDate();
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    @Override
    public Object getParamValue() {
        return getDate();
    }
}

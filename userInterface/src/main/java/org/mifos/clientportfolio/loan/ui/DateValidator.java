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

package org.mifos.clientportfolio.loan.ui;

import org.joda.time.DateTime;

public class DateValidator {

    public boolean formsValidDate(Number dayOfMonth, Number monthOfYear, Number year) {
        boolean isValid = true;
        try {
            new DateTime().withDate(year.intValue(), monthOfYear.intValue(), dayOfMonth.intValue());
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;
    }
    
    public boolean formsValidDate(String dayOfMonth, String monthOfYear, String year) {
        boolean isValid = true;
        try {
            int dom = Integer.parseInt(dayOfMonth);
            int moy = Integer.parseInt(monthOfYear);
            int y = Integer.parseInt(year);
            isValid = formsValidDate(dom, moy, y);
        } catch (NumberFormatException e) {
            isValid = false;
        }
        return isValid;
    }
}
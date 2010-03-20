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

package org.mifos.customers.util.helpers;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.DateUtils;

public class CustomerRecentActivityView extends View {

    public CustomerRecentActivityView(java.util.Date activityDate, String description, String amount, String postedBy) {
        this.activityDate = new java.sql.Date(activityDate.getTime());
        this.description = description;
        this.amount = amount;
        this.postedBy = postedBy;
    }

    public CustomerRecentActivityView() {
    }

    private Date activityDate;

    private String description;

    private String amount = "-";

    private String postedBy = "-";

    private Locale locale = null;

    private String userPrefferedDate = null;

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getUserPrefferedDate() {
        return DateUtils.getUserLocaleDate(getLocale(), getActivityDate().toString());
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}

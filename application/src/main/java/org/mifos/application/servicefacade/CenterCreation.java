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

package org.mifos.application.servicefacade;

import java.util.Locale;

public class CenterCreation {

    private final Short officeId;
    private final Short loggedInUserId;
    private final Short loggedInUserLevelId;
    private final Locale locale;

    public CenterCreation(Short officeId, Short userId, Short userLevelId, Locale locale) {
        this.officeId = officeId;
        this.loggedInUserId = userId;
        this.loggedInUserLevelId = userLevelId;
        this.locale = locale;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Short getLoggedInUserId() {
        return this.loggedInUserId;
    }

    public Short getLoggedInUserLevelId() {
        return this.loggedInUserLevelId;
    }

    public Locale getLocale() {
        return this.locale;
    }
}


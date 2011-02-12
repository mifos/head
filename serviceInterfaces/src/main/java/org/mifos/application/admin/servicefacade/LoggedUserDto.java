/*
 * Copyright Grameen Foundation USA
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

package org.mifos.application.admin.servicefacade;

public class LoggedUserDto {
    private final String offices;
    private final String names;
    private final String activityTime;
    private final String activityContext;

    public LoggedUserDto(String offices, String names, String activityTime, String activityContext) {
        this.offices = offices;
        this.names = names;
        this.activityTime = activityTime;
        this.activityContext = activityContext;
    }

    public String getOffices() {
        return offices;
    }

    public String getNames() {
        return names;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public String getActivityContext() {
        return activityContext;
    }
}

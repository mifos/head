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

import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class LoginActivityDto {

    private final UserContext userContext;
    private final ActivityContext activityContext;
    private final Short passwordChangedFlag;

    public LoginActivityDto(UserContext userContext, ActivityContext activityContext, Short passwordChangedFlag) {
        this.userContext = userContext;
        this.activityContext = activityContext;
        this.passwordChangedFlag = passwordChangedFlag;
    }

    public UserContext getUserContext() {
        return this.userContext;
    }

    public ActivityContext getActivityContext() {
        return this.activityContext;
    }

    public Short getPasswordChangedFlag() {
        return this.passwordChangedFlag;
    }

    public boolean isPasswordChanged() {
        return this.passwordChangedFlag > 0;
    }
}
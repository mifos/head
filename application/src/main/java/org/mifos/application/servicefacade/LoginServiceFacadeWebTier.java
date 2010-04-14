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

import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.authentication.AuthenticationDao;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class LoginServiceFacadeWebTier implements LoginServiceFacade {

    private final AuthenticationDao personnelDao;

    public LoginServiceFacadeWebTier(AuthenticationDao personnelDao) {
        this.personnelDao = personnelDao;
    }

    @Override
    public LoginActivityDto login(String username, String password) throws ApplicationException {

        PersonnelBO user = this.personnelDao.findPersonnelByUsername(username);
        if (user == null) {
            throw new ApplicationException(LoginConstants.KEYINVALIDUSER);
        }

        UserContext userContext = user.login(password);

        ActivityContext activityContext = new ActivityContext(Short.valueOf("0"), user.getOffice().getOfficeId(), user.getPersonnelId());

        return new LoginActivityDto(userContext, activityContext, user.getPasswordChanged());
    }
}
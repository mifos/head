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
import org.mifos.customers.personnel.business.service.PersonnelService;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ChangePasswordRequest;
import org.mifos.dto.domain.LoginDto;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.service.BusinessRuleException;

/**
 *
 */
public class LoginServiceFacadeWebTier implements NewLoginServiceFacade {

    private final PersonnelService personnelService;
    private final PersonnelDao personnelDao;

    public LoginServiceFacadeWebTier(PersonnelService personnelService, PersonnelDao personnelDao) {
        this.personnelService = personnelService;
        this.personnelDao = personnelDao;
    }

    @Override
    public LoginDto login(String username, String password) {

        PersonnelBO user = this.personnelDao.findPersonnelByUsername(username);
        if (user == null) {
            throw new BusinessRuleException(LoginConstants.KEYINVALIDUSER);
        }

        try {
            user.login(password);

            boolean isPasswordChanged = user.getPasswordChanged() == Constants.YES ? true : false;
            if (isPasswordChanged) {
                user.updateLastPersonnelLoggedin();
            }

            return new LoginDto(user.getPersonnelId(), user.getOffice().getOfficeId(), isPasswordChanged);
        } catch (PersonnelException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        PersonnelBO user = this.personnelDao.findPersonnelByUsername(changePasswordRequest.getUsername());

        this.personnelService.changePassword(user, changePasswordRequest.getNewPassword());
    }
}
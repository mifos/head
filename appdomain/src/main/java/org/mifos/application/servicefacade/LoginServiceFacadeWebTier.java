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

package org.mifos.application.servicefacade;

import java.util.List;
import java.util.Locale;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelService;
import org.mifos.customers.personnel.exceptions.PersonnelException;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ChangePasswordRequest;
import org.mifos.dto.domain.LoginDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.security.MifosUser;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 */
public class LoginServiceFacadeWebTier implements NewLoginServiceFacade {

    private final PersonnelService personnelService;
    private final PersonnelDao personnelDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();
    
    private static final String COUNTRY_CODE = "Localization.CountryCode";
    private static final String LANGUAGE_CODE = "Localization.LanguageCode";

    @Autowired
    public LoginServiceFacadeWebTier(PersonnelService personnelService, PersonnelDao personnelDao) {
        this.personnelService = personnelService;
        this.personnelDao = personnelDao;
    }

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;
    
    @Override
    public LoginDto login(String username, String password) {

        PersonnelBO user = this.personnelDao.findPersonnelByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(LoginConstants.KEYINVALIDUSER);
        }
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        List<ValueListElement> localeList = personnelServiceFacade.getDisplayLocaleList();
        Locale preferredLocale = new Locale(configMgr.getString(LANGUAGE_CODE), configMgr.getString(COUNTRY_CODE));
        String listElement = "[" + preferredLocale.toString() + "]";
        Short localeId = user.getPreferredLocale();
        
        for (ValueListElement element : localeList) {
            if (element.getName().contains(listElement)) {
                localeId = element.getId().shortValue();
                break;
            }
        }
        
        user.setPreferredLocale(localeId);
        UserContext userContext = new UserContext();
        userContext.setPreferredLocale(preferredLocale);
        userContext.setLocaleId(localeId);
        userContext.setId(user.getPersonnelId());
        userContext.setName(user.getDisplayName());
        userContext.setLevel(user.getLevelEnum());
        userContext.setRoles(user.getRoles());
        userContext.setLastLogin(user.getLastLogin());
        userContext.setPasswordChanged(user.getPasswordChanged());
        userContext.setBranchId(user.getOffice().getOfficeId());
        userContext.setBranchGlobalNum(user.getOffice().getGlobalOfficeNum());
        userContext.setOfficeLevelId(user.getOffice().getLevel().getId());


        user.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(user);
            user.login(password);
            this.personnelDao.save(user);
            this.transactionHelper.commitTransaction();

            return new LoginDto(user.getPersonnelId(), user.getOffice().getOfficeId(), user.isPasswordChanged());
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        PersonnelBO user = this.personnelDao.findPersonnelByUsername(changePasswordRequest.getUsername());

        this.personnelService.changePassword(user, changePasswordRequest.getNewPassword());
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {

        MifosUser appUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(appUser);

        PersonnelBO user = this.personnelDao.findPersonnelByUsername(username);
        boolean passwordIsAlreadyChanged = user.isPasswordChanged();

        user.updateDetails(userContext);
        try {
            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(user);
            user.updatePassword(oldPassword, newPassword);
            this.personnelDao.save(user);
            this.transactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }

        return passwordIsAlreadyChanged;
    }

	@Override
	public boolean checkOldPassword(String username, String oldPassword) {
		try {
			return this.personnelDao.findPersonnelByUsername(username).isPasswordValid(oldPassword);
		} catch (PersonnelException e) {
			return false;
		}
	}
}
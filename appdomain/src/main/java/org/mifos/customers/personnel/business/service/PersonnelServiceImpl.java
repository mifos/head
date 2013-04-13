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

package org.mifos.customers.personnel.business.service;

import java.util.ArrayList;
import java.util.Date;

import org.joda.time.LocalDateTime;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.config.PasswordRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelUsedPasswordEntity;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.security.authentication.EncryptionService;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelDao personnelDao;
    private final HibernateTransactionHelper hibernateTransactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Autowired
    public PersonnelServiceImpl(PersonnelDao personnelDao) {
        this.personnelDao = personnelDao;
    }

    @Override
    public void changePassword(PersonnelBO user, String newPassword, boolean setPasswordChanged) {

        UserContext userContext = new UserContext();
        userContext.setId(user.getPersonnelId());
        userContext.setName(user.getUserName());
        user.updateDetails(userContext);

        validateIfPasswordIsRecentlyUsed(user, newPassword);
        
    	byte[] newEncPass = EncryptionService.getInstance().createEncryptedPassword(newPassword);
    	
        PersonnelUsedPasswordEntity personnelUsedPassword;
        int passwordHistoryCount = PasswordRules.getPasswordHistoryCount();
        
        if (user.getPersonnelUsedPasswords().size() >= passwordHistoryCount) {
        	personnelUsedPassword = new ArrayList<PersonnelUsedPasswordEntity>(user.getPersonnelUsedPasswords()).get(0);
        	personnelUsedPassword.setUsedPassword(newEncPass);
            personnelUsedPassword.setDateChanged(new LocalDateTime().toDateTime().toDate());
        } else {
        	personnelUsedPassword = new PersonnelUsedPasswordEntity();
        	personnelUsedPassword.setPersonnel(user);
            personnelUsedPassword.setUsedPassword(newEncPass);
            personnelUsedPassword.setDateChanged(new LocalDateTime().toDateTime().toDate());
        	user.getPersonnelUsedPasswords().add(personnelUsedPassword);
        }
        
        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(user);

            user.changePasswordTo(newPassword, user.getPersonnelId(), setPasswordChanged);
            this.personnelDao.save(user);

            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    private void validateIfPasswordIsRecentlyUsed(PersonnelBO user, String newPassword) {
    	
    	for (PersonnelUsedPasswordEntity usedPass: user.getPersonnelUsedPasswords()) {
    		if (EncryptionService.getInstance().verifyPassword(newPassword, usedPass.getUsedPassword())) {
    			throw new BusinessRuleException(AccountExceptionConstants.PASSWORD_USED_EXCEPTION);
    		}
    	}
    	
    }
    
	@Override
	public void changePasswordExpirationDate(PersonnelBO user, Date passwordExpirationDate) {
		UserContext userContext = new UserContext();
		userContext.setId(user.getPersonnelId());
		userContext.setName(user.getUserName());
		user.updateDetails(userContext);

		try {
			hibernateTransactionHelper.startTransaction();
			hibernateTransactionHelper.beginAuditLoggingFor(user);
			user.setPasswordExpirationDate(passwordExpirationDate);
			this.personnelDao.save(user);

			hibernateTransactionHelper.commitTransaction();
		} catch (Exception e) {
			hibernateTransactionHelper.rollbackTransaction();
			throw new MifosRuntimeException(e);
		} finally {
			hibernateTransactionHelper.closeSession();
		}
	}
}
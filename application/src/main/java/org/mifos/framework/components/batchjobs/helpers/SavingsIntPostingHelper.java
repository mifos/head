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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.config.Localization;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.security.MifosUser;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class SavingsIntPostingHelper extends TaskHelper {

    private SavingsServiceFacade savingsServiceFacade = ApplicationContextProvider.getBean(SavingsServiceFacade.class);

    public SavingsIntPostingHelper() {
        super();
    }

    @Override
    public void execute(final long scheduledFireTime) throws BatchJobException {

        MifosUser principal = createMifosAdminUser();

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            securityContext = new SecurityContextImpl();
            SecurityContextHolder.setContext(securityContext);
        }
        if (securityContext.getAuthentication() == null || !securityContext.getAuthentication().isAuthenticated()) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal, principal.getAuthorities());
            securityContext.setAuthentication(authentication);
        }

        LocalDate dateOfBatchJob = new LocalDate(scheduledFireTime);

        List<String> errorList = new ArrayList<String>();
        try {
            this.savingsServiceFacade.postInterestForLastPostingPeriod(dateOfBatchJob);
        } catch (BusinessRuleException e) {
            errorList.add(e.getMessageKey());
        }

        if (errorList.size() > 0) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    private MifosUser createMifosAdminUser() {
        Integer userId = Integer.valueOf(1);
        Short branchId = Short.valueOf("1");
        String username = "mifos";
        //FIXME hardcoded default user
        byte[] password = "testmifos".getBytes();
        boolean enabled  = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        List<Short> roleIds = new ArrayList<Short>();

       return new MifosUser(userId, branchId, PersonnelLevel.LOAN_OFFICER.getValue(), roleIds, username, password, enabled,
                            accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, Localization.ENGLISH_LOCALE_ID);
    }
}

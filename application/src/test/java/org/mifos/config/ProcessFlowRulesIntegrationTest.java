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

package org.mifos.config;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.testng.annotations.Test;

/**
 * Validate configuration override logic for optional process flow states.
 */
@Test(groups={"integration", "configTestSuite"})
public class ProcessFlowRulesIntegrationTest extends MifosIntegrationTestCase {
    public ProcessFlowRulesIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
    public void tearDown() {
        AccountPersistence ap = new AccountPersistence();
        AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(AccountStateEntity.class,
                AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
        ase.setIsOptional(false);
        StaticHibernateUtil.commitTransaction();
    }

    public void testOverrideNeeded() throws Exception {
        assertTrue(ProcessFlowRules.needsOverride(false, true));
    }

    public void testOverrideNotNecessary() throws Exception {
        assertFalse(ProcessFlowRules.needsOverride(false, false));
        assertFalse(ProcessFlowRules.needsOverride(true, true));
    }

    public void testOverrideValidation() throws Exception {
        assertTrue(ProcessFlowRules.isValidOverride(true, true));
        assertTrue(ProcessFlowRules.isValidOverride(false, true));
        assertTrue(ProcessFlowRules.isValidOverride(false, false));
        assertFalse(ProcessFlowRules.isValidOverride(true, false));
    }

    public void testInvalidOverride() throws Exception {
        try {
            ProcessFlowRules.needsOverride(true, false);
            fail("Expected ConfigurationException");
        } catch (ConfigurationException e){
            // expected
        }
    }

    public void testValidOverrideAgainstDb() throws Exception {
        CustomerPersistence cp = new CustomerPersistence();
        CustomerStatusEntity cse = (CustomerStatusEntity) cp.loadPersistentObject(CustomerStatusEntity.class,
                CustomerStatus.CLIENT_PENDING.getValue());
        assertTrue(cse.getIsOptional());
        cse.setIsOptional(false);
        StaticHibernateUtil.commitTransaction();
        assertFalse(cse.getIsOptional());
        assertTrue(ProcessFlowRules.isClientPendingApprovalStateEnabled());
        ProcessFlowRules.init();
        assertTrue(cse.getIsOptional());
    }

    public void testInvalidOverrideAgainstDb() throws Exception {
        try {
            AccountPersistence ap = new AccountPersistence();
            AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(AccountStateEntity.class,
                    AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
            ase.setIsOptional(true);
            StaticHibernateUtil.commitTransaction();
            assertTrue(ase.getIsOptional());
            assertFalse(ProcessFlowRules.isLoanDisbursedToLoanOfficerStateEnabled());
            ProcessFlowRules.init();
            fail("Expected ConfigurationException");
        } catch (ConfigurationException e){
            // expected
        }
    }
}

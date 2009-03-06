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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.Test;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestCaseInitializer;

/**
 * Validate configuration override logic for optional process flow states.
 */
public class TestProcessFlowRules {
    public TestProcessFlowRules() throws SystemException, ApplicationException {
        new TestCaseInitializer().initialize();
    }

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestProcessFlowRules.class);
	}

	@Test
	public void testOverrideNeeded() throws Exception {
		assertTrue(ProcessFlowRules.needsOverride(false, true));
	}

	@Test
	public void testOverrideNotNecessary() throws Exception {
		assertFalse(ProcessFlowRules.needsOverride(false, false));
		assertFalse(ProcessFlowRules.needsOverride(true, true));
	}

	@Test
	public void testOverrideValidation() throws Exception {
		assertTrue(ProcessFlowRules.isValidOverride(true, true));
		assertTrue(ProcessFlowRules.isValidOverride(false, true));
		assertTrue(ProcessFlowRules.isValidOverride(false, false));
		assertFalse(ProcessFlowRules.isValidOverride(true, false));
	}

	@Test(expected = ConfigurationException.class)
	public void testInvalidOverride() throws Exception {
		ProcessFlowRules.needsOverride(true, false);
	}

	@AfterClass
	public static void tearDownAfterClass() {
		AccountPersistence ap = new AccountPersistence();
		AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(
				AccountStateEntity.class,
				AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
		ase.setIsOptional(false);
		HibernateUtil.commitTransaction();
	}

	@Test
	public void testValidOverrideAgainstDb() throws Exception {
		CustomerPersistence cp = new CustomerPersistence();
		CustomerStatusEntity cse = (CustomerStatusEntity) cp
				.loadPersistentObject(CustomerStatusEntity.class,
						CustomerStatus.CLIENT_PENDING.getValue());
		assertTrue(cse.getIsOptional());
		cse.setIsOptional(false);
		HibernateUtil.commitTransaction();
		assertFalse(cse.getIsOptional());
		assertTrue(ProcessFlowRules.isClientPendingApprovalStateEnabled());
		ProcessFlowRules.init();
		assertTrue(cse.getIsOptional());
	}

	@Test(expected = ConfigurationException.class)
	public void testInvalidOverrideAgainstDb() throws Exception {
		AccountPersistence ap = new AccountPersistence();
		AccountStateEntity ase = (AccountStateEntity) ap.loadPersistentObject(
				AccountStateEntity.class,
				AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
		ase.setIsOptional(true);
		HibernateUtil.commitTransaction();
		assertTrue(ase.getIsOptional());
		assertFalse(ProcessFlowRules.isLoanDisbursedToLoanOfficerStateEnabled());
		ProcessFlowRules.init();
	}
}

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
 
package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class TestFinancialRules {
	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(TestFinancialRules.class);
	}

	@BeforeClass
	public static void init() throws Exception {
		// initialize Spring, Hibernate, etc.
		new TestCaseInitializer().initialize();
	}

	@Test
	public void testSupportedActions() throws Exception {
		assertEquals("11201", FinancialRules.getInstance()
				.getGLAccountForAction(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.DEBIT));
		assertEquals("13100", FinancialRules.getInstance()
				.getGLAccountForAction(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.CREDIT));
	}

	/**
	 * Ensure a lookup for a category of a nonexistant financial action fails.
	 */
	@Test(expected = RuntimeException.class)
	public void testUnsupportedAction01() throws Exception {
		FinancialRules.getInstance().getGLAccountForAction((short) -1,
				FinancialConstants.DEBIT);
	}

	/**
	 * Reversal adjustments aren't supported. If support is to be added, a mapping
	 * must also be added to the bean config file used by {@link FinancialRules}.
	 */
	public void testUnsupportedAction02() throws Exception {
		assertNull(FinancialRules.getInstance().getGLAccountForAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.CREDIT));
	}

	/**
	 * Reversal adjustments aren't supported. If support is to be added, a mapping
	 * must also be added to the bean config file used by {@link FinancialRules}
	 */
	public void testUnsupportedAction03() throws Exception {
		assertNull(FinancialRules.getInstance().getGLAccountForAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.DEBIT));
	}
	
	// TODO: test override functionality of FinancialRules Spring bean config
}

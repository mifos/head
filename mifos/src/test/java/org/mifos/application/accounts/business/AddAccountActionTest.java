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
 
package org.mifos.application.accounts.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.Assert;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Test;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;


public class AddAccountActionTest {

	private static final short SEND_TO_ORPHANS = 43;

	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		
		Upgrade upgrade = new AddAccountAction(
			72,
			SEND_TO_ORPHANS,
			TEST_LOCALE,
			"Send money to orphans");

		upgradeAndCheck(database, upgrade);
	}

	private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) 
	throws Exception {
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) session.get(
				AccountActionEntity.class, SEND_TO_ORPHANS);
		action.setLocaleId(TEST_LOCALE);

		assertEquals(SEND_TO_ORPHANS, (short) action.getId());
		assertEquals(" ", action.getLookUpValue().getLookUpName());
	}
	

	@Test 
	public void validateLookupValueKeyTest() throws Exception {
		String validKey = "AccountAction-LoanRepayment";
		String format = "AccountAction-";
		assertTrue(AddAccountAction.validateLookupValueKey(format, validKey));
		String invalidKey = "Action-LoanRepayment";
		assertFalse(AddAccountAction.validateLookupValueKey(format, invalidKey));
	}
	
	@Test 
	public void constructorTest() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		short newId = 31000;
		AddAccountAction upgrade = null;
		try
		{
			// use deprecated construtor
			upgrade = new AddAccountAction(
					DatabaseVersionPersistence.APPLICATION_VERSION + 1,
					newId,
					TEST_LOCALE,
					"NewAccountAction");
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountAction.wrongConstructor);
		}
		String invalidKey ="NewAccountAction";
		
		try
		{
			// use invalid lookup key format
			upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId ,invalidKey);	
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountAction.wrongLookupValueKeyFormat);
		}
		String goodKey = "AccountAction-NewAccountAction";
		//	use valid construtor and valid key
		upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey);	
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) session.get(
				AccountActionEntity.class, newId);
		assertEquals(goodKey, action.getLookUpValue().getLookUpName());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddAccountActionTest.class);
	}


}

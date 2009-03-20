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
 
package org.mifos.application.accounts.util.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.application.master.MessageLookup;

public class TestAccountState extends MifosIntegrationTest {
	public TestAccountState() throws SystemException, ApplicationException {
        super();
    }

    private AccountPersistence accountPersistence;

	@Override
	protected void setUp() throws Exception {
		accountPersistence = new AccountPersistence();
	}

	/*
	 * Check that the values in the enumerated type AccountState
	 * match with the Entity values in terms of ids, state names,
	 * and the text that they eventually resolve to.
	 */
	public void testRetrieveAllAccountStateList() throws NumberFormatException,
			PersistenceException {
		List<AccountStateEntity> accountStateEntityList = accountPersistence
				.retrieveAllAccountStateList(ProductType.SAVINGS.getValue());
		assertNotNull(accountStateEntityList);
		assertEquals(6, accountStateEntityList.size());

		List<AccountStateEntity> accountStateEntityList2 = accountPersistence
				.retrieveAllAccountStateList(ProductType.LOAN.getValue());
		assertNotNull(accountStateEntityList2);
		assertEquals(12, accountStateEntityList2.size());

		accountStateEntityList.addAll(accountStateEntityList2);
		
		// order by id
		Collections.sort(accountStateEntityList, new Comparator<AccountStateEntity>() {
			public int compare(AccountStateEntity state1, AccountStateEntity state2) {
				return state1.getId().compareTo(state2.getId());
			}
		});
		
		assertEquals(AccountState.values().length, accountStateEntityList.size());
		
		Iterator<AccountStateEntity> stateListIterator = accountStateEntityList.iterator();
		for (AccountState state : AccountState.values()) {
			AccountStateEntity stateInDatabase = stateListIterator.next();
			assertEquals(state.getValue(), stateInDatabase.getId());
			assertEquals(state.getPropertiesKey(), stateInDatabase.getLookUpValue().getLookUpName());
			assertEquals(MessageLookup.getInstance().lookup(state), MessageLookup.getInstance().lookup(stateInDatabase));
		}
		
	}


}

package org.mifos.application.accounts.util.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.application.master.MessageLookup;

public class TestAccountState extends MifosTestCase {
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

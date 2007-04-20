package org.mifos.application.accounts.business;

import java.util.List;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountStateMachine extends MifosTestCase {

	private AccountBusinessService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = new AccountBusinessService();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetStatusList() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOANACCOUNT, null);
		List<AccountStateEntity> stateList = service.getStatusList(
				new AccountStateEntity(
						AccountState.LOANACC_ACTIVEINGOODSTANDING),
				AccountTypes.LOANACCOUNT, Short.valueOf("1"));
		assertEquals(2, stateList.size());
	}

	public void testGetStatusName() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOANACCOUNT, null);
		assertNotNull(service.getStatusName((short) 1,
				AccountState.LOANACC_RESCHEDULED, AccountTypes.LOANACCOUNT));
	}

	public void testGetFlagName() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOANACCOUNT, null);
		assertNotNull(service.getFlagName((short) 1,
				AccountStateFlag.LOAN_WITHDRAW, AccountTypes.LOANACCOUNT));
	}

	public void testStatesInitializationException() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
					AccountTypes.LOANACCOUNT, null);
			fail();
		} catch (StatesInitializationException sie) {
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testServiceUnavailableException() throws Exception {
		try {
			service = (AccountBusinessService) ServiceFactory.getInstance()
					.getBusinessService(null);
			fail();
		} catch (ServiceUnavailableException sue) {
		}
	}

	public void testFlagForLoanCancelState() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOANACCOUNT, null);
		HibernateUtil.closeSession();
		List<AccountStateEntity> stateList = service
				.getStatusList(new AccountStateEntity(
						AccountState.LOANACC_PARTIALAPPLICATION),
						AccountTypes.LOANACCOUNT, Short.valueOf("1"));
		for (AccountStateEntity accountState : stateList) {
			if (accountState.getId().equals(
					AccountState.LOANACC_CANCEL.getValue())) {
				assertEquals(3, accountState.getFlagSet().size());
				for (AccountStateFlagEntity accountStateFlag : accountState
						.getFlagSet()) {
					if (accountStateFlag.getId().equals(
							AccountStateFlag.LOAN_REVERSAL.getValue()))
						fail();
				}
			}
		}
	}
}

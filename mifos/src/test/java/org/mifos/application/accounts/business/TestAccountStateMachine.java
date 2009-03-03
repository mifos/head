package org.mifos.application.accounts.business;

import java.util.List;

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountStateMachine extends MifosIntegrationTest {

	public TestAccountStateMachine() throws SystemException, ApplicationException {
        super();
    }

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
				AccountTypes.LOAN_ACCOUNT, null);
		List<AccountStateEntity> stateList = service.getStatusList(
				new AccountStateEntity(
						AccountState.LOAN_ACTIVE_IN_GOOD_STANDING),
				AccountTypes.LOAN_ACCOUNT, Short.valueOf("1"));
		assertEquals(2, stateList.size());
	}

	public void testGetStatusName() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOAN_ACCOUNT, null);
		assertNotNull(service.getStatusName((short) 1,
				AccountState.LOAN_CLOSED_RESCHEDULED, AccountTypes.LOAN_ACCOUNT));
	}

	public void testGetFlagName() throws Exception {
		AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
				AccountTypes.LOAN_ACCOUNT, null);
		assertNotNull(service.getFlagName((short) 1,
				AccountStateFlag.LOAN_WITHDRAW, AccountTypes.LOAN_ACCOUNT));
	}

	public void testStatesInitializationException() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			AccountStateMachines.getInstance().initialize((short) 1, (short) 1,
					AccountTypes.LOAN_ACCOUNT, null);
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
				AccountTypes.LOAN_ACCOUNT, null);
		HibernateUtil.closeSession();
		List<AccountStateEntity> stateList = service
				.getStatusList(new AccountStateEntity(
						AccountState.LOAN_PARTIAL_APPLICATION),
						AccountTypes.LOAN_ACCOUNT, Short.valueOf("1"));
		for (AccountStateEntity accountState : stateList) {
			if (accountState.getId().equals(
					AccountState.LOAN_CANCELLED.getValue())) {
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

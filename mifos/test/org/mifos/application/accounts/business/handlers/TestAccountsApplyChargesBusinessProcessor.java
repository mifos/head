package org.mifos.application.accounts.business.handlers;

import org.mifos.application.accounts.business.handler.AccountsApplyChargesBusinessProcessor;
import org.mifos.application.accounts.util.valueobjects.AccountsApplyCharges;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.framework.util.valueobjects.Context;

import org.mifos.framework.MifosTestCase;

public class TestAccountsApplyChargesBusinessProcessor extends MifosTestCase {

	public void testLoadFeeMasterData() throws Exception{
		AccountsApplyChargesBusinessProcessor accountsApplyChargesBusinessProcessor=new AccountsApplyChargesBusinessProcessor();
		AccountsApplyCharges accountsApplyCharges=new AccountsApplyCharges();
		accountsApplyCharges.setAccountId(Integer.valueOf("25"));
		accountsApplyCharges.setInput("5");
		accountsApplyCharges.setChargeAmount(Double.valueOf("20.3"));
		accountsApplyCharges.setChargeType(Short.valueOf("1"));
		Context context=new Context();
		context.setValueObject(accountsApplyCharges);
		context.setUserContext(TestObjectFactory.getUserContext());
		try {
			accountsApplyChargesBusinessProcessor.create(context);
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
}

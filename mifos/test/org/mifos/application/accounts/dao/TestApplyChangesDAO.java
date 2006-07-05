package org.mifos.application.accounts.dao;

import org.mifos.application.accounts.dao.AccountsApplyChargesDAO;
import org.mifos.application.accounts.util.valueobjects.AccountsApplyCharges;
import org.mifos.application.customer.dao.ApplyChargesDAO;
import org.mifos.application.customer.util.valueobjects.ApplyCharges;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

import org.mifos.framework.MifosTestCase;

public class TestApplyChangesDAO extends MifosTestCase {
	
	
	public void testLoadFeeMasterData(){
		AccountsApplyChargesDAO accountsApplyChargesDAO=new AccountsApplyChargesDAO();
		AccountsApplyCharges accountsApplyCharges=new AccountsApplyCharges();
		accountsApplyCharges.setAccountId(Integer.valueOf("14"));
		accountsApplyCharges.setInput("5");
		Context context=new Context();
		context.setValueObject(accountsApplyCharges);
		try {
			accountsApplyChargesDAO.loadFeeMasterData(context);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

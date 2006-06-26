package org.mifos.application.accounts.business;

import java.util.Date;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;

import junit.framework.TestCase;

public class TestAccountFeesEntity extends TestAccount {
	
	public void testChangeFeesStatus(){
		Set<AccountFeesEntity> accountFeesEntitySet=accountBO.getAccountFees();
		for(AccountFeesEntity accountFeesEntity: accountFeesEntitySet){
			accountFeesEntity.changeFeesStatus(AccountConstants.INACTIVE_FEES,new Date(System.currentTimeMillis()));
			assertEquals(accountFeesEntity.getFeeStatus(),AccountConstants.INACTIVE_FEES);
		}
	}

}

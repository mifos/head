package org.mifos.application.accounts.business;

import junit.framework.TestCase;

public class AccountBOTest extends TestCase {
	
	public void testGenerateId() throws Exception {
		AccountBO account = new AccountBO(35);
		String officeGlobalNum = "0567";
		String globalAccountNum = account.generateId(officeGlobalNum);
		assertEquals("056700000000035", globalAccountNum);
	}

}

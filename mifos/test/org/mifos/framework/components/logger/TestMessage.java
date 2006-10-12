package org.mifos.framework.components.logger;

import org.mifos.framework.MifosTestCase;


public class TestMessage extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testMessage() throws Exception {
		Message m = new Message("test Message");
		m.setLoggedUser("loggedUser");
		assertEquals("loggedUser", m.getLoggedUser());

		m.setUserOffice("userOffice");
		assertEquals("userOffice", m.getUserOffice());
	}
}

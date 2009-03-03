package org.mifos.framework.components.logger;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;


public class TestMessage extends MifosIntegrationTest {

	public TestMessage() throws SystemException, ApplicationException {
        super();
    }

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

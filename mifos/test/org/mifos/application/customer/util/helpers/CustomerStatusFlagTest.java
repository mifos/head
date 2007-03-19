package org.mifos.application.customer.util.helpers;

import junit.framework.TestCase;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.*;

public class CustomerStatusFlagTest extends TestCase {
	
	public void testIsBlacklisted() throws Exception {
		assertTrue(CLIENT_CANCEL_BLACKLISTED.isBlacklisted());
		assertTrue(CLIENT_CLOSED_BLACKLISTED.isBlacklisted());
		assertTrue(GROUP_CANCEL_BLACKLISTED.isBlacklisted());
		assertTrue(GROUP_CLOSED_BLACKLISTED.isBlacklisted());
		assertFalse(CLIENT_CANCEL_WITHDRAW.isBlacklisted());
		assertFalse(CLIENT_CLOSED_OTHER.isBlacklisted());
		assertFalse(GROUP_CLOSED_LEFTPROGRAM.isBlacklisted());
	}

}

package org.mifos.application.customer.util.helpers;

import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CANCEL_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CANCEL_WITHDRAW;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CLOSED_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.CLIENT_CLOSED_OTHER;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CANCEL_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CLOSED_BLACKLISTED;
import static org.mifos.application.customer.util.helpers.CustomerStatusFlag.GROUP_CLOSED_LEFTPROGRAM;
import junit.framework.TestCase;

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

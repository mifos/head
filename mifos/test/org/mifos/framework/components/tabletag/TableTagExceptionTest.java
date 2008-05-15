package org.mifos.framework.components.tabletag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TableTagExceptionTest extends MifosTestCase {

	public void testTableTagException() throws Exception {
		try {
			UserContext userContext = TestObjectFactory.getContext();
			Text.getImage(this, "name", userContext.getPreferredLocale());
			fail();
		} catch (TableTagException tte) {
			assertEquals("exception.framework.TableTagException", tte.getKey());
		}
	}
}

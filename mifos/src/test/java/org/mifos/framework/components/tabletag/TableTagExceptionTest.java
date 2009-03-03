package org.mifos.framework.components.tabletag;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TableTagExceptionTest extends MifosIntegrationTest {

	public TableTagExceptionTest() throws SystemException, ApplicationException {
        super();
    }

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

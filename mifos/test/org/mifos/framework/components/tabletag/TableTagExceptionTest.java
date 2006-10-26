package org.mifos.framework.components.tabletag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.TableTagException;

public class TableTagExceptionTest extends MifosTestCase {

	public void testTableTagException() throws Exception {
		try {
			Text.getImage(this, "name");
			fail();
		} catch (TableTagException tte) {
			assertEquals("exception.framework.TableTagException", tte.getKey());
		}
	}
}

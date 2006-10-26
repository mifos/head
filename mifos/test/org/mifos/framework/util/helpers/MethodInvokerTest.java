package org.mifos.framework.util.helpers;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.SystemException;


public class MethodInvokerTest extends MifosTestCase {

	public void testInvoke() throws Exception {
		try {
			MethodInvoker.invoke(this, "testSomethingElse", null, null, null);
			fail();
		} catch (SystemException se) {
			assertEquals("exception.framework.SystemException.MethodInvocationException", se.getKey());
		}
	}

}

package org.mifos.framework.util.helpers;

import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.SystemException;


public class MethodInvokerTest extends MifosTestCase {

	public void testInvokeFailure() throws Exception {
		try {
			MethodInvoker.invoke(this, "testSomethingElse", null, null, null);
			fail();
		} catch (SystemException se) {
			assertEquals("exception.framework.SystemException.MethodInvocationException", se.getKey());
		}
	}
	
	public void testInvoke() throws Exception {
		Money interest=(Money)MethodInvoker.invoke(new EMIInstallment(), "getInterest", new Object[]{});
		assertEquals(Double.valueOf("0.0"),interest.getAmountDoubleValue());	
		
	}
	
	public void testInvokeWithNoExceptionFailure() throws Exception {
		Object object = MethodInvoker.invokeWithNoException(this, "testSomethingElse", null, null, null);
		assertNull(object);
	}
	
	public void testInvokeWithNoException() throws Exception {
		Money interest=(Money)MethodInvoker.invoke(new EMIInstallment(), "getInterest", new Object[]{});
		assertEquals(Double.valueOf("0.0"),interest.getAmountDoubleValue());	
		
	}

}

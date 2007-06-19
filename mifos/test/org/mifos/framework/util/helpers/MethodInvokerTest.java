package org.mifos.framework.util.helpers;

import static org.mifos.framework.TestUtils.EURO;
import junit.framework.TestCase;

import org.mifos.application.accounts.loan.util.helpers.EMIInstallment;
import org.mifos.framework.exceptions.SystemException;


public class MethodInvokerTest extends TestCase {
	
	EMIInstallment installment;

	@Override
	protected void setUp() throws Exception {
		DatabaseSetup.configureLogging();
		installment = new EMIInstallment(
			new Money(EURO, "0"), new Money(EURO, "0"));
	}

	public void testInvokeFailure() throws Exception {
		try {
			MethodInvoker.invoke(this, "testSomethingElse", null, null, null);
			fail();
		} catch (SystemException se) {
			assertEquals("exception.framework.SystemException.MethodInvocationException", se.getKey());
		}
	}
	
	public void testInvoke() throws Exception {
		Money interest= (Money) MethodInvoker.invoke(
			installment, "getInterest", new Object[]{});
		assertEquals(Double.valueOf("0.0"),interest.getAmountDoubleValue());	
	}
	
	public void testInvokeWithNoExceptionFailure() throws Exception {
		Object object = MethodInvoker.invokeWithNoException(this, "testSomethingElse", null, null, null);
		assertNull(object);
	}
	
	public void testInvokeWithNoException() throws Exception {
		Money interest= (Money) MethodInvoker.invokeWithNoException(
			installment, "getInterest", new Object[]{});
		assertEquals(Double.valueOf("0.0"),interest.getAmountDoubleValue());	
	}

}

package org.mifos.application.productdefinition.business;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class PrdOfferingBOTest extends MifosIntegrationTest {
	public PrdOfferingBOTest() throws SystemException, ApplicationException {
        super();
    }

    public void testReturnTrueForEqualsIfPrdOfferingIdIsSame() {
		LoanOfferingBO loanOfferingBO = LoanOfferingBO
				.createInstanceForTest(Short.valueOf("1234"));
		Assert.assertTrue(loanOfferingBO.equals(LoanOfferingBO
				.createInstanceForTest(Short.valueOf("1234"))));
		Assert.assertFalse(loanOfferingBO.equals(LoanOfferingBO
				.createInstanceForTest(Short.valueOf("4321"))));
		Assert.assertFalse(loanOfferingBO.equals(SavingsOfferingBO
				.createInstanceForTest(Short.valueOf("1234"))));
	}

	public void testReturnTrueForIsOfSameOfferingIfPrdOfferingIdIsSame() {
		LoanOfferingBO loanOfferingBO = LoanOfferingBO
				.createInstanceForTest(Short.valueOf("1234"));
		Assert.assertTrue(loanOfferingBO.isOfSameOffering(LoanOfferingBO
				.createInstanceForTest(Short.valueOf("1234"))));
		Assert.assertFalse(loanOfferingBO.isOfSameOffering(LoanOfferingBO
				.createInstanceForTest(Short.valueOf("4321"))));
	}
}

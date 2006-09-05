package org.mifos.application.productdefinition.persistence;

import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PrdOfferingPersistenceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testretrieveLatenessForPrd() throws Exception {
		Short latenessDays = null;
		latenessDays = new LoansPrdPersistence().retrieveLatenessForPrd();
		assertNotNull(latenessDays);
		assertEquals(Short.valueOf("10"), latenessDays);
	}

	public void testGetMaxPrdOfferingWithouProduct()
			throws NumberFormatException, PersistenceException {
		assertNull(new PrdOfferingPersistence().getMaxPrdOffering());
	}

	public void testGetMaxPrdOfferingWithProduct()
			throws NumberFormatException, PersistenceException {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering();
		assertNotNull(new PrdOfferingPersistence().getMaxPrdOffering());
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetPrdStatus() throws NumberFormatException,
			PersistenceException {
		PrdStatusEntity prdStatus = new PrdOfferingPersistence()
				.getPrdStatus(PrdStatus.SAVINGSACTIVE);
		assertNotNull(prdStatus);
		assertEquals(ProductType.SAVINGS.getValue(), prdStatus.getPrdType()
				.getProductTypeID());
		assertEquals(Short.valueOf("1"), prdStatus.getPrdStateId()
				.getPrdStateId());
	}

}

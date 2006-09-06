package org.mifos.application.productdefinition.business.service;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;

public class SavingsPrdBusinessServiceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetActiveSavingsProductCategories() throws ServiceException {
		assertEquals(1, new SavingsPrdBusinessService()
				.getActiveSavingsProductCategories().size());
	}

	public void testGetSavingsApplicableRecurrenceTypes() throws Exception {
		assertEquals(2, new SavingsPrdBusinessService()
				.getSavingsApplicableRecurrenceTypes().size());
	}
}

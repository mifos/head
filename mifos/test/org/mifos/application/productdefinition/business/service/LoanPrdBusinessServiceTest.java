package org.mifos.application.productdefinition.business.service;

import java.util.List;

import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdBusinessServiceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetActiveLoanProductCategoriesForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			assertEquals(1, new LoanPrdBusinessService()
					.getActiveLoanProductCategories().size());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetActiveLoanProductCategories() throws ServiceException {
		assertEquals(1, new LoanPrdBusinessService()
				.getActiveLoanProductCategories().size());
	}

	public void testGetLoanApplicableCustomerTypesForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService()
					.getLoanApplicableCustomerTypes((short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetLoanApplicableCustomerTypes() throws ServiceException {
		assertEquals(2, new LoanPrdBusinessService()
				.getLoanApplicableCustomerTypes((short) 1).size());
	}

	public void testGetSourcesOfFundForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getSourcesOfFund();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetSourcesOfFund() throws ServiceException {
		List<Fund> funds = new LoanPrdBusinessService().getSourcesOfFund();
		assertNotNull(funds);
	}
}

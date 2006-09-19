package org.mifos.application.productdefinition.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdBusinessServiceTest extends MifosTestCase {

	private LoanOfferingBO loanOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetBusinessObject() throws ServiceException {
		assertNull(new LoanPrdBusinessService().getBusinessObject(null));
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

	public void testGetLoanOffering() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		loanOffering = new LoanPrdBusinessService()
				.getLoanOffering(loanOffering.getPrdOfferingId());
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());
	}

	public void testGetLoanOfferingForInvalidConnection()
			throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		HibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getLoanOffering(loanOffering
					.getPrdOfferingId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), frequency);
	}
}

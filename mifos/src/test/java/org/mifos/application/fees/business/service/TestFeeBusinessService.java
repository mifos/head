package org.mifos.application.fees.business.service;

import java.util.List;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestFeeBusinessService extends MifosIntegrationTest {

	public TestFeeBusinessService() throws SystemException, ApplicationException {
        super();
    }

    private FeeBO fee1;
	private FeeBO fee2;

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(fee2);
		TestObjectFactory.cleanUp(fee1);
		StaticHibernateUtil.closeSession();
	}

	public void testGetFee() {
		fee1 = TestObjectFactory.createOneTimeAmountFee(
				"Customer_OneTime_AmountFee", FeeCategory.CENTER, "100",
				FeePayment.UPFRONT);
		StaticHibernateUtil.commitTransaction();

		FeeBO dbFee = new FeeBusinessService().getFee(fee1.getFeeId());
		assertNotNull(dbFee);
		assertEquals(dbFee.getFeeName(), fee1.getFeeName());
		fee1 = dbFee;
	}

	public void testRetrieveFeesForCustomer() throws Exception {
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1",
				FeeCategory.CENTER, "200", RecurrenceType.MONTHLY, Short
						.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1",
				FeeCategory.LOAN, "400", RecurrenceType.MONTHLY, Short
						.valueOf("2"));
		StaticHibernateUtil.commitTransaction();

		List<FeeBO> feeList = new FeeBusinessService().retrieveCustomerFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("CustomerFee1", feeList.get(0).getFeeName());
	}

	public void testRetrieveFeesForCustomerFailure() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FeeBusinessService().retrieveCustomerFees();
			fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testRetrieveFeesForProduct() throws Exception {
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1",
				FeeCategory.CENTER, "200", RecurrenceType.MONTHLY, Short.valueOf("2"));
		fee2 = TestObjectFactory.createPeriodicAmountFee("ProductFee1",
				FeeCategory.LOAN, "400", RecurrenceType.MONTHLY, Short.valueOf("2"));
		StaticHibernateUtil.commitTransaction();
		
		List<FeeBO> feeList = new FeeBusinessService().retrieveProductFees();
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("ProductFee1", feeList.get(0).getFeeName());
	}

	public void testRetrieveFeesForProductFailure() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FeeBusinessService().retrieveProductFees();
			fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testRetrieveCustomerFeesByCategaroyType() throws Exception {
		fee1 = TestObjectFactory.createPeriodicAmountFee("CustomerFee1",
				FeeCategory.CENTER, "200", RecurrenceType.MONTHLY, Short.valueOf("2"));
		StaticHibernateUtil.commitTransaction();
		
		List<FeeBO> feeList = new FeeBusinessService()
				.retrieveCustomerFeesByCategaroyType(FeeCategory.CENTER);
		assertNotNull(feeList);
		assertEquals(1, feeList.size());
		assertEquals("CustomerFee1", feeList.get(0).getFeeName());
	}

	public void testRetrieveCustomerFeesByCategaroyTypeFailure()
			throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FeeBusinessService()
					.retrieveCustomerFeesByCategaroyType(FeeCategory.ALLCUSTOMERS);
			fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}
	}
}

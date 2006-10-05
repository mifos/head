package org.mifos.application.productdefinition.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsPrdBusinessServiceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
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

	public void testGetAllSavingsProducts() throws Exception {
		SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
		assertEquals(1, new SavingsPrdBusinessService().getAllSavingsProducts()
				.size());
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testGetApplicablePrdStatus() throws ServiceException {
		List<PrdStatusEntity> prdStatusList = new SavingsPrdBusinessService()
				.getApplicablePrdStatus((short) 1);
		HibernateUtil.closeSession();
		assertEquals(2, prdStatusList.size());
		for (PrdStatusEntity prdStatus : prdStatusList) {
			if (prdStatus.getPrdState().equals("1"))
				assertEquals("Active", prdStatus.getPrdState().getName());
			if (prdStatus.getPrdState().equals("2"))
				assertEquals("InActive", prdStatus.getPrdState().getName());
		}
	}

	public void testGetApplicablePrdStatusForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new SavingsPrdBusinessService().getApplicablePrdStatus((short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetAllSavingsProductsFailure() throws Exception {
		SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
		TestObjectFactory.simulateInvalidConnection();
		try {
			new SavingsPrdBusinessService().getAllSavingsProducts();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
			HibernateUtil.closeSession();
			TestObjectFactory.removeObject(savingsOffering);
		}
	}

	public void testGetSavingsApplicableRecurrenceTypesFailure()
			throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new SavingsPrdBusinessService()
					.getSavingsApplicableRecurrenceTypes();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetActiveSavingsProductCategoriesFailure() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new SavingsPrdBusinessService().getActiveSavingsProductCategories();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}
	
	public void testRetrieveDormancyDays() throws Exception{
		Short dormancyDays = new SavingsPrdBusinessService().retrieveDormancyDays();
		assertEquals(dormancyDays, Short.valueOf("30"));
	}

	public void testRetrieveDormancyDaysForInvalidConnection() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			new SavingsPrdBusinessService().getActiveSavingsProductCategories();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	private SavingsOfferingBO createSavingsOfferingBO() {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering("Savings Product",
				"SAVP", (short) 1, new Date(System.currentTimeMillis()),
				(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0, (short) 2,
				(short) 1, meetingIntCalc, meetingIntPost);
	}
}

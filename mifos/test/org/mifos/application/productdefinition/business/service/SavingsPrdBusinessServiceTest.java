package org.mifos.application.productdefinition.business.service;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.TestObjectFactory;

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
	public void testGetAllSavingsProducts() throws Exception {
		SavingsOfferingBO savingsOffering = createSavingsOfferingBO();
		assertEquals(1, new SavingsPrdBusinessService()
				.getAllSavingsProducts().size());
		TestObjectFactory.removeObject(savingsOffering);
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

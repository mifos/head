/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.master.business.service;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MasterBusinessServiceIntegrationTest extends MifosIntegrationTest {

	public MasterBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    MasterDataService masterService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		masterService = new MasterDataService();
		HierarchyManager.getInstance().init();
	}

	@Override
	public void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetListOfActiveLoanOfficers() throws Exception {
		List loanOfficers = masterService.getListOfActiveLoanOfficers(
				PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), Short
						.valueOf("3"), PersonnelConstants.LOAN_OFFICER);
		assertEquals(1, loanOfficers.size());
	}

	public void testGetListOfActiveLoanOfficersForInvalidConnection()
			throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getListOfActiveLoanOfficers(
					PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), Short
							.valueOf("3"), PersonnelConstants.LOAN_OFFICER);
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}

	public void testGetActiveBranches() throws Exception {
		List branches = masterService.getActiveBranches(Short.valueOf("1"));
		assertEquals(1, branches.size());
	}

	public void testGetActiveBranchesForInvalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getActiveBranches(Short.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}

	public void testGetListOfActiveParentsUnderLoanOfficer() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		CustomerBO center = TestObjectFactory.createCenter("Center_Active",
				meeting);
		List<CustomerView> customers = masterService
				.getListOfActiveParentsUnderLoanOfficer(Short.valueOf("1"),
						CustomerLevel.CENTER.getValue(), Short.valueOf("3"));
		assertEquals(1, customers.size());
		TestObjectFactory.cleanUp(center);
	}

	public void testGetListOfActiveParentsUnderLoanOfficerForInvalidConnection()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		CustomerBO center = TestObjectFactory.createCenter("Center_Active",
				meeting);
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getListOfActiveParentsUnderLoanOfficer(Short
					.valueOf("1"), CustomerLevel.CENTER.getValue(), Short
					.valueOf("3"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
		TestObjectFactory.cleanUp(center);
	}

	public void testGetMasterData() throws Exception {
		CustomValueList paymentTypes = masterService
				.getMasterData(
						MasterConstants.ATTENDENCETYPES,
						(short)1,
						"org.mifos.application.master.business.CustomerAttendanceType",
						"attendanceId");
		List<CustomValueListElement> paymentValues = paymentTypes.getCustomValueListElements();
		assertEquals(4, paymentValues.size());

	}
	
	public void testGetSavingsProductsAsOfMeetingDate() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		Date startDate = new Date(System.currentTimeMillis());
		CustomerBO center = TestObjectFactory.createCenter("Center", meeting);
		SavingsOfferingBO savingsOffering =
			TestObjectFactory.createSavingsProduct("SavingPrd1", "S",
				startDate, meetingIntCalc, meetingIntPost);
		AccountBO account = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), startDate, savingsOffering);

		List<PrdOfferingBO> productList = masterService
				.getSavingsProductsAsOfMeetingDate(startDate, "1.1", center
						.getPersonnel().getPersonnelId());
		assertEquals(1, productList.size());
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(center);
	}

	public void testGetSavingsProductsAsOfMeetingDateForInvalidConnection()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		java.sql.Date startDate =
			new java.sql.Date(System.currentTimeMillis());
		CustomerBO center = TestObjectFactory.createCenter("Center", meeting);
		SavingsOfferingBO savingsOffering = 
			TestObjectFactory.createSavingsProduct("SavingPrd1", "S", 
					startDate, meetingIntCalc, meetingIntPost);
		AccountBO account = TestObjectFactory.createSavingsAccount("432434",
				center, Short.valueOf("16"), startDate, savingsOffering);

		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getSavingsProductsAsOfMeetingDate(startDate, "1.1",
					center.getPersonnel().getPersonnelId());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {

			StaticHibernateUtil.closeSession();
		}
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(center);
	}

	public void testRetrievePaymentTypes() throws Exception {
		List<PaymentTypeEntity> paymentTypeList = masterService
				.retrievePaymentTypes(Short.valueOf("1"));
		assertEquals(3, paymentTypeList.size());
	}

	public void testRetrievePaymentTypesForInvalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.retrievePaymentTypes(Short.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}

	/*public void testGetSupportedPaymentModes() throws Exception {
		List<PaymentTypeEntity> paymentTypeList = masterService
				.getSupportedPaymentModes(Short.valueOf("1"), Short
						.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentTypeList.size());
	}*/

	/*public void testGetSupportedPaymentModesForInvalidConnection()
			throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getSupportedPaymentModes(Short.valueOf("1"), Short
					.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}*/

	public void testGetMasterEntityName() throws NumberFormatException,
			PersistenceException, ServiceException {
		assertEquals("Partial Application", masterService
				.retrieveMasterEntities(1, Short.valueOf("1")));
	}

	public void testGetMasterEntityNameForInvalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.retrieveMasterEntities(1, Short.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}
	
	public void testgetLoanProductsAsOfMeetingDate(){
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getLoanProductsAsOfMeetingDate(null,"1.1", Short.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}
	public void testretrieveCustomFieldsDefinition(){
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.retrieveCustomFieldsDefinition(EntityType.CENTER);
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}

	public void testGetMasterDataEntity(){
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterService.getMasterDataEntity(
				AccountStateEntity.class,
				AccountState.LOAN_PARTIAL_APPLICATION.getValue());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}	
}

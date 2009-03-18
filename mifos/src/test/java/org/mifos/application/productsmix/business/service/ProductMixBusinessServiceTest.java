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

package org.mifos.application.productsmix.business.service;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.persistence.ProductMixPersistence;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;


import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class ProductMixBusinessServiceTest  extends MifosIntegrationTest {
	
	public ProductMixBusinessServiceTest() throws SystemException, ApplicationException {
        super();
    }

    private SavingsOfferingBO savingsOffering;
	private SavingsOfferingBO secondSavingsOffering;
	private LoanOfferingBO loanOffering;
	private LoanOfferingBO loanOffering2;
	private CustomerBO center;	
	private CustomerBO center2;	
	private ProductMixBusinessService service;
	MeetingBO meetingIntPost;
	MeetingBO meetingIntCalc;
	MeetingBO meetingIntPost2;
	MeetingBO meetingIntCalc2;
	ProductMixBO prdmix;
	ProductMixBO prdmix2;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createSavingProduct();
		service = (ProductMixBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.PrdMix);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(prdmix);	
		TestObjectFactory.removeObject(prdmix2);	
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.removeObject(loanOffering2);
		TestObjectFactory.removeObject(savingsOffering);
		TestObjectFactory.removeObject(secondSavingsOffering);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(center2);
		
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetBusinessObject() throws ServiceException {
		assertNull(service.getBusinessObject(null));
	}
	
	public void testGetAllPrdOfferingsByType_Success() throws ServiceException {
		assertEquals(1, service.getAllPrdOfferingsByType(
				ProductType.SAVINGS.getValue().toString()).size());
		StaticHibernateUtil.closeSession();

	}
	public void testGetAllowedPrdOfferingsForMixProduct_Success()
			throws ServiceException {
		assertEquals(1, service.getAllowedPrdOfferingsForMixProduct(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());
		StaticHibernateUtil.closeSession();
	}

	public void testGetAllPrdOfferingsByType_failure() throws ServiceException {
		assertEquals(0, service.getAllPrdOfferingsByType(
				ProductType.LOAN.getValue().toString()).size());
		StaticHibernateUtil.closeSession();
	}
	
	
	public void testGetAllowedPrdOfferingsByType() throws ServiceException {
		createSecondSavingProduct();
		assertEquals(2, service.getAllowedPrdOfferingsByType(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());

		assertEquals("A_SavingPrd", service.getAllowedPrdOfferingsByType(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).get(0)
				.getPrdOfferingName());

		assertTrue("Savings products should be in alphabitical order:",
				(service.getAllowedPrdOfferingsByType(
						savingsOffering.getPrdOfferingId().toString(),
						ProductType.SAVINGS.getValue().toString()).get(0)
						.getPrdOfferingName().compareToIgnoreCase(service
						.getAllowedPrdOfferingsByType(
								savingsOffering.getPrdOfferingId().toString(),
								ProductType.SAVINGS.getValue().toString()).get(
								1).getPrdOfferingName())) < 0);
		StaticHibernateUtil.closeSession();

	}

	
	public void testGetNotAllowedPrdOfferingsForMixProduct()
			throws ServiceException {
		
		createSecondSavingProduct();
		prdmix2=createNotAllowedProductForAProductOffering(savingsOffering,savingsOffering);
		prdmix=createNotAllowedProductForAProductOffering(savingsOffering,secondSavingsOffering);
		
		assertEquals(1, service.getNotAllowedPrdOfferingsForMixProduct(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());
			
	}
	
	
	public void testGetNotAllowedPrdOfferingsByType_success() throws ServiceException {
		createSecondSavingProduct();
		prdmix2=createNotAllowedProductForAProductOffering(savingsOffering,savingsOffering);
		prdmix=createNotAllowedProductForAProductOffering(savingsOffering,secondSavingsOffering);
		
		assertEquals(2, service.getNotAllowedPrdOfferingsByType(savingsOffering.getPrdOfferingId().toString()).size());
		
		assertTrue("Savings products should be in alphabitical order:",
				(service.getNotAllowedPrdOfferingsByType(savingsOffering.getPrdOfferingId().toString()).get(0)
						.getPrdOfferingName().compareToIgnoreCase(service
						.getNotAllowedPrdOfferingsByType(savingsOffering.getPrdOfferingId().toString()).get(
								1).getPrdOfferingName())) < 0);

		StaticHibernateUtil.closeSession();
	}

	private  ProductMixBO createNotAllowedProductForAProductOffering(PrdOfferingBO prdOffering,PrdOfferingBO prdOfferingNotAllowedId)
	{
		return TestObjectFactory.createNotAllowedProductForAProductOffering(prdOffering,prdOfferingNotAllowedId);
		
	}
	
	private CenterBO createCenter() {
		return createCenter("Center_Active_test");
	}
	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createCenter(name, meeting);
	}
	private void createSavingProduct() {
	Date startDate = new Date(System.currentTimeMillis());

	meetingIntCalc = TestObjectFactory
			.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
					EVERY_WEEK, CUSTOMER_MEETING));
	meetingIntPost = TestObjectFactory
			.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
					EVERY_WEEK, CUSTOMER_MEETING));

	center = createCenter();
	savingsOffering = 
		TestObjectFactory.createSavingsProduct("SavingPrd1", "S",
			startDate,
			meetingIntCalc, meetingIntPost);

	}
	
		private void createSecondSavingProduct() {
		Date startDate = new Date(System.currentTimeMillis());
		meetingIntCalc2 = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		meetingIntPost2 = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		center2 = createCenter("Center_Active_test2");

		secondSavingsOffering = TestObjectFactory.createSavingsProduct(
				"A_SavingPrd", "AS", startDate, meetingIntCalc2,
				meetingIntPost2);

	}

	public void testGetPrdOfferingMix() throws ServiceException,
			PersistenceException {
		createLoanProductMixed();
		createsecondLoanProductMixed();
		prdmix = createNotAllowedProductForAProductOffering(loanOffering,
				loanOffering);
		assertEquals(2, service.getPrdOfferingMix().size());
		assertTrue("Products Mix should be in alphabitical order:", (service
				.getPrdOfferingMix().get(0).getPrdOfferingName()
				.compareToIgnoreCase(service.getPrdOfferingMix().get(1)
						.getPrdOfferingName())) < 0);
		StaticHibernateUtil.closeSession();

	}

	private void createLoanProductMixed() throws PersistenceException {

		Date startDate = new Date(System.currentTimeMillis());

		meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		loanOffering = TestObjectFactory.createLoanOffering("Loan", "L",
				startDate, meetingIntCalc);
		loanOffering.updatePrdOfferingFlag();

	}

	private void createsecondLoanProductMixed() throws PersistenceException {

		Date startDate = new Date(System.currentTimeMillis());

		meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));

		loanOffering2 = TestObjectFactory.createLoanOffering("aLoan", "aL",
				startDate, meetingIntCalc);
		loanOffering2.updatePrdOfferingFlag();

	}
	
	public void testCanProductsExist() throws Exception {
		ProductMixPersistence productMixPersistenceMock = createMock(ProductMixPersistence.class);
		short PRD_OFFERING_ID_ONE = (short)1;
		short PRD_OFFERING_ID_TWO = (short)2;
		LoanOfferingBO loanOfferingMock1 = createMock(LoanOfferingBO.class);
		LoanOfferingBO loanOfferingMock2 = createMock(LoanOfferingBO.class);
		
		expect(productMixPersistenceMock.doesPrdOfferingsCanCoexist(PRD_OFFERING_ID_ONE, PRD_OFFERING_ID_TWO)).andReturn(true);
		expect(loanOfferingMock1.getPrdOfferingId()).andReturn(PRD_OFFERING_ID_ONE);
		expect(loanOfferingMock2.getPrdOfferingId()).andReturn(PRD_OFFERING_ID_TWO);
		replay(loanOfferingMock1,loanOfferingMock2,productMixPersistenceMock);
		
		new ProductMixBusinessService(productMixPersistenceMock).canProductsCoExist(loanOfferingMock1, loanOfferingMock2);
		verify(loanOfferingMock1,loanOfferingMock2,productMixPersistenceMock);
	}

}
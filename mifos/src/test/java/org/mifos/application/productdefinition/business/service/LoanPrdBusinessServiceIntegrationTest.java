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
 
package org.mifos.application.productdefinition.business.service;

import java.sql.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanPrdBusinessServiceIntegrationTest extends MifosIntegrationTest {

	public LoanPrdBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private LoanOfferingBO loanOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		StaticHibernateUtil.closeSession();
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

	public void testGetLoanOffering() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		loanOffering = new LoanPrdBusinessService()
				.getLoanOffering(loanOffering.getPrdOfferingId());
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());
	}

	public void testGetLoanOfferingForInvalidConnection()
			throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getLoanOffering(loanOffering
					.getPrdOfferingId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
	}

	public void testGetApplicablePrdStatus() throws ServiceException {
		List<PrdStatusEntity> prdStatusList = new LoanPrdBusinessService()
				.getApplicablePrdStatus((short) 1);
		StaticHibernateUtil.closeSession();
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
			new LoanPrdBusinessService().getApplicablePrdStatus((short) 1);
			fail();
		} catch (ServiceException e) {
		}
	}

	public void testGetLoanOfferingWithLocaleId() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		short localeId = 1;
		loanOffering = new LoanPrdBusinessService().getLoanOffering(
				loanOffering.getPrdOfferingId(), localeId);
		assertNotNull(loanOffering);
		assertEquals("Loan Offering", loanOffering.getPrdOfferingName());
		assertEquals("Loan", loanOffering.getPrdOfferingShortName());

		assertEquals("Other", loanOffering.getPrdCategory()
				.getProductCategoryName());
		assertEquals(ApplicableTo.GROUPS, 
				loanOffering.getPrdApplicableMasterEnum());
		assertEquals("Active", loanOffering.getPrdStatus().getPrdState()
				.getName());
		assertEquals("Grace on all repayments", loanOffering
				.getGracePeriodType().getName());
		assertEquals("Flat", loanOffering.getInterestTypes().getName());
	}

	public void testGetLoanOfferingWithLocaleIdForInvalidConnection() {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getLoanOffering(loanOffering
					.getPrdOfferingId(), (short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
	}

	public void testGetAllLoanOfferings() throws ServiceException {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		LoanOfferingBO loanOffering1 = createLoanOfferingBO("Loan Offering1",
				"Loa1");
		StaticHibernateUtil.closeSession();

		List<LoanOfferingBO> loanOfferings = new LoanPrdBusinessService()
				.getAllLoanOfferings((short) 1);
		assertNotNull(loanOfferings);
		assertEquals(2, loanOfferings.size());
		for (LoanOfferingBO loanOfferingBO : loanOfferings) {
			assertNotNull(loanOfferingBO.getPrdOfferingName());
			assertNotNull(loanOfferingBO.getPrdOfferingId());
			assertNotNull(loanOfferingBO.getPrdStatus().getPrdState().getName());
		}
		StaticHibernateUtil.closeSession();
		TestObjectFactory.removeObject(loanOffering1);
	}

	public void testGetAllLoanOfferingsForInvalidConnection() {
		loanOffering = createLoanOfferingBO("Loan Offering", "Loan");
		StaticHibernateUtil.closeSession();

		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().getAllLoanOfferings((short) 1);
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
	}

	public void testRetrieveLatenessForPrd() throws Exception{
		try {
			Short latenessDays = new LoanPrdBusinessService().retrieveLatenessForPrd();
			assertEquals(latenessDays,Short.valueOf("10"));
		} catch (ServiceException e) {
			assertTrue(false);
		}
		StaticHibernateUtil.closeSession();
		
	}
	
	public void testRetrieveLatenessForPrdForInvalidConnection() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try {
			new LoanPrdBusinessService().retrieveLatenessForPrd();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
		
	}
	
	private LoanOfferingBO createLoanOfferingBO(String prdOfferingName,
			String shortName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		return TestObjectFactory.createLoanOffering(prdOfferingName, shortName,
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, 
				InterestType.FLAT, frequency);
	}
}

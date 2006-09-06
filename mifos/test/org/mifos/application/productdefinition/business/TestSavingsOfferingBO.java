/**

 * TestSavingsOfferingBO.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.productdefinition.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsOfferingBO extends MifosTestCase {

	private SavingsOfferingBO savingsOffering;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.removeObject(savingsOffering);
	}

	public void testBuildSavingsOfferingWithoutData() {
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutName()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), null, "S",
					productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutSavingsType()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings offering",
					"S", productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), null, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutGLCode()
			throws InvalidUserException, SystemException, ApplicationException {
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings offering",
					"S", productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					null, null);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithShortNameGreaterThanFourDig()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Offering",
					"Savings", productCategory, prdApplicableMaster, new Date(
							System.currentTimeMillis()), savingsType,
					intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateLessThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Offering",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateEqualToCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
				TestObjectFactory.getUserContext(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(savingsOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGSACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testBuildSavingsOfferingWithStartDateGreaterThanCurrentDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(2);
		SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
				TestObjectFactory.getUserContext(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(savingsOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGSINACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());

	}

	public void testBuildSavingsOfferingWithEndDateLessThanStartDate()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Offering",
					"Savi", productCategory, prdApplicableMaster, startDate,
					endDate, "dssf", null, savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"),
					new Money(), new Money(), 10.0, depglCodeEntity,
					intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithDuplicatePrdOfferingName()
			throws InvalidUserException, SystemException, ApplicationException {
		savingsOffering = createSavingsOfferingBO("Savings Product", "SAVP");

		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithDuplicatePrdOfferingShortName()
			throws InvalidUserException, SystemException, ApplicationException {
		savingsOffering = createSavingsOfferingBO("Savings Product", "SAVP");

		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithNoRecommendedAmountForMandatoryOffering()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					null, 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithNoRecommendedAmountUnitForGroupOffering()
			throws InvalidUserException, SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.GROUPS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10.0"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testCreateSavingsOffering() throws SystemException,
			ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = new SavingsOfferingBO(
				TestObjectFactory.getUserContext(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, endDate,
				"dssf", null, savingsType, intCalType, intCalcMeeting,
				intPostMeeting, new Money("10"), new Money(), new Money(),
				10.0, depglCodeEntity, intglCodeEntity);
		savingsOffering.save();
		HibernateUtil.commitTransaction();

		savingsOffering = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());
		assertEquals("Savings Offering", savingsOffering.getPrdOfferingName());
		assertEquals("Savi", savingsOffering.getPrdOfferingShortName());
		assertNotNull(savingsOffering.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGSACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());
		assertEquals(PrdApplicableMaster.CLIENTS.getValue(), savingsOffering
				.getPrdApplicableMaster().getId());
		assertEquals(SavingsType.MANDATORY.getValue(), savingsOffering
				.getSavingsType().getId());
		assertEquals(InterestCalcType.AVERAGE_BALANCE.getValue(),
				savingsOffering.getInterestCalcType().getId());
	}

	private SavingsOfferingBO createSavingsOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(prdOfferingName,
				shortName, (short) 1, new Date(System.currentTimeMillis()),
				(short) 2, 300.0, (short) 1, 1.2, 200.0, 200.0, (short) 2,
				(short) 1, meetingIntCalc, meetingIntPost);
	}

	private MeetingBO getMeeting() {
		return TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

}

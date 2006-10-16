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
import java.util.List;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsOfferingBO extends MifosTestCase {

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.removeObject(savingsOffering);
		TestObjectFactory.removeObject(savingsOffering1);
	}

	public static void setStatus(SavingsOfferingBO savingsOffering,
			PrdStatusEntity prdStatus) {
		savingsOffering.setPrdStatus(prdStatus);
	}

	public static void setRecommendedAmntUnit(
			SavingsOfferingBO savingsOffering,
			RecommendedAmountUnit recommendedAmountUnit) {
		savingsOffering.setRecommendedAmntUnit(new RecommendedAmntUnitEntity(
				recommendedAmountUnit));
	}

	public void testUpdateSavingsOfferingForLogging() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		String shortName = "S";
		String newShortName = "S1";
		String desc = "Desc";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = createSavingsOfferingBO(name, shortName,
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.MINIMUM_BALANCE);
		savingsOffering.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(savingsOffering);
		savingsOffering.update(Short.valueOf("1"), newName, newShortName,
				productCategory, prdApplicableMaster, startDate, endDate,
				"Desc", PrdStatus.SAVINGSINACTIVE, null, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				new Money("100"), new Money("1"), 10.0);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savingsOffering = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.SAVINGSPRODUCT.getValue(), new Integer(
						savingsOffering.getPrdOfferingId().toString()));
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.SAVINGSPRODUCT.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(12, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Balance used for Interest rate calculation")) {
				assertEquals("Minimum Balance", auditLogRecord.getOldValue());
				assertEquals("Average Balance", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
				assertEquals("Active", auditLogRecord.getOldValue());
				assertEquals("Inactive", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Type Of Deposits")) {
				assertEquals("Voluntary", auditLogRecord.getOldValue());
				assertEquals("Mandatory", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Service Charge Rate")) {
				assertEquals("1.2", auditLogRecord.getOldValue());
				assertEquals("10.0", auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();

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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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
		Date startDate = offSetCurrentDate(0);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde
					.getKey());
		}
	}

	public void testBuildSavingsOfferingWithDuplicatePrdOfferingShortName()
			throws  SystemException, ApplicationException {
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
		Date startDate = offSetCurrentDate(0);
		try {
			SavingsOfferingBO savingsOffering = new SavingsOfferingBO(
					TestObjectFactory.getUserContext(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME, pde
					.getKey());
		}
	}

	public void testBuildSavingsOfferingWithNoRecommendedAmountForMandatoryOffering()
			throws  SystemException, ApplicationException {
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
			throws  SystemException, ApplicationException {
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

	public void testCreateSavingsOfferingForInvalidConnection()
			throws Exception {
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
		TestObjectFactory.simulateInvalidConnection();
		try {
			savingsOffering = new SavingsOfferingBO(TestObjectFactory
					.getUserContext(), "Savings Offering", "Savi",
					productCategory, prdApplicableMaster, startDate, endDate,
					"dssf", null, savingsType, intCalType, intCalcMeeting,
					intPostMeeting, new Money("10"), new Money(), new Money(),
					10.0, depglCodeEntity, intglCodeEntity);
			savingsOffering.save();
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
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
		savingsOffering = new SavingsOfferingBO(TestObjectFactory
				.getUserContext(), "Savings Offering", "Savi", productCategory,
				prdApplicableMaster, startDate, endDate, "dssf", null,
				savingsType, intCalType, intCalcMeeting, intPostMeeting,
				new Money("10"), new Money(), new Money(), 10.0,
				depglCodeEntity, intglCodeEntity);
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

	public void testUpdateSavingsOfferingWithoutName() throws Exception {
		savingsOffering = createSavingsOfferingBO("Savings_offering", "S");
		try {
			savingsOffering.update(Short.valueOf("1"), null, null, null, null,
					null, null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithoutShortName() throws Exception {
		savingsOffering = createSavingsOfferingBO("Savings_offering", "S");
		try {
			savingsOffering.update(Short.valueOf("1"), "Savings_Changed", null,
					null, null, null, null, null, null);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithDuplicateName() throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = createSavingsOfferingBO("Savings_offering", "S");
		savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
		try {
			savingsOffering.update(Short.valueOf("1"), "Savings_offering1",
					"S", productCategory, prdApplicableMaster, startDate,
					endDate, "Desc", PrdStatus.SAVINGSACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde
					.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithDuplicateShortName()
			throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = createSavingsOfferingBO("Savings_offering", "S");
		savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
		try {
			savingsOffering.update(Short.valueOf("1"),
					"Savings_offeringChanged", "S1", productCategory,
					prdApplicableMaster, startDate, endDate, "Desc",
					PrdStatus.SAVINGSACTIVE, null, savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), new Money(
							"100"), new Money("1"), 10.0);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME, pde
					.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithStartDateNotChanged()
			throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = createSavingsOfferingBO(name, "S",
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSINACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		savingsOffering.setStartDate(reduceCurrentDate(1));
		savingsOffering.update(Short.valueOf("1"), newName, "S1",
				productCategory, prdApplicableMaster, savingsOffering
						.getStartDate(), endDate, "Desc",
				PrdStatus.SAVINGSINACTIVE, null, savingsType, intCalType,
				intCalcMeeting, intPostMeeting, new Money("10"), new Money(
						"100"), new Money("1"), 10.0);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savingsOffering = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());
		assertEquals(reduceCurrentDate(1), savingsOffering.getStartDate());
	}

	public void testUpdateSavingsOfferingFailureWithStartDateInFuture()
			throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(10);
		savingsOffering = createSavingsOfferingBO(name, "S",
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSINACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		savingsOffering.setStartDate(reduceCurrentDate(1));
		Date newStartDate = offSetCurrentDate(1);
		try {
			savingsOffering.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, newStartDate,
					endDate, "Desc", PrdStatus.SAVINGSINACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION,
					pde.getKey());
		}

	}

	public void testUpdateFailureWithInitialStartDateInFuture()
			throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(5);
		Date endDate = offSetCurrentDate(15);
		savingsOffering = createSavingsOfferingBO(name, "S",
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSINACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		Date newStartDate = reduceCurrentDate(1);
		try {
			savingsOffering.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, newStartDate,
					endDate, "Desc", PrdStatus.SAVINGSINACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.INVALIDSTARTDATE, pde
					.getKey());
		}

	}

	public void testUpdateFailureWithEndDateLessThanStartDate()
			throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(5);
		Date endDate = offSetCurrentDate(3);
		savingsOffering = createSavingsOfferingBO(name, "S",
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSINACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		try {
			savingsOffering.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Desc", PrdStatus.SAVINGSACTIVE, null, savingsType,
					intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			assertTrue(false);
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.INVALIDENDDATE, pde
					.getKey());
		}

	}

	public void testUpdateWithInitialStartDateInFuture() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(5);
		Date endDate = offSetCurrentDate(15);
		savingsOffering = createSavingsOfferingBO(name, "S",
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSINACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		Date newStartDate = offSetCurrentDate(6);
		savingsOffering.update(Short.valueOf("1"), newName, "S1",
				productCategory, prdApplicableMaster, newStartDate, endDate,
				"Desc", PrdStatus.SAVINGSACTIVE, null, savingsType, intCalType,
				intCalcMeeting, intPostMeeting, new Money("10"), new Money(
						"100"), new Money("1"), 10.0);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savingsOffering = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());
		assertEquals(newStartDate, savingsOffering.getStartDate());
	}

	public void testUpdateSavingsOffering() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		String shortName = "S";
		String newShortName = "S1";
		String desc = "Desc";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsOffering = createSavingsOfferingBO(name, shortName,
				PrdApplicableMaster.CLIENTS, startDate,
				PrdStatus.SAVINGSACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.MINIMUM_BALANCE);
		savingsOffering.update(Short.valueOf("1"), newName, newShortName,
				productCategory, prdApplicableMaster, startDate, endDate,
				"Desc", PrdStatus.SAVINGSINACTIVE, null, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				new Money("100"), new Money("1"), 10.0);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savingsOffering = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsOffering.getPrdOfferingId());
		assertEquals(newName, savingsOffering.getPrdOfferingName());
		assertEquals(newShortName, savingsOffering.getPrdOfferingShortName());
		assertEquals(desc, savingsOffering.getDescription());
		assertEquals(startDate, savingsOffering.getStartDate());
		assertEquals(endDate, savingsOffering.getEndDate());
		assertEquals(productCategory.getProductCategoryID().intValue(),
				savingsOffering.getPrdCategory().getProductCategoryID()
						.intValue());
		assertEquals(PrdStatus.SAVINGSINACTIVE.getValue(), savingsOffering
				.getPrdStatus().getOfferingStatusId());
		assertEquals(ProductType.SAVINGS.getValue(), savingsOffering
				.getPrdStatus().getPrdType().getProductTypeID());
		assertEquals(savingsType.getId(), savingsOffering.getSavingsType()
				.getId());
		assertEquals(intCalType.getId(), savingsOffering.getInterestCalcType()
				.getId());
		assertEquals(intCalcMeeting.getMeetingDetails().getRecurAfter(),
				savingsOffering.getTimePerForInstcalc().getMeeting()
						.getMeetingDetails().getRecurAfter());
		assertEquals(intCalcMeeting.getMeetingDetails().getRecurrenceType()
				.getRecurrenceId().shortValue(), savingsOffering
				.getTimePerForInstcalc().getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId().shortValue());
		assertEquals(intPostMeeting.getMeetingDetails().getRecurAfter(),
				savingsOffering.getFreqOfPostIntcalc().getMeeting()
						.getMeetingDetails().getRecurAfter());
		assertEquals("Recommended Amount", new Money("10"), savingsOffering
				.getRecommendedAmount());
		assertEquals(10.0, savingsOffering.getInterestRate());
		assertEquals("Max Amount Withdrawl Amount", new Money("100"),
				savingsOffering.getMaxAmntWithdrawl());
		assertEquals("Min Amount Withdrawl Amount", new Money("1"),
				savingsOffering.getMinAmntForInt());
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

	private java.sql.Date reduceCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}

	private SavingsOfferingBO createSavingsOfferingBO(String prdOfferingName,
			String shortName, PrdApplicableMaster applicableTo, Date startDate,
			PrdStatus offeringStatus, SavingsType savingType,
			InterestCalcType interestCalcType) {

		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(prdOfferingName,
				shortName, applicableTo.getValue(), startDate, offeringStatus
						.getValue(), 300.0, (short) 1, 1.2, 200.0, 200.0,
				savingType.getValue(), interestCalcType.getValue(),
				meetingIntCalc, meetingIntPost);
	}

}

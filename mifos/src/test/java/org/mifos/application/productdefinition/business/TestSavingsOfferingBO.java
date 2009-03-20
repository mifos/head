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
 
package org.mifos.application.productdefinition.business;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsOfferingBO extends MifosIntegrationTest {

	public TestSavingsOfferingBO() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    private SavingsOfferingBO savingsProduct;

	private SavingsOfferingBO savingsOffering1;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.removeObject(savingsProduct);
		TestObjectFactory.removeObject(savingsOffering1);
	}

	public void testUpdateSavingsOfferingForLogging() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		String shortName = "S";
		String newShortName = "S1";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) 
			TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsProduct = createSavingsOfferingBO(name, shortName,
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_ACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.MINIMUM_BALANCE);
		savingsProduct.setUserContext(TestUtils.makeUserWithLocales());
		StaticHibernateUtil.getInterceptor().createInitialValueMap(savingsProduct);
		savingsProduct.update(Short.valueOf("1"), newName, newShortName,
				productCategory, prdApplicableMaster, startDate, endDate,
				"Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				new Money("100"), new Money("1"), 10.0);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsProduct.getPrdOfferingId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.SAVINGSPRODUCT, new Integer(
						savingsProduct.getPrdOfferingId().toString()));
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.SAVINGSPRODUCT, 
			auditLogList.get(0).getEntityTypeAsEnum());
		Set<AuditLogRecord> records = auditLogList.get(0).getAuditLogRecords();
		assertEquals(12, records.size());
		for (AuditLogRecord auditLogRecord : records) {
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
			else {
				// What are the others?
			}
		}
		TestObjectFactory.cleanUpChangeLog();

	}

	public void testBuildSavingsOfferingWithoutData() {
		try {
			new SavingsOfferingBO(null,
					null, null, null, null, null, null, null, null, null, null,
					null, null, null);
			fail();
		} catch (ProductDefinitionException e) {
		}
	}

	public void testBuildSavingsOfferingWithoutName()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), null, "S",
					productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutSavingsType()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings offering",
					"S", productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), null, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithoutGLCode()
			throws  SystemException, ApplicationException {
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings offering",
					"S", productCategory, prdApplicableMaster, new Date(System
							.currentTimeMillis()), savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), 10.0,
					null, null);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithShortNameGreaterThanFourDig()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Offering",
					"Savings", productCategory, prdApplicableMaster, new Date(
							System.currentTimeMillis()), savingsType,
					intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateLessThanCurrentDate()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Offering",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithStartDateEqualToCurrentDate()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		SavingsOfferingBO product = new SavingsOfferingBO(
				TestUtils.makeUser(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(product.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGS_ACTIVE, product.getStatus());

	}

	public void testBuildSavingsOfferingWithStartDateGreaterThanCurrentDate()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(2);
		SavingsOfferingBO product = new SavingsOfferingBO(
				TestUtils.makeUser(), "Savings Offering", "Savi",
				productCategory, prdApplicableMaster, startDate, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				10.0, depglCodeEntity, intglCodeEntity);
		assertNotNull(product.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGS_INACTIVE, product.getStatus());

	}

	public void testBuildSavingsOfferingWithEndDateLessThanStartDate()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(-2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Offering",
					"Savi", productCategory, prdApplicableMaster, startDate,
					endDate, "dssf", null, savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"),
					new Money(), new Money(), 10.0, depglCodeEntity,
					intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithDuplicatePrdOfferingName()
			throws  SystemException, ApplicationException {
		savingsProduct = createSavingsOfferingBO("Savings Product", "SAVP");

		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Product",
					"Savi", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde
					.getKey());
		}
	}

	public void testBuildSavingsOfferingWithDuplicatePrdOfferingShortName()
			throws  SystemException, ApplicationException {
		savingsProduct = createSavingsOfferingBO("Savings Product", "SAVP");

		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME, pde
					.getKey());
		}
	}

	public void testBuildSavingsOfferingWithNoRecommendedAmountForMandatoryOffering()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					null, 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testBuildSavingsOfferingWithNoRecommendedAmountUnitForGroupOffering()
			throws  SystemException, ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.GROUPS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(-2);
		try {
			new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Product1",
					"SAVP", productCategory, prdApplicableMaster, startDate,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10.0"), 10.0, depglCodeEntity, intglCodeEntity);
			fail();
		} catch (ProductDefinitionException e) {
			assertTrue(true);
		}
	}

	public void testCreateSavingsOfferingForInvalidConnection()
			throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		TestObjectFactory.simulateInvalidConnection();
		try {
			savingsProduct = new SavingsOfferingBO(
					TestUtils.makeUser(), "Savings Offering", "Savi",
					productCategory, prdApplicableMaster, startDate, endDate,
					"dssf", null, savingsType, intCalType, intCalcMeeting,
					intPostMeeting, new Money("10"), new Money(), new Money(),
					10.0, depglCodeEntity, intglCodeEntity);
			savingsProduct.save();
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}

	public void testCreateSavingsOffering() throws SystemException,
			ApplicationException {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
		SavingsTypeEntity savingsType = new SavingsTypeEntity(
				SavingsType.MANDATORY);

		InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(
				InterestCalcType.AVERAGE_BALANCE);
		MeetingBO intCalcMeeting = getMeeting();
		MeetingBO intPostMeeting = getMeeting();
		GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory
				.getObject(ProductCategoryBO.class, (short) 2);
		Date startDate = offSetCurrentDate(0);
		Date endDate = offSetCurrentDate(7);
		savingsProduct = new SavingsOfferingBO(TestUtils.makeUser(),
				"Savings Offering", "Savi", productCategory,
				prdApplicableMaster, startDate, endDate, "dssf", null,
				savingsType, intCalType, intCalcMeeting, intPostMeeting,
				new Money("10"), new Money(), new Money(), 10.0,
				depglCodeEntity, intglCodeEntity);
		savingsProduct.save();
		StaticHibernateUtil.commitTransaction();

		savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsProduct.getPrdOfferingId());
		assertEquals("Savings Offering", savingsProduct.getPrdOfferingName());
		assertEquals("Savi", savingsProduct.getPrdOfferingShortName());
		assertNotNull(savingsProduct.getGlobalPrdOfferingNum());
		assertEquals(PrdStatus.SAVINGS_ACTIVE, savingsProduct.getStatus());
		assertEquals(ApplicableTo.CLIENTS,
				savingsProduct.getPrdApplicableMasterEnum());
		assertEquals(SavingsType.MANDATORY, savingsProduct
				.getSavingsTypeAsEnum());
		assertEquals(InterestCalcType.AVERAGE_BALANCE.getValue(),
				savingsProduct.getInterestCalcType().getId());
	}

	public void testUpdateSavingsOfferingWithoutName() throws Exception {
		savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
		try {
			savingsProduct.update(Short.valueOf("1"), null, null, null, null,
					null, null, null, null);
			fail();
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithoutShortName() throws Exception {
		savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
		try {
			savingsProduct.update(Short.valueOf("1"), "Savings_Changed", null,
					null, null, null, null, null, null);
			fail();
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithDuplicateName() throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
		savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
		try {
			savingsProduct.update(Short.valueOf("1"), "Savings_offering1",
					"S", productCategory, prdApplicableMaster, startDate,
					endDate, "Desc", PrdStatus.SAVINGS_ACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			fail();
		} catch (ProductDefinitionException pde) {
			assertTrue(true);
			assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde
					.getKey());
		}
	}

	public void testUpdateSavingsOfferingWithDuplicateShortName()
			throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
		savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
		try {
			savingsProduct.update(Short.valueOf("1"),
					"Savings_offeringChanged", "S1", productCategory,
					prdApplicableMaster, startDate, endDate, "Desc",
					PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType,
					intCalcMeeting, intPostMeeting, new Money("10"), new Money(
							"100"), new Money("1"), 10.0);
			fail();
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
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, "S",
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		savingsProduct.setStartDate(reduceCurrentDate(1));
		savingsProduct.update(Short.valueOf("1"), newName, "S1",
				productCategory, prdApplicableMaster, savingsProduct
						.getStartDate(), endDate, "Desc",
				PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType,
				intCalcMeeting, intPostMeeting, new Money("10"), new Money(
						"100"), new Money("1"), 10.0);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsProduct.getPrdOfferingId());
		assertEquals(reduceCurrentDate(1), savingsProduct.getStartDate());
	}

	public void testUpdateSavingsOfferingFailureWithStartDateInFuture()
			throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, "S",
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		savingsProduct.setStartDate(reduceCurrentDate(1));
		Date newStartDate = offSetCurrentDate(1);
		try {
			savingsProduct.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, newStartDate,
					endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			fail();
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
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, "S",
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		Date newStartDate = reduceCurrentDate(1);
		try {
			savingsProduct.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, newStartDate,
					endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null,
					savingsType, intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			fail();
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
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, "S",
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		try {
			savingsProduct.update(Short.valueOf("1"), newName, "S1",
					productCategory, prdApplicableMaster, startDate, endDate,
					"Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType,
					intCalType, intCalcMeeting, intPostMeeting,
					new Money("10"), new Money("100"), new Money("1"), 10.0);
			fail();
		} catch (ProductDefinitionException pde) {
			assertEquals(ProductDefinitionConstants.INVALIDENDDATE, pde
					.getKey());
		}

	}

	public void testUpdateWithInitialStartDateInFuture() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, "S",
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.AVERAGE_BALANCE);
		Date newStartDate = offSetCurrentDate(6);
		savingsProduct.update(Short.valueOf("1"), newName, "S1",
				productCategory, prdApplicableMaster, newStartDate, endDate,
				"Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType,
				intCalcMeeting, intPostMeeting, new Money("10"), new Money(
						"100"), new Money("1"), 10.0);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsProduct.getPrdOfferingId());
		assertEquals(newStartDate, savingsProduct.getStartDate());
	}

	public void testUpdateSavingsOffering() throws Exception {
		String name = "Savings_offering";
		String newName = "Savings_offeringChanged";
		String shortName = "S";
		String newShortName = "S1";
		String desc = "Desc";
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.CLIENTS);
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
		savingsProduct = createSavingsOfferingBO(name, shortName,
				ApplicableTo.CLIENTS, startDate,
				PrdStatus.SAVINGS_ACTIVE, SavingsType.VOLUNTARY,
				InterestCalcType.MINIMUM_BALANCE);
		savingsProduct.update(Short.valueOf("1"), newName, newShortName,
				productCategory, prdApplicableMaster, startDate, endDate,
				"Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType,
				intCalType, intCalcMeeting, intPostMeeting, new Money("10"),
				new Money("100"), new Money("1"), 10.0);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(
				SavingsOfferingBO.class, savingsProduct.getPrdOfferingId());
		assertEquals(newName, savingsProduct.getPrdOfferingName());
		assertEquals(newShortName, savingsProduct.getPrdOfferingShortName());
		assertEquals(desc, savingsProduct.getDescription());
		assertEquals(startDate, savingsProduct.getStartDate());
		assertEquals(endDate, savingsProduct.getEndDate());
		assertEquals(productCategory.getProductCategoryID().intValue(),
				savingsProduct.getPrdCategory().getProductCategoryID()
						.intValue());
		assertEquals(PrdStatus.SAVINGS_INACTIVE, savingsProduct.getStatus());
		assertEquals(ProductType.SAVINGS, savingsProduct
				.getPrdStatus().getPrdType().getType());
		assertEquals(savingsType.getId(), savingsProduct.getSavingsType()
				.getId());
		assertEquals(intCalType.getId(), savingsProduct.getInterestCalcType()
				.getId());
		assertEquals(intCalcMeeting.getMeetingDetails().getRecurAfter(),
				savingsProduct.getTimePerForInstcalc().getMeeting()
						.getMeetingDetails().getRecurAfter());
		assertEquals(intCalcMeeting.getMeetingDetails().getRecurrenceType()
				.getRecurrenceId().shortValue(), savingsProduct
				.getTimePerForInstcalc().getMeeting().getMeetingDetails()
				.getRecurrenceType().getRecurrenceId().shortValue());
		assertEquals(intPostMeeting.getMeetingDetails().getRecurAfter(),
				savingsProduct.getFreqOfPostIntcalc().getMeeting()
						.getMeetingDetails().getRecurAfter());
		assertEquals("Recommended Amount", new Money("10"), savingsProduct
				.getRecommendedAmount());
		assertEquals(10.0, savingsProduct.getInterestRate(), DELTA);
		assertEquals("Max Amount Withdrawl Amount", new Money("100"),
				savingsProduct.getMaxAmntWithdrawl());
		assertEquals("Min Amount Withdrawl Amount", new Money("1"),
				savingsProduct.getMinAmntForInt());
	}

	private SavingsOfferingBO createSavingsOfferingBO(String prdOfferingName,
			String shortName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsProduct(
				prdOfferingName, shortName, ApplicableTo.CLIENTS, 
				new Date(System.currentTimeMillis()), 
				PrdStatus.SAVINGS_ACTIVE, 300.0, 
				RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 
				200.0, 200.0, 
				SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE,
				meetingIntCalc, meetingIntPost);
	}

	private MeetingBO getMeeting() {
		return TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
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
			String shortName, ApplicableTo applicableTo, Date startDate,
			PrdStatus offeringStatus, SavingsType savingType,
			InterestCalcType interestCalcType) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		return TestObjectFactory.createSavingsProduct(
				prdOfferingName, shortName, applicableTo, startDate, 
				offeringStatus, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
				1.2, 
				200.0, 200.0, savingType, interestCalcType, 
				meetingIntCalc, meetingIntPost);
	}

}

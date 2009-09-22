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

import junit.framework.Assert;

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
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsOfferingBOIntegrationTest extends MifosIntegrationTestCase {

    public SavingsOfferingBOIntegrationTest() throws SystemException, ApplicationException {
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
        TestObjectFactory.removeObject(savingsProduct);
        TestObjectFactory.removeObject(savingsOffering1);
        super.tearDown();
    }

    public void testUpdateSavingsOfferingForLogging() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        String shortName = "S";
        String newShortName = "S1";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = createSavingsOfferingBO(name, shortName, ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_ACTIVE, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE);
        savingsProduct.setUserContext(TestUtils.makeUserWithLocales());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(savingsProduct);
        savingsProduct.update(Short.valueOf("1"), newName, newShortName, productCategory, prdApplicableMaster,
                startDate, endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsProduct
                .getPrdOfferingId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.SAVINGSPRODUCT, new Integer(
                savingsProduct.getPrdOfferingId().toString()));
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.SAVINGSPRODUCT, auditLogList.get(0).getEntityTypeAsEnum());
        Set<AuditLogRecord> records = auditLogList.get(0).getAuditLogRecords();
       Assert.assertEquals(12, records.size());
        for (AuditLogRecord auditLogRecord : records) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Balance used for Interest rate calculation")) {
               Assert.assertEquals("Minimum Balance", auditLogRecord.getOldValue());
               Assert.assertEquals("Average Balance", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
               Assert.assertEquals("Active", auditLogRecord.getOldValue());
               Assert.assertEquals("Inactive", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Type Of Deposits")) {
               Assert.assertEquals("Voluntary", auditLogRecord.getOldValue());
               Assert.assertEquals("Mandatory", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge Rate")) {
               Assert.assertEquals("1.2", auditLogRecord.getOldValue());
               Assert.assertEquals("10.0", auditLogRecord.getNewValue());
            } else {
                // What are the others?
            }
        }
        TestObjectFactory.cleanUpChangeLog();

    }

    public void testBuildSavingsOfferingWithoutData() {
        try {
            new SavingsOfferingBO(null, "fake", null, null, null, null, null, null, null, null, null, Double
                    .valueOf("0.0"), null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
        }
    }

    public void testBuildSavingsOfferingWithoutName() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), null, "S", productCategory, prdApplicableMaster, new Date(
                    System.currentTimeMillis()), savingsType, intCalType, intCalcMeeting, intPostMeeting, new Money(
                    "10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithoutSavingsType() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings offering", "S", productCategory, prdApplicableMaster,
                    new Date(System.currentTimeMillis()), null, intCalType, intCalcMeeting, intPostMeeting, new Money(
                            "10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithoutGLCode() throws SystemException {
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings offering", "S", productCategory, prdApplicableMaster,
                    new Date(System.currentTimeMillis()), savingsType, intCalType, intCalcMeeting, intPostMeeting,
                    new Money("10"), 10.0, null, null);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithShortNameGreaterThanFourDig() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savings", productCategory,
                    prdApplicableMaster, new Date(System.currentTimeMillis()), savingsType, intCalType, intCalcMeeting,
                    intPostMeeting, new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithStartDateLessThanCurrentDate() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(-2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi", productCategory,
                    prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting, intPostMeeting, new Money(
                            "10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithStartDateEqualToCurrentDate() throws SystemException,
            ProductDefinitionException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        SavingsOfferingBO product = new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi",
                productCategory, prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(product.getGlobalPrdOfferingNum());
       Assert.assertEquals(PrdStatus.SAVINGS_ACTIVE, product.getStatus());

    }

    public void testBuildSavingsOfferingWithStartDateGreaterThanCurrentDate() throws SystemException,
            ApplicationException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(2);
        SavingsOfferingBO product = new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi",
                productCategory, prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), 10.0, depglCodeEntity, intglCodeEntity);
        Assert.assertNotNull(product.getGlobalPrdOfferingNum());
       Assert.assertEquals(PrdStatus.SAVINGS_INACTIVE, product.getStatus());

    }

    public void testBuildSavingsOfferingWithEndDateLessThanStartDate() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(-2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi", productCategory,
                    prdApplicableMaster, startDate, endDate, "dssf", null, savingsType, intCalType, intCalcMeeting,
                    intPostMeeting, new Money("10"), new Money(), new Money(), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithDuplicatePrdOfferingName() throws SystemException {
        savingsProduct = createSavingsOfferingBO("Savings Product", "SAVP");

        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Product", "Savi", productCategory,
                    prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting, intPostMeeting, new Money(
                            "10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde.getKey());
        }
    }

    public void testBuildSavingsOfferingWithDuplicatePrdOfferingShortName() throws SystemException {
        savingsProduct = createSavingsOfferingBO("Savings Product", "SAVP");

        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Product1", "SAVP", productCategory,
                    prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting, intPostMeeting, new Money(
                            "10"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME, pde.getKey());
        }
    }

    public void testBuildSavingsOfferingWithNoRecommendedAmountForMandatoryOffering() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(-2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Product1", "SAVP", productCategory,
                    prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting, intPostMeeting, null,
                    10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testBuildSavingsOfferingWithNoRecommendedAmountUnitForGroupOffering() throws SystemException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(-2);
        try {
            new SavingsOfferingBO(TestUtils.makeUser(), "Savings Product1", "SAVP", productCategory,
                    prdApplicableMaster, startDate, savingsType, intCalType, intCalcMeeting, intPostMeeting, new Money(
                            "10.0"), 10.0, depglCodeEntity, intglCodeEntity);
            Assert.fail();
        } catch (ProductDefinitionException e) {
           Assert.assertTrue(true);
        }
    }

    public void testCreateSavingsOfferingForInvalidConnection() throws Exception {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        TestObjectFactory.simulateInvalidConnection();
        try {
            savingsProduct = new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi", productCategory,
                    prdApplicableMaster, startDate, endDate, "dssf", null, savingsType, intCalType, intCalcMeeting,
                    intPostMeeting, new Money("10"), new Money(), new Money(), 10.0, depglCodeEntity, intglCodeEntity);
            savingsProduct.save();
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testCreateSavingsOffering() throws SystemException, ProductDefinitionException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        GLCodeEntity depglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        GLCodeEntity intglCodeEntity = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                (short) 7);
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = new SavingsOfferingBO(TestUtils.makeUser(), "Savings Offering", "Savi", productCategory,
                prdApplicableMaster, startDate, endDate, "dssf", null, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), new Money(), new Money(), 10.0, depglCodeEntity, intglCodeEntity);
        savingsProduct.save();
        StaticHibernateUtil.commitTransaction();

        savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsProduct
                .getPrdOfferingId());
       Assert.assertEquals("Savings Offering", savingsProduct.getPrdOfferingName());
       Assert.assertEquals("Savi", savingsProduct.getPrdOfferingShortName());
        Assert.assertNotNull(savingsProduct.getGlobalPrdOfferingNum());
       Assert.assertEquals(PrdStatus.SAVINGS_ACTIVE, savingsProduct.getStatus());
       Assert.assertEquals(ApplicableTo.CLIENTS, savingsProduct.getPrdApplicableMasterEnum());
       Assert.assertEquals(SavingsType.MANDATORY, savingsProduct.getSavingsTypeAsEnum());
       Assert.assertEquals(InterestCalcType.AVERAGE_BALANCE.getValue(), savingsProduct.getInterestCalcType().getId());
    }

    public void testUpdateSavingsOfferingWithoutName() throws Exception {
        savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
        try {
            savingsProduct.update(Short.valueOf("1"), null, null, null, null, null, null, null, null);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
        }
    }

    public void testUpdateSavingsOfferingWithoutShortName() throws Exception {
        savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
        try {
            savingsProduct.update(Short.valueOf("1"), "Savings_Changed", null, null, null, null, null, null, null);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.ERROR_CREATE, pde.getKey());
        }
    }

    public void testUpdateSavingsOfferingWithDuplicateName() throws Exception {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
        savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
        try {
            savingsProduct.update(Short.valueOf("1"), "Savings_offering1", "S", productCategory, prdApplicableMaster,
                    startDate, endDate, "Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType,
                    intCalcMeeting, intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.DUPLPRDINSTNAME, pde.getKey());
        }
    }

    public void testUpdateSavingsOfferingWithDuplicateShortName() throws Exception {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = createSavingsOfferingBO("Savings_offering", "S");
        savingsOffering1 = createSavingsOfferingBO("Savings_offering1", "S1");
        try {
            savingsProduct
                    .update(Short.valueOf("1"), "Savings_offeringChanged", "S1", productCategory, prdApplicableMaster,
                            startDate, endDate, "Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType,
                            intCalcMeeting, intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.DUPLPRDINSTSHORTNAME, pde.getKey());
        }
    }

    public void testUpdateSavingsOfferingWithStartDateNotChanged() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = createSavingsOfferingBO(name, "S", ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY, InterestCalcType.AVERAGE_BALANCE);
        savingsProduct.setStartDate(reduceCurrentDate(1));
        savingsProduct.update(Short.valueOf("1"), newName, "S1", productCategory, prdApplicableMaster, savingsProduct
                .getStartDate(), endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType,
                intCalcMeeting, intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsProduct
                .getPrdOfferingId());
       Assert.assertEquals(reduceCurrentDate(1), savingsProduct.getStartDate());
    }

    public void testUpdateSavingsOfferingFailureWithStartDateInFuture() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(10);
        savingsProduct = createSavingsOfferingBO(name, "S", ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY, InterestCalcType.AVERAGE_BALANCE);
        savingsProduct.setStartDate(reduceCurrentDate(1));
        Date newStartDate = offSetCurrentDate(1);
        try {
            savingsProduct.update(Short.valueOf("1"), newName, "S1", productCategory, prdApplicableMaster,
                    newStartDate, endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType,
                    intCalcMeeting, intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.STARTDATEUPDATEEXCEPTION, pde.getKey());
        }

    }

    public void testUpdateFailureWithInitialStartDateInFuture() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(5);
        Date endDate = offSetCurrentDate(15);
        savingsProduct = createSavingsOfferingBO(name, "S", ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY, InterestCalcType.AVERAGE_BALANCE);
        Date newStartDate = reduceCurrentDate(1);
        try {
            savingsProduct.update(Short.valueOf("1"), newName, "S1", productCategory, prdApplicableMaster,
                    newStartDate, endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType,
                    intCalcMeeting, intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertTrue(true);
           Assert.assertEquals(ProductDefinitionConstants.INVALIDSTARTDATE, pde.getKey());
        }

    }

    public void testUpdateFailureWithEndDateLessThanStartDate() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(5);
        Date endDate = offSetCurrentDate(3);
        savingsProduct = createSavingsOfferingBO(name, "S", ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY, InterestCalcType.AVERAGE_BALANCE);
        try {
            savingsProduct.update(Short.valueOf("1"), newName, "S1", productCategory, prdApplicableMaster, startDate,
                    endDate, "Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType, intCalcMeeting,
                    intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
            Assert.fail();
        } catch (ProductDefinitionException pde) {
           Assert.assertEquals(ProductDefinitionConstants.INVALIDENDDATE, pde.getKey());
        }

    }

    public void testUpdateWithInitialStartDateInFuture() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);
        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(5);
        Date endDate = offSetCurrentDate(15);
        savingsProduct = createSavingsOfferingBO(name, "S", ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_INACTIVE, SavingsType.VOLUNTARY, InterestCalcType.AVERAGE_BALANCE);
        Date newStartDate = offSetCurrentDate(6);
        savingsProduct.update(Short.valueOf("1"), newName, "S1", productCategory, prdApplicableMaster, newStartDate,
                endDate, "Desc", PrdStatus.SAVINGS_ACTIVE, null, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsProduct
                .getPrdOfferingId());
       Assert.assertEquals(newStartDate, savingsProduct.getStartDate());
    }

    public void testUpdateSavingsOffering() throws Exception {
        String name = "Savings_offering";
        String newName = "Savings_offeringChanged";
        String shortName = "S";
        String newShortName = "S1";
        String desc = "Desc";
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.CLIENTS);
        SavingsTypeEntity savingsType = new SavingsTypeEntity(SavingsType.MANDATORY);

        InterestCalcTypeEntity intCalType = new InterestCalcTypeEntity(InterestCalcType.AVERAGE_BALANCE);
        MeetingBO intCalcMeeting = getMeeting();
        MeetingBO intPostMeeting = getMeeting();
        ProductCategoryBO productCategory = (ProductCategoryBO) TestObjectFactory.getObject(ProductCategoryBO.class,
                (short) 2);
        Date startDate = offSetCurrentDate(0);
        Date endDate = offSetCurrentDate(7);
        savingsProduct = createSavingsOfferingBO(name, shortName, ApplicableTo.CLIENTS, startDate,
                PrdStatus.SAVINGS_ACTIVE, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE);
        savingsProduct.update(Short.valueOf("1"), newName, newShortName, productCategory, prdApplicableMaster,
                startDate, endDate, "Desc", PrdStatus.SAVINGS_INACTIVE, null, savingsType, intCalType, intCalcMeeting,
                intPostMeeting, new Money("10"), new Money("100"), new Money("1"), 10.0);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        savingsProduct = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsProduct
                .getPrdOfferingId());
       Assert.assertEquals(newName, savingsProduct.getPrdOfferingName());
       Assert.assertEquals(newShortName, savingsProduct.getPrdOfferingShortName());
       Assert.assertEquals(desc, savingsProduct.getDescription());
       Assert.assertEquals(startDate, savingsProduct.getStartDate());
       Assert.assertEquals(endDate, savingsProduct.getEndDate());
       Assert.assertEquals(productCategory.getProductCategoryID().intValue(), savingsProduct.getPrdCategory()
                .getProductCategoryID().intValue());
       Assert.assertEquals(PrdStatus.SAVINGS_INACTIVE, savingsProduct.getStatus());
       Assert.assertEquals(ProductType.SAVINGS, savingsProduct.getPrdStatus().getPrdType().getType());
       Assert.assertEquals(savingsType.getId(), savingsProduct.getSavingsType().getId());
       Assert.assertEquals(intCalType.getId(), savingsProduct.getInterestCalcType().getId());
       Assert.assertEquals(intCalcMeeting.getMeetingDetails().getRecurAfter(), savingsProduct.getTimePerForInstcalc()
                .getMeeting().getMeetingDetails().getRecurAfter());
       Assert.assertEquals(intCalcMeeting.getMeetingDetails().getRecurrenceType().getRecurrenceId().shortValue(),
                savingsProduct.getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurrenceType()
                        .getRecurrenceId().shortValue());
       Assert.assertEquals(intPostMeeting.getMeetingDetails().getRecurAfter(), savingsProduct.getFreqOfPostIntcalc()
                .getMeeting().getMeetingDetails().getRecurAfter());
       Assert.assertEquals("Recommended Amount", new Money("10"), savingsProduct.getRecommendedAmount());
       Assert.assertEquals(10.0, savingsProduct.getInterestRate(), DELTA);
       Assert.assertEquals("Max Amount Withdrawl Amount", new Money("100"), savingsProduct.getMaxAmntWithdrawl());
       Assert.assertEquals("Min Amount Withdrawl Amount", new Money("1"), savingsProduct.getMinAmntForInt());
    }

    private SavingsOfferingBO createSavingsOfferingBO(final String prdOfferingName, final String shortName) {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createSavingsProduct(prdOfferingName, shortName, ApplicableTo.CLIENTS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
    }

    private MeetingBO getMeeting() {
        return TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
    }

    private java.sql.Date offSetCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private java.sql.Date reduceCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }

    private SavingsOfferingBO createSavingsOfferingBO(final String prdOfferingName, final String shortName,
            final ApplicableTo applicableTo, final Date startDate, final PrdStatus offeringStatus, final SavingsType savingType,
            final InterestCalcType interestCalcType) {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createSavingsProduct(prdOfferingName, shortName, applicableTo, startDate,
                offeringStatus, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0, savingType,
                interestCalcType, meetingIntCalc, meetingIntPost);
    }

}

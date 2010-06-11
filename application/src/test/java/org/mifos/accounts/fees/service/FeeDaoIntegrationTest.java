/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.fees.service;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.*;
import org.mifos.accounts.fees.entities.*;
import org.mifos.accounts.fees.entities.FeeFrequencyEntity;
import org.mifos.accounts.fees.entities.FeeLevelEntity;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.persistence.MasterEntityDao;
import org.mifos.accounts.fees.util.helpers.*;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.platform.persistence.GenericDaoHibernateImpl;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/FeeContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class FeeDaoIntegrationTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private MasterEntityDao masterEntityDao;

    @Autowired
    private FeeDao feeDao;

    private UserContext userCtx;

    private GLCodeEntity glCode;

    @Before
    public void setUserContext() {
        userCtx = TestUtils.makeUser();
    }

    @Before
    public void setGLCode() {
        GenericDaoHibernateImpl<GLCodeEntity, Short> dao = new GenericDaoHibernateImpl<GLCodeEntity, Short>(GLCodeEntity.class);
        dao.setSessionFactory(sessionFactory);
        glCode = new GLCodeEntity(Short.valueOf("666"), "666");
        dao.create(glCode);
        //glCode = dao.getDetails(Short.valueOf("49"));
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldRetrieveCustomerFees() throws Exception {
        int noofCustomerFees = feeDao.retrieveCustomerFees().size();
        FeeEntity fee = createPeriodicFee(true, "shouldRetrieveCustomerFees", FeeCategory.ALLCUSTOMERS);
        List<FeeEntity> fees = feeDao.retrieveCustomerFees();
        Assert.assertTrue("num of customer fees should have been more than before",fees.size() > noofCustomerFees);
    }

    @Test
    @Transactional
    public void shouldRetrieveProductFees() throws Exception {
        int noOfProductFees = feeDao.retrieveProductFees().size();
        FeeEntity fee = createPeriodicFee(false, "shouldRetrieveProductFees", FeeCategory.LOAN);
        List<FeeEntity> fees = feeDao.retrieveProductFees();
        //System.out.println(fees.size());
        Assert.assertTrue("num of product fees should have been more than before",fees.size() > noOfProductFees);
    }


    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreateOnetimeUpfrontAmountFee() throws Exception {
        /*GenericDaoHibernateImpl<FeeEntity, Short> dao = new GenericDaoHibernateImpl<FeeEntity, Short>(FeeEntity.class);
        dao.setSessionFactory(sessionFactory);*/
        boolean isCustomerDefaultFee = false;
        FeeEntity fee =
            new AmountFeeEntity(
                    "test.shouldCreateOnetimeUpfrontAmountFee",
                    getCategoryTypeEntity(userCtx, FeeCategory.CENTER),
                    glCode,
                    new Money(TestUtils.RUPEE, "100"),
                    isCustomerDefaultFee,
                    TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = getFeeFrequencyTypeEntity(userCtx, FeeFrequencyType.ONETIME);
        FeePaymentEntity feePayment = getFeePaymentEntiry(userCtx, FeePayment.UPFRONT);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, fee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, fee, feePayment, null));
        feeDao.create(fee);
        Assert.assertTrue("created fee id is null!", fee.getFeeId() != null);
    }



    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreateOneTimeUpfrontRateFee() throws Exception {
        boolean isCustomerDefaultFee = false;
        FeeEntity rateFee =
            new RateFeeEntity("test.shouldCreateOneTimeUpfrontRateFee",
                new CategoryTypeEntity(FeeCategory.CENTER),
                glCode,
                2.0,
                getFeeFormulaEntity(userCtx, FeeFormula.AMOUNT),
                isCustomerDefaultFee,
                TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = getFeeFrequencyTypeEntity(userCtx, FeeFrequencyType.ONETIME);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, rateFee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, rateFee, new FeePaymentEntity(FeePayment.UPFRONT), null));
        feeDao.create(rateFee);
        Assert.assertTrue("created fee id is null", rateFee.getFeeId() != null);
    }



    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicAmountFee() throws Exception {
        MeetingBO feefrequencyMeeting = new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.PERIODIC_FEE);
        boolean isCustomerDefaultFee = false;
        FeeEntity fee =
            new AmountFeeEntity("test.shouldCreatePeriodicAmountFee",
                new CategoryTypeEntity(FeeCategory.CENTER),
                glCode,
                new Money(TestUtils.RUPEE, "100"),
                isCustomerDefaultFee,
                TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = getFeeFrequencyTypeEntity(userCtx, FeeFrequencyType.PERIODIC);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, fee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, fee, null, feefrequencyMeeting));
        feeDao.create(fee);
        Assert.assertTrue("created fee id is null", fee.getFeeId() != null);
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicRateFee() throws Exception {
        MeetingBO feefrequencyMeeting =
            new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.PERIODIC_FEE);
        boolean isCustomerDefaultFee = false;
        FeeEntity fee = new RateFeeEntity("test.shouldCreatePeriodicRateFee",
                new CategoryTypeEntity(FeeCategory.CENTER),
                glCode, 100.0,
                new FeeFormulaEntity(FeeFormula.AMOUNT),
                isCustomerDefaultFee,
                TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, fee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, fee, null, feefrequencyMeeting));
        feeDao.create(fee);
        Assert.assertTrue("created fee id is null", fee.getFeeId() != null);
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicCustomerDefaultFee() throws Exception {
        //with customer default fee
        FeeEntity fee = createPeriodicFee(true, "testPeriodicFee", FeeCategory.ALLCUSTOMERS);
        sessionFactory.getCurrentSession().flush();
        FeeEntity newFee = feeDao.getDetails(fee.getFeeId());
        Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), newFee.getCategoryType().getId());
        Assert.assertEquals(true, newFee.isCustomerDefaultFee());
        Assert.assertTrue("expected created fee is not a customer fee",vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));

    }

    private FeeEntity createPeriodicFee(boolean isCustomerDefaultFee, String feeName, FeeCategory category) throws Exception {
        MeetingBO feefrequencyMeeting =
            new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.PERIODIC_FEE);
        FeeEntity fee =
           new RateFeeEntity(feeName,
                new CategoryTypeEntity(category), glCode, 100.0,
                new FeeFormulaEntity(FeeFormula.AMOUNT), isCustomerDefaultFee,
                TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, fee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, fee, null, feefrequencyMeeting));
        feeDao.create(fee);
        Assert.assertTrue("created fee id is null", fee.getFeeId() != null);
        return fee;
    }


    private void setFeeAttributesBeforeCreate(
            boolean isCustomerDefaultFee,
            FeeEntity fee,
            FeeStatus feeStatus,
            FeeFrequencyEntity feeFrequency) throws PersistenceException, PropertyNotFoundException {
        fee.setFeeFrequency(feeFrequency);
        fee.setCreatedDate(new Date());
        fee.setCreatedBy(userCtx.getId());
        fee.setFeeStatus(getFeeStatusEntity(userCtx, feeStatus));
        if (isCustomerDefaultFee) {
            fee.defaultToCustomer();
        }
    }

    /*private GenericDaoHibernateImpl<FeeEntity, Short> getGenericFeeDao() {
        GenericDaoHibernateImpl<FeeEntity, Short> dao = new GenericDaoHibernateImpl<FeeEntity, Short>(FeeEntity.class);
        dao.setSessionFactory(sessionFactory);
        return dao;
    }*/


    private FeeFormulaEntity getFeeFormulaEntity(UserContext userCtx, FeeFormula formula) throws PersistenceException {
        return masterEntityDao.retrieveMasterEntity(FeeFormulaEntity.class,
                formula.getValue(), userCtx.getLocaleId());
    }

    private FeeFrequencyTypeEntity getFeeFrequencyTypeEntity(UserContext userCtx, FeeFrequencyType frequencyType) throws PersistenceException {
        FeeFrequencyTypeEntity feeFrequencyType = masterEntityDao.retrieveMasterEntity(
                FeeFrequencyTypeEntity.class, frequencyType.getValue(), userCtx.getLocaleId());
        return feeFrequencyType;
    }

    private FeeStatusEntity getFeeStatusEntity(UserContext userCtx, FeeStatus feeStatus) throws PersistenceException {
        return masterEntityDao.retrieveMasterEntity(FeeStatusEntity.class,
                feeStatus.getValue(), userCtx.getLocaleId());
    }

    private FeePaymentEntity getFeePaymentEntiry(UserContext userCtx, FeePayment payment) throws PersistenceException {
        return masterEntityDao.retrieveMasterEntity(FeePaymentEntity.class,
                payment.getValue(), userCtx.getLocaleId());
    }

    private CategoryTypeEntity getCategoryTypeEntity(UserContext userCtx, FeeCategory category) throws PersistenceException {
        CategoryTypeEntity categoryType = masterEntityDao.retrieveMasterEntity(
                CategoryTypeEntity.class, category.getValue(), userCtx.getLocaleId());
        return categoryType;
    }

    /*private GLCodeEntity getGLCode(String glCode) {
        return masterEntityDao.retrieveGLCodeEntity(Short.valueOf(glCode));
    }*/


    private boolean vaidateDefaultCustomerFee(Set<FeeLevelEntity> defaultCustomers, FeeCategory feeCategory) {
        //BIND Copy paste from FeeBOIntegrationTest!
        boolean bCenter = false;
        boolean bGroup = false;
        boolean bClient = false;

        for (FeeLevelEntity feeLevel : defaultCustomers) {
            if (feeLevel.getLevelId().equals(FeeLevel.CENTERLEVEL.getValue())) {
                bCenter = true;
            }
            if (feeLevel.getLevelId().equals(FeeLevel.GROUPLEVEL.getValue())) {
                bGroup = true;
            }
            if (feeLevel.getLevelId().equals(FeeLevel.CLIENTLEVEL.getValue())) {
                bClient = true;
            }
        }

        if (feeCategory.equals(FeeCategory.CENTER)) {
            return bCenter && !bGroup && !bClient;
        }

        if (feeCategory.equals(FeeCategory.GROUP)) {
            return !bCenter && bGroup && !bClient;
        }

        if (feeCategory.equals(FeeCategory.CLIENT)) {
            return !bCenter && !bGroup && bClient;
        }

        if (feeCategory.equals(FeeCategory.ALLCUSTOMERS)) {
            return bCenter && bGroup && bClient;
        }
        return !bCenter && !bGroup && !bClient;
    }
}

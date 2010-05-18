package org.mifos.accounts.fees.service;



import java.util.Date;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.entities.AmountFeeEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.entities.FeeFrequencyEntity;
import org.mifos.accounts.fees.entities.FeeLevelEntity;
import org.mifos.accounts.fees.entities.RateFeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.servicefacade.FeeDao;
import org.mifos.accounts.fees.servicefacade.GenericDaoHibernateImpl;
import org.mifos.accounts.fees.servicefacade.MasterEntityDao;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeeLevel;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/FeeContext.xml"})
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class FeeDaoTest {

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
    }


    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreateOnetimeUpfrontAmountFee() {
        /*GenericDaoHibernateImpl<FeeEntity, Short> dao = new GenericDaoHibernateImpl<FeeEntity, Short>(FeeEntity.class);
        dao.setSessionFactory(sessionFactory);*/
        try {
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
            getFeeDao().create(fee);
            Assert.assertTrue( fee.getFeeId() != null);
        } catch (FeeException e) {
            Assert.fail(String.format("FeeException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (PersistenceException pe) {
            Assert.fail(String.format("PersistenceException occurred. key %s. Error: %s", pe.getKey(), pe.getMessage()));
        }
    }



    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreateOneTimeUpfrontRateFee() {
        try {
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
            getFeeDao().create(rateFee);
            Assert.assertTrue( rateFee.getFeeId() != null);
        } catch (FeeException e) {
            Assert.fail(String.format("FeeException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (PersistenceException pe) {
            Assert.fail(String.format("PersistenceException occurred. key %s. Error: %s", pe.getKey(), pe.getMessage()));
        }
    }



    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicAmountFee() {
        try {
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
            getFeeDao().create(fee);
            Assert.assertTrue( fee.getFeeId() != null);
        } catch (FeeException e) {
            e.printStackTrace();
            Assert.fail(String.format("FeeException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (MeetingException me) {
            Assert.fail(String.format("MeetingException occurred. key %s. Error: %s", me.getKey(), me.getMessage()));
        } catch (PersistenceException pe) {
            Assert.fail(String.format("PersistenceException occurred. key %s. Error: %s", pe.getKey(), pe.getMessage()));
        }
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicRateFee() {
        try {
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
            getFeeDao().create(fee);
            Assert.assertTrue( fee.getFeeId() != null);
        } catch (FeeException e) {
            Assert.fail(String.format("FeeException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (MeetingException e) {
            Assert.fail(String.format("MeetingException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (PersistenceException pe) {
            Assert.fail(String.format("PersistenceException occurred. key %s. Error: %s", pe.getKey(), pe.getMessage()));
        }

    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    //@Rollback(false)
    public void shouldCreatePeriodicDefaultFee() throws Exception {
        //with customer default fee
        boolean isCustomerDefaultFee = true;
        MeetingBO feefrequencyMeeting =
            new MeetingBO(RecurrenceType.WEEKLY, Short.valueOf("2"), new Date(), MeetingType.PERIODIC_FEE);
        FeeEntity fee =
           new RateFeeEntity("test.ShouldCreatePeriodicDefaultFee",
                new CategoryTypeEntity(FeeCategory.ALLCUSTOMERS), glCode, 100.0,
                new FeeFormulaEntity(FeeFormula.AMOUNT), isCustomerDefaultFee,
                TestObjectFactory.getHeadOffice());
        FeeFrequencyTypeEntity feeFrequencyType = new FeeFrequencyTypeEntity(FeeFrequencyType.PERIODIC);
        setFeeAttributesBeforeCreate(isCustomerDefaultFee, fee, FeeStatus.ACTIVE,
                new FeeFrequencyEntity(feeFrequencyType, fee, null, feefrequencyMeeting));
        getFeeDao().create(fee);
        Assert.assertTrue( fee.getFeeId() != null);

        sessionFactory.getCurrentSession().flush();

        FeeEntity newFee = getFeeDao().getDetails(fee.getFeeId());
        Assert.assertEquals(FeeCategory.ALLCUSTOMERS.getValue(), newFee.getCategoryType().getId());
        Assert.assertEquals(true, newFee.isCustomerDefaultFee());
        Assert.assertTrue(vaidateDefaultCustomerFee(fee.getFeeLevels(), fee.getCategoryType().getFeeCategory()));
    }


    private void setFeeAttributesBeforeCreate(
            boolean isCustomerDefaultFee,
            FeeEntity fee,
            FeeStatus feeStatus,
            FeeFrequencyEntity feeFrequency) throws FeeException, PersistenceException {
        fee.setFeeFrequency(feeFrequency);
        fee.setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
        fee.setCreatedBy(userCtx.getId());
        fee.setFeeStatus(getFeeStatusEntity(userCtx, feeStatus));
        makeFeeDefaultToCustomer(fee, isCustomerDefaultFee);
    }

    private FeeDao getFeeDao() {
        return feeDao;
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

    private void makeFeeDefaultToCustomer(FeeEntity fee, boolean isCustomerDefaultFee) throws FeeException {
        if (!isCustomerDefaultFee) {
            return;
        }
        FeeCategory feeCategory;
        try {
            feeCategory = fee.getCategoryType().getFeeCategory();
        } catch (PropertyNotFoundException pnfe) {
            throw new FeeException(pnfe);
        }
        if (feeCategory.equals(FeeCategory.CLIENT)) {
            fee.addFeeLevel(FeeLevel.CLIENTLEVEL);
        } else if (feeCategory.equals(FeeCategory.GROUP)) {
            fee.addFeeLevel(FeeLevel.GROUPLEVEL);
        } else if (feeCategory.equals(FeeCategory.CENTER)) {
            fee.addFeeLevel(FeeLevel.CENTERLEVEL);
        } else if (feeCategory.equals(FeeCategory.ALLCUSTOMERS)) {
            fee.addFeeLevel(FeeLevel.CLIENTLEVEL);
            fee.addFeeLevel(FeeLevel.GROUPLEVEL);
            fee.addFeeLevel(FeeLevel.CENTERLEVEL);
        }
    }

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

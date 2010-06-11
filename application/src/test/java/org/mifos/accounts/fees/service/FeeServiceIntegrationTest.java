package org.mifos.accounts.fees.service;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.entities.AmountFeeEntity;
import org.mifos.accounts.fees.entities.FeeEntity;
import org.mifos.accounts.fees.entities.RateFeeEntity;
import org.mifos.accounts.fees.exceptions.FeeException;
import org.mifos.accounts.fees.servicefacade.FeeService;
import org.mifos.platform.persistence.GenericDaoHibernateImpl;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
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
@ContextConfiguration(locations = {"/org/mifos/config/resources/FeeContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class FeeServiceIntegrationTest {

    @Autowired
    private SessionFactory sessionFactory; //to create dummy GLCodeEntity

    @Autowired
    private FeeService feeService;

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
    public void shouldCreateOnetimeAmountFee() {
        try {
            //FeeService feeService = new FeeServiceImpl(masterEntityDao, feeDao);
            FeeEntity fee = feeService.createOneTimeFee(userCtx,
                "FeeServiceIntegrationTest.CreateOnetimeAmountFee", false, //not customer Default Fee,
                false, 0.0, //not a rate fee
                new Money(TestUtils.RUPEE, "100"),
                FeeCategory.CENTER, null, //feeFormulaEntity
                glCode, TestObjectFactory.getHeadOffice(), FeePayment.UPFRONT);
            Assert.assertTrue( fee.getFeeId() != null);
            Assert.assertTrue(fee instanceof AmountFeeEntity);
        } catch (FeeException e) {
            Assert.fail(String.format("FeeException occurred. key %s. Error: %s", e.getKey(), e.getMessage()));
        } catch (PersistenceException pe) {
            Assert.fail(String.format("PersistenceException occurred. key %s. Error: %s", pe.getKey(), pe.getMessage()));
        }
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreatePeriodicDefaultFee() throws Exception {
        Short recurAfter = Short.valueOf("1");
        FeeEntity fee = feeService.createPeriodicFee(userCtx,
                "FeeServiceIntegrationTest.CreatePeriodicDefaultFee", true,
                true, 100.0, //rate fee
                null, //no fee money
                FeeCategory.ALLCUSTOMERS,
                FeeFormula.AMOUNT,
                glCode,
                TestObjectFactory.getHeadOffice(),
                RecurrenceType.MONTHLY,
                recurAfter);
        Assert.assertTrue( fee.getFeeId() != null);
        Assert.assertTrue(fee instanceof RateFeeEntity);
        Assert.assertEquals(true, fee.isCustomerDefaultFee());
    }

    /*private GLCodeEntity getGLCode(String glCode) {
        return masterEntityDao.retrieveGLCodeEntity(Short.valueOf(glCode));
    }*/

}

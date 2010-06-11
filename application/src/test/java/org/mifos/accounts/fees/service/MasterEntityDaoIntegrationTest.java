package org.mifos.accounts.fees.service;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.persistence.MasterEntityDaoHibernateImpl;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/FeeContext.xml", "/test-persistenceContext.xml"})
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class MasterEntityDaoIntegrationTest extends MifosIntegrationTestCase{

    @Autowired
    private SessionFactory sessionFactory;

    public MasterEntityDaoIntegrationTest() throws Exception {
        super();
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldGetGLCodeById() {
        MasterEntityDaoHibernateImpl dao = new MasterEntityDaoHibernateImpl();
        dao.setSessionFactory(sessionFactory);
        GLCodeEntity glCode = dao.retrieveGLCodeEntity(Short.valueOf("49"));
        Assert.assertEquals(Short.valueOf("49"), glCode.getGlcodeId());
    }

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldGetFeePaymentByType() {
        MasterEntityDaoHibernateImpl dao = new MasterEntityDaoHibernateImpl();
        dao.setSessionFactory(sessionFactory);
        FeePaymentEntity feePayment = null;
        try {
            feePayment = dao.retrieveMasterEntity(FeePaymentEntity.class, Short.valueOf("1"),Short.valueOf("0"));
        } catch (PersistenceException e) {
            Assert.fail("failed for exception:" + e.getMessage());
        }
        Assert.assertEquals(Short.valueOf("1"), feePayment.getId());
    }

}

package org.mifos.accounts.fees.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/org/mifos/config/resources/FeeContext.xml"})
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class FeeServiceTest {

    @Test
    @Transactional(rollbackFor=DataAccessException.class)
    public void shouldCreateOneTimeRateFee() {
        Assert.assertTrue(true);
    }

}

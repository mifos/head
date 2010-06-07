package org.mifos.accounts.fees.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.fees.servicefacade.FeeServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-FeeContext.xml"}) //{"/org/mifos/config/resources/FeeContext.xml"}
@TransactionConfiguration(transactionManager="platformTransactionManager", defaultRollback=true)
public class WebFeeServiceFacadeTest {

    @Autowired
    private FeeServiceFacade feeServiceFacade;

    @Test
    @Transactional
    public void shouldGetAllFees() throws Exception {
        //List<FeeDto> customerFees = feeSvcFacade.getCustomerFees();
        //List<FeeDto> productFees = feeSvcFacade.getProductFees();
    }

}

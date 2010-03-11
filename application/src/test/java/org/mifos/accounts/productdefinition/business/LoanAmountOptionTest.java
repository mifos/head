package org.mifos.accounts.productdefinition.business;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.TestUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanAmountOptionTest {

    @Mock
    private LoanOfferingBO loanProduct;

    @Test
    public void testLoanAmountOption() {
        when(loanProduct.getCurrency()).thenReturn(TestUtils.RUPEE);
         LoanAmountOption l = new LoanAmountOptionImpl(123456789000.00,123456789000.0,123456789000.000,loanProduct);
         Assert.assertEquals("123456789000.0", l.getDefaultLoanAmountString());
         Assert.assertEquals("123456789000.0", l.getMaxLoanAmountString());
         Assert.assertEquals("123456789000.0", l.getMinLoanAmountString());
    }

    class LoanAmountOptionImpl extends LoanAmountOption {

        public LoanAmountOptionImpl(Double minLoanAmount, Double maxLoanAmount, Double defaultLoanAmount,
                LoanOfferingBO loanOffering) {
            super(minLoanAmount, maxLoanAmount, defaultLoanAmount, loanOffering);
        }
    }
}

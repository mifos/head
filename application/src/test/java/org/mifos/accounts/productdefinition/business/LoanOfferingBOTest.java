package org.mifos.accounts.productdefinition.business;

import junit.framework.Assert;
import org.junit.Test;

public class LoanOfferingBOTest {

    @Test
    public void shouldCaptureCapitalAndLiabilityInformation() {
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO();
        CashFlowDetail cashFlowDetail = new CashFlowDetail();
        cashFlowDetail.setIndebtednessRatio(123d);
        loanOfferingBO.setCashFlowDetail(cashFlowDetail);
        Assert.assertTrue(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(0d);
        Assert.assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(-3d);
        Assert.assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(null);
        Assert.assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        loanOfferingBO.setCashFlowDetail(null);
        Assert.assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
    }

}

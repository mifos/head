package org.mifos.accounts.productdefinition.business;

import junit.framework.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoanOfferingBOTest {

    @Test
    public void shouldCaptureCapitalAndLiabilityInformation() {
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO();
        CashFlowDetail cashFlowDetail = new CashFlowDetail();
        cashFlowDetail.setIndebtednessRatio(123d);
        loanOfferingBO.setCashFlowDetail(cashFlowDetail);
        Assert.assertTrue(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(0d);
        assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(-3d);
        assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        cashFlowDetail.setIndebtednessRatio(null);
        assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
        loanOfferingBO.setCashFlowDetail(null);
        assertFalse(loanOfferingBO.shouldCaptureCapitalAndLiabilityInformation());
    }
    
    @Test
    public void shouldValidateCashFlowForInstallments() {
        LoanOfferingBO loanOfferingBO = new LoanOfferingBO();
        loanOfferingBO.setCashFlowCheckEnabled(true);
        CashFlowDetail cashFlowDetail = new CashFlowDetail();
        cashFlowDetail.setCashFlowThreshold(100d);
        loanOfferingBO.setCashFlowDetail(cashFlowDetail);
        assertTrue(loanOfferingBO.shouldValidateCashFlowForInstallments());
        cashFlowDetail.setCashFlowThreshold(0d);
        assertFalse(loanOfferingBO.shouldValidateCashFlowForInstallments());
        cashFlowDetail.setCashFlowThreshold(null);
        assertFalse(loanOfferingBO.shouldValidateCashFlowForInstallments());
        loanOfferingBO.setCashFlowDetail(null);
        assertFalse(loanOfferingBO.shouldValidateCashFlowForInstallments());
        loanOfferingBO.setCashFlowCheckEnabled(false);
        assertFalse(loanOfferingBO.shouldValidateCashFlowForInstallments());
    }

}

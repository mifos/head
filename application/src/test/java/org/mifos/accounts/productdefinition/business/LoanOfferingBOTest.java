package org.mifos.accounts.productdefinition.business;

import junit.framework.Assert;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.springframework.test.annotation.ExpectedException;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import static org.mockito.Mockito.mock;

import java.sql.Timestamp;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;

import static org.mockito.Mockito.*;

public class InterestPostingAccountingEntryTest {
    @Ignore
    private class InterestPostingAccountingEntryForTest extends InterestPostingAccountingEntry {
        private COABO coaBo;
        public void setChartOfAccountsReturnValue(COABO coaBo) {
            this.coaBo = coaBo;
        }
        protected FinancialActionBO getFinancialAction(FinancialActionConstants financialActionId) throws FinancialException {
            return null;
        }
        protected COABO getChartOfAccountsEntry(String glcode) {
            return coaBo;
        }
    };

    @BeforeClass
    public static void classSetup() {
        MifosLogManager.configureLogging();
    }

    @Test
    public void testGetSpecificAccountActionEntry() throws FinancialException {
        InterestPostingAccountingEntryForTest entry = new InterestPostingAccountingEntryForTest();

        /*
        COABO mockChartOfAccountsEntry = mock(COABO.class);
        COABO mockChartOfAccountsHeadEntry = mock(COABO.class);
        when(mockChartOfAccountsHeadEntry.getCategoryType()).thenReturn(GLCategoryType.ASSET);
        when(mockChartOfAccountsEntry.getAccountId()).thenReturn((short)1);
        when(mockChartOfAccountsHeadEntry.getAccountId()).thenReturn((short)2);
        when(mockChartOfAccountsEntry.getCOAHead()).thenReturn(new COABO(2,"what"));
*/
        // workaround for not being able to get commented out code above to work
        // make the COABO entry just return itself to avoid a null pointer return
        // value when this is called

        entry.setChartOfAccountsReturnValue(new COABO(3,"unused text") {
            public COABO getCOAHead() {return this;}
        });

        DateMidnight actionDate = new DateMidnight(2009,9,9);
        DateMidnight postingDate = new DateMidnight(2009,1,1);

        SavingsBO mockSavingsBO = mock(SavingsBO.class);
        SavingsOfferingBO mockSavingsOfferingBO = mock(SavingsOfferingBO.class);
        SavingsTrxnDetailEntity mockAccountTrxnEntity = mock(SavingsTrxnDetailEntity.class);
        when(mockAccountTrxnEntity.getAccount()).thenReturn(mockSavingsBO);
        when(mockAccountTrxnEntity.getInterestAmount()).thenReturn(new Money("10"));
        when(mockAccountTrxnEntity.getActionDate()).thenReturn(actionDate.toDate());
        when(mockAccountTrxnEntity.getTrxnCreatedDate()).thenReturn(new Timestamp(postingDate.getMillis()));
        when(mockSavingsBO.getSavingsOffering()).thenReturn(mockSavingsOfferingBO);
        GLCodeEntity mockGlCode = mock(GLCodeEntity.class);
        when(mockSavingsOfferingBO.getInterestGLCode()).thenReturn(mockGlCode);
        when(mockSavingsOfferingBO.getDepositGLCode()).thenReturn(mockGlCode);

        SavingsInterestPostingFinancialActivity activity =
            new SavingsInterestPostingFinancialActivity(mockAccountTrxnEntity);

        entry.buildAccountEntryForAction(activity);

        List<FinancialTransactionBO> transactions = activity.getFinanacialTransaction();
        FinancialTransactionBO trans = transactions.iterator().next();

        Assert.assertEquals(actionDate.toDate(), trans.getActionDate());
        Assert.assertEquals(postingDate.toDate(), trans.getPostedDate());

    }
}

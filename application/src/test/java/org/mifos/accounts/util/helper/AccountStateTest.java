package org.mifos.accounts.util.helper;

import org.junit.Test;
import org.mifos.accounts.util.helpers.AccountState;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountStateTest {
    
    @Test
    public void isDisbursed(){
        assertFalse(AccountState.isDisbursed(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue()));
        assertFalse(AccountState.isDisbursed(AccountState.LOAN_APPROVED.getValue()));
        assertTrue(AccountState.isDisbursed(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue()));
    }
}

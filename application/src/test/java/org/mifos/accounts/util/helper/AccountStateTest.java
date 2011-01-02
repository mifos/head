package org.mifos.accounts.util.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mifos.accounts.util.helpers.AccountState;

public class AccountStateTest {
    
    @Test
    public void isDisbursed(){
        assertFalse(AccountState.isDisbursed(AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue()));
        assertFalse(AccountState.isDisbursed(AccountState.LOAN_APPROVED.getValue()));
        assertTrue(AccountState.isDisbursed(AccountState.LOAN_ACTIVE_IN_BAD_STANDING.getValue()));
    }
}

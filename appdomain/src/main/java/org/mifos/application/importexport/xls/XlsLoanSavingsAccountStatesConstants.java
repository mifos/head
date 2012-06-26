package org.mifos.application.importexport.xls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;

/**
 * Contais statuses of loan and savings accounts. Each status is a pair of two values: name is string used in xls template,
 * and state is of type AccountState, used as mifos' states representation
 * @author lgadomski
 */
public enum XlsLoanSavingsAccountStatesConstants {
    LOAN_CANCELLED("Cancelled",AccountState.LOAN_CANCELLED),
    LOAN_ACTIVE_GOOD_STANDING("Active in good standing",AccountState.LOAN_ACTIVE_IN_GOOD_STANDING),
    LOAN_ACTIVE_BAD_STANDING("Active in bad standing",AccountState.LOAN_ACTIVE_IN_BAD_STANDING),
    LOAN_APPROVED("Approved",AccountState.LOAN_APPROVED),
    LOAN_CLOSED_OBLIGATIONS_MET("Closed Obligation Met",AccountState.LOAN_CLOSED_OBLIGATIONS_MET),
    LOAN_CLOSED_RESCHEDULED("Closed Rescheduled",AccountState.LOAN_CLOSED_RESCHEDULED),
    LOAN_CLOSED_WRITTEN_OFF("Closed Written OFF",AccountState.LOAN_CLOSED_WRITTEN_OFF),
    LOAN_DISBURSED_TO_LOAN_OFFICER("Disbursed To Loan Officer",AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER),
    LOAN_PARTIAL_APPLICATION("Partial Application",AccountState.LOAN_PARTIAL_APPLICATION),
    LOAN_PENDING_APPROVAL("Pending Approval",AccountState.LOAN_PENDING_APPROVAL),
    
    SAVINGS_ACTIVE("Active",AccountState.SAVINGS_ACTIVE),
    SAVINGS_CANCELLED("Cancelled",AccountState.SAVINGS_CANCELLED),
    SAVINGS_CLOSED("Closed",AccountState.SAVINGS_CLOSED),
    SAVINGS_INACTIVE("Inactive",AccountState.SAVINGS_INACTIVE),
    SAVINGS_PARTIAL_APPLICATION("Partial Application",AccountState.SAVINGS_PARTIAL_APPLICATION),
    SAVINGS_PENDING_APPROVAL("Pending Approval",AccountState.SAVINGS_PENDING_APPROVAL);
    private XlsLoanSavingsAccountStatesConstants(String name, AccountState state){
        this.name=name;
        this.state=state;
    }
    private String name;
    private AccountState state;
    /**
     * Returns list of states for savings or loan accounts.
     * @param typeOfAccount works only for savings accounts and loans accounts. For other types returns empty list.
     * @return list of states for account, where account is savings or loan, or empty list
     */
    public static List<XlsLoanSavingsAccountStatesConstants> getAccountStatesForAccountType(AccountTypes typeOfAccount){
        List<XlsLoanSavingsAccountStatesConstants> accountStates=new ArrayList<XlsLoanSavingsAccountStatesConstants>();
        if(typeOfAccount.equals(AccountTypes.LOAN_ACCOUNT)){
            XlsLoanSavingsAccountStatesConstants[] states={LOAN_PARTIAL_APPLICATION, LOAN_PENDING_APPROVAL, LOAN_APPROVED,
                    LOAN_DISBURSED_TO_LOAN_OFFICER, LOAN_ACTIVE_GOOD_STANDING, LOAN_CLOSED_OBLIGATIONS_MET,
                    LOAN_CLOSED_WRITTEN_OFF, LOAN_CLOSED_RESCHEDULED, LOAN_ACTIVE_BAD_STANDING,
                    LOAN_CANCELLED};
            accountStates=Arrays.asList(states);
        }else if(typeOfAccount.equals(AccountTypes.SAVINGS_ACCOUNT)){
            XlsLoanSavingsAccountStatesConstants[] states={SAVINGS_PARTIAL_APPLICATION,SAVINGS_PENDING_APPROVAL,SAVINGS_CANCELLED,
                    SAVINGS_ACTIVE,SAVINGS_CLOSED, SAVINGS_INACTIVE};
            accountStates=Arrays.asList(states);
        }
        return accountStates;
    }
    /**
     * Account status representation in xls template
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * Account status representation in mifos' code.
     * @return
     */
    public AccountState getState() {
        return state;
    }
    
}

package org.mifos.application.importexport.xls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountTypes;

public enum XlsLoanSavingsFlagsConstants {
    LOAN_WITHDRAW("Withdraw",AccountStateFlag.LOAN_WITHDRAW),
    LOAN_OTHER("Other",AccountStateFlag.LOAN_REJECTED),
    LOAN_REJECTED("Rejected",AccountStateFlag.LOAN_REJECTED),
    LOAN_REVERSAL("Reversal",AccountStateFlag.LOAN_REVERSAL),
    
    SAVINGS_WITHDRAW("Withdraw",AccountStateFlag.SAVINGS_REJECTED),
    SAVINGS_BlACKLISTED("Blacklisted",AccountStateFlag.SAVINGS_BLACKLISTED),
    SAVINGS_REJECTED("Rejected",AccountStateFlag.SAVINGS_REJECTED);
    private String name;
    private AccountStateFlag flag;
    private XlsLoanSavingsFlagsConstants(String name, AccountStateFlag flag) {
        this.name = name;
        this.flag = flag;
    }
    public String getName() {
        return name;
    }
    public AccountStateFlag getFlag() {
        return flag;
    }
    public static List<XlsLoanSavingsFlagsConstants> getAccountFlagsForAccountType(AccountTypes type){
        List<XlsLoanSavingsFlagsConstants> accountFlags=new ArrayList<XlsLoanSavingsFlagsConstants>();
        if(type.equals(AccountTypes.LOAN_ACCOUNT)){
            XlsLoanSavingsFlagsConstants[] states={LOAN_OTHER, LOAN_REJECTED, LOAN_REVERSAL, LOAN_WITHDRAW};
            accountFlags=Arrays.asList(states);
        }else if(type.equals(AccountTypes.SAVINGS_ACCOUNT)){
            XlsLoanSavingsFlagsConstants[] states={SAVINGS_BlACKLISTED, SAVINGS_REJECTED, SAVINGS_WITHDRAW};
            accountFlags=Arrays.asList(states);
        }
        return accountFlags;
    }
    public static XlsLoanSavingsFlagsConstants findByNameForAccountType(String name,AccountTypes type){
        XlsLoanSavingsFlagsConstants constant=null;
        List<XlsLoanSavingsFlagsConstants> constants=getAccountFlagsForAccountType(type);
        for (XlsLoanSavingsFlagsConstants xlsLoanSavingsFlagsConstants : constants) {
            if(xlsLoanSavingsFlagsConstants.getName().equalsIgnoreCase(name)){
                constant= xlsLoanSavingsFlagsConstants;
                break;
            }
        }
        return constant;
    }
}

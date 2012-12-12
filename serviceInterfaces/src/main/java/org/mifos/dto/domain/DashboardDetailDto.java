package org.mifos.dto.domain;

import java.io.Serializable;

public class DashboardDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String globalNumber;
    private String state;
    private String loanOfficer;
    private String balance;
    private String displayName;
    private String url;
    
    public String getGlobalNumber() {
        return globalNumber;
    }
    public void setGlobalNumber(String globalNumber) {
        this.globalNumber = globalNumber;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getLoanOfficer() {
        return loanOfficer;
    }
    public void setLoanOfficer(String loanOfficer) {
        this.loanOfficer = loanOfficer;
    }
    public String getBalance() {
        return balance;
    }
    public void setBalance(String balance) {
        this.balance = balance;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

}

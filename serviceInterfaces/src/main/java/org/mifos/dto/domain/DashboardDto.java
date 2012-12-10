package org.mifos.dto.domain;

import java.io.Serializable;

public class DashboardDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private int borrowersCount;
    private int borrowersGroupCount;
    private int activeCentersCount;
    private int activeGroupsCount;
    private int activeClientsCount;
    private int waitingForApprovalLoansCount;
    private int loansInArrearsCount;
    private int loansToBePaidCurrentWeek;
    
    public int getBorrowersCount() {
        return borrowersCount;
    }
    public void setBorrowersCount(int borrowersCount) {
        this.borrowersCount = borrowersCount;
    }
    public int getBorrowersGroupCount() {
        return borrowersGroupCount;
    }
    public void setBorrowersGroupCount(int borrowersGroupCount) {
        this.borrowersGroupCount = borrowersGroupCount;
    }
    public int getActiveCentersCount() {
        return activeCentersCount;
    }
    public void setActiveCentersCount(int activeCentersCount) {
        this.activeCentersCount = activeCentersCount;
    }
    public int getActiveGroupsCount() {
        return activeGroupsCount;
    }
    public void setActiveGroupsCount(int activeGroupsCount) {
        this.activeGroupsCount = activeGroupsCount;
    }
    public int getActiveClientsCount() {
        return activeClientsCount;
    }
    public void setActiveClientsCount(int activeClientsCount) {
        this.activeClientsCount = activeClientsCount;
    }
    public int getWaitingForApprovalLoansCount() {
        return waitingForApprovalLoansCount;
    }
    public void setWaitingForApprovalLoansCount(int waitingFroApprovalLoansCount) {
        this.waitingForApprovalLoansCount = waitingFroApprovalLoansCount;
    }
    public int getLoansInArrearsCount() {
        return loansInArrearsCount;
    }
    public void setLoansInArrearsCount(int loansInAreasCount) {
        this.loansInArrearsCount = loansInAreasCount;
    }
    public int getLoansToBePaidCurrentWeek() {
        return loansToBePaidCurrentWeek;
    }
    public void setLoansToBePaidCurrentWeek(int loansToBePaidCurrentMonth) {
        this.loansToBePaidCurrentWeek = loansToBePaidCurrentMonth;
    }

}

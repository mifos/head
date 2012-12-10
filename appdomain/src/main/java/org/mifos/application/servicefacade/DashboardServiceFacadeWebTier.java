package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.DashboardDto;
import org.springframework.beans.factory.annotation.Autowired;

public class DashboardServiceFacadeWebTier implements DashboardServiceFacade {

    private final CustomerDao customerDao;
    private final LoanDao loanDao;
    
    @Autowired
    private DashboardServiceFacadeWebTier(CustomerDao customerDao,LoanDao loanDao) {
        this.customerDao=customerDao;
        this.loanDao=loanDao;
    }

    private int getNumberOfBorrowers() {
        return getBorrowers().size();
    }
    
    private int getNumberOfGroupBorrowers() {
        return getBorrowersGroup().size();
    }

    
    private int getNumberOfWaitingForApprovalLoans() {
        return getWaitingForApprovalLoans().size();
    }

    private int getNumberOfLoansInArrears() {
        return getLoansInArrears().size();
    }

    private List<ClientBO> getBorrowers(){
        return customerDao.findAllBorrowers();
    }
    
    private List<GroupBO> getBorrowersGroup(){
        return customerDao.findAllBorrowersGroup();
    }
    
    private List<LoanBO> getWaitingForApprovalLoans() {
        return loanDao.findAllLoansWaitingForApproval();
    }

    private List<LoanBO> getLoansInArrears() {
        return loanDao.findAllBadStandingLoans();
    }
    
    private int getNumberOfLoansToBePaidCurrentWeek(){
        return getLoansToBePaidCurrentWeek().size();
    }
    
    private List<LoanBO> getLoansToBePaidCurrentWeek(){
        return loanDao.findLoansToBePaidCurrentWeek();
    }

    @Override
    public DashboardDto getDashboardDto() {
        DashboardDto dashboardDto = new DashboardDto();

        dashboardDto.setBorrowersCount(getNumberOfBorrowers());
        dashboardDto.setBorrowersGroupCount(getNumberOfGroupBorrowers());
        
        dashboardDto.setActiveClientsCount(customerDao.countOfActiveClients());
        dashboardDto.setActiveGroupsCount(customerDao.countOfActiveGroups());
        dashboardDto.setActiveCentersCount(customerDao.countOfActiveCenters());
        
        dashboardDto.setWaitingForApprovalLoansCount(getNumberOfWaitingForApprovalLoans());
        
        dashboardDto.setLoansInArrearsCount(getNumberOfLoansInArrears());
        
        dashboardDto.setLoansToBePaidCurrentWeek(getNumberOfLoansToBePaidCurrentWeek());
        
        return dashboardDto;
    }
}

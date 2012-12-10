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
    
    @Override
    public List<ClientBO> getBorrowers(){
        return customerDao.findAllBorrowers();
    }
    
    @Override
    public List<GroupBO> getBorrowersGroup(){
        return customerDao.findAllBorrowersGroup();
    }
    
    @Override
    public List<LoanBO> getWaitingForApprovalLoans() {
        return loanDao.findAllLoansWaitingForApproval();
    }

    @Override
    public List<LoanBO> getLoansInArrears() {
        return loanDao.findAllBadStandingLoans();
    }
    
    @Override
    public List<LoanBO> getLoansToBePaidCurrentWeek(){
        return loanDao.findLoansToBePaidCurrentWeek();
    }

    @Override
    public DashboardDto getDashboardDto() {
        DashboardDto dashboardDto = new DashboardDto();

        dashboardDto.setBorrowersCount(customerDao.countAllBorrowers());
        dashboardDto.setBorrowersGroupCount(customerDao.countAllBorrowersGroup());
        
        dashboardDto.setActiveClientsCount(customerDao.countOfActiveClients());
        dashboardDto.setActiveGroupsCount(customerDao.countOfActiveGroups());
        dashboardDto.setActiveCentersCount(customerDao.countOfActiveCenters());
        
        dashboardDto.setWaitingForApprovalLoansCount(loanDao.countAllLoansWaitingForApproval());
        
        dashboardDto.setLoansInArrearsCount(loanDao.countAllBadStandingLoans());
        
        dashboardDto.setLoansToBePaidCurrentWeek(loanDao.countLoansToBePaidCurrentWeek());
        
        return dashboardDto;
    }
}

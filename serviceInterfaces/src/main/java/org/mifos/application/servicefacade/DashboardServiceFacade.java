package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.dto.domain.DashboardDto;

public interface DashboardServiceFacade {

    DashboardDto getDashboardDto();
    
    String[] getLoanHeaders();
    String[] getCustomerHeaders();
    
    int countBorrowers();
    int countBorrowersGroup();
    int countOfActiveClients();
    int countOfActiveGroups();
    int countOfActiveCenters();
    int countLoansWaitingForApproval();
    int countBadStandingLoans();
    int countLoansToBePaidCurrentWeek();
    
    List<?> getActiveClients(int position,int noOfObjects,String ordering);
    List<?> getActiveGroups(int position,int noOfObjects,String ordering);
    List<?> getActiveCenters(int position,int noOfObjects,String ordering);
    List<?> getBorrowers(int position,int noOfObj,String ordering);
    List<?> getBorrowersGroup(int position,int noOfObjects,String ordering);
    List<?> getWaitingForApprovalLoans(int position,int noOfObjects,String ordering);
    List<?> getLoansInArrears(int position,int noOfObjects,String ordering);
    List<?> getLoansToBePaidCurrentWeek(int position,int noOfObjects,String ordering);

}

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.dto.domain.DashboardDto;

public interface DashboardServiceFacade {

    DashboardDto getDashboardDto();
    
    String[] getHeaders();
    
    List<?> getActiveClients();
    List<?> getActiveGroups();
    List<?> getActiveCenters();
    List<?> getBorrowers();
    List<?> getBorrowersGroup();
    List<?> getWaitingForApprovalLoans();
    List<?> getLoansInArrears();
    List<?> getLoansToBePaidCurrentWeek();
}

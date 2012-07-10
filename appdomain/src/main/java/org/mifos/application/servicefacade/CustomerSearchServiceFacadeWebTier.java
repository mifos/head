package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerSearchDto;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.screen.CenterSearchResultDto;
import org.mifos.dto.screen.ClientSearchResultDto;
import org.mifos.dto.screen.CustomerHierarchyDto;
import org.mifos.dto.screen.GroupSearchResultDto;
import org.mifos.dto.screen.LoanAccountSearchResultDto;
import org.mifos.dto.screen.SavingsAccountSearchResultDto;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomerSearchServiceFacadeWebTier implements
		CustomerSearchServiceFacade {

	@Autowired
	CustomerServiceFacade customerServiceFacade;
	
	@Autowired
	PersonnelDao personnelDao;
	
	@Autowired
	OfficeDao officeDao;
	
	@Override
	public CustomerHierarchyDto search(String searchString, Short officeId, int pageNumber, int pageSize  ){
		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        if (searchString == null) {
            throw new MifosRuntimeException(CenterConstants.NO_SEARCH_STRING);
        }

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new MifosRuntimeException(CenterConstants.NO_SEARCH_STRING);
        }

        CustomerHierarchyDto customerHierarchyDto = new CustomerHierarchyDto();
        QueryResult searchResult = null;
        List<CustomerSearchDto> resultList = null;
        
        try {
            searchResult = new CustomerPersistence().search(normalisedSearchString, officeId, userContext.getId(), userContext.getBranchId());
        } catch ( PersistenceException e ) {
            throw new MifosRuntimeException(e);
        }
       
        try {
            resultList = searchResult.get(pageNumber, pageSize);
            customerHierarchyDto.setSize(searchResult.getSize());
            customerHierarchyDto.setSearchResultSize(resultList.size());
        } catch ( HibernateSearchException e ){
            throw new MifosRuntimeException(e);
        }
		/* FIXME: QueryResult.get returns CustomerSearchDto with messed up customers and accounts data.
		 */
        for ( CustomerSearchDto customerSearchDto : resultList ){
        	if ( customerSearchDto.getCustomerType() == 1){
        		ClientSearchResultDto clientSearchResultDto = new ClientSearchResultDto();
        		clientSearchResultDto.setOfficeId(customerSearchDto.getOfficeId());
        		clientSearchResultDto.setOfficeName(customerSearchDto.getOfficeName());
        		
        		/* QueryResult.get returns CustomerSearchDto (as Client) with clientName as centerName, 
        		 * clientGobalCustNum as centerGobalCustNum and vice versa 
        		 */
        		clientSearchResultDto.setClientName(customerSearchDto.getCenterName());
        		clientSearchResultDto.setClientGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
        		clientSearchResultDto.setGroupName(customerSearchDto.getGroupName());
        		clientSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		clientSearchResultDto.setCenterName(customerSearchDto.getClientName());
        		clientSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getClientGlobalCustNum());
        		
        		clientSearchResultDto.setBranchName(customerSearchDto.getBranchName());
        		clientSearchResultDto.setBranchId(customerSearchDto.getBranchGlobalNum());
        		clientSearchResultDto.setCustomerStatusId(customerSearchDto.getCustomerStatus());
        		
        		clientSearchResultDto.setLoanOfficerName(customerSearchDto.getLoanOfficerName());
        		clientSearchResultDto.setLoanOfficerId(customerSearchDto.getLoanOffcerGlobalNum());
        		
        		for (Object loanGlobalAccount : customerSearchDto.getLoanGlobalAccountNum()){
        			clientSearchResultDto.getLoanGlobalAccountNum().add((String) loanGlobalAccount);
        		}
        		for (Object savingGlobalAccount : customerSearchDto.getSavingsGlobalAccountNum()){
        			clientSearchResultDto.getSavingsGlobalAccountNum().add((String) savingGlobalAccount);
        		}
        		
        		clientSearchResultDto.setStatus(customerSearchDto.getStatus());
        		
        		customerHierarchyDto.getClients().add(clientSearchResultDto);
        	} else if ( customerSearchDto.getCustomerType() == 2){ 
        		
        		GroupSearchResultDto groupSearchResultDto = new GroupSearchResultDto();
        		
        		groupSearchResultDto.setOfficeId(customerSearchDto.getOfficeId());
        		groupSearchResultDto.setOfficeName(customerSearchDto.getOfficeName());
        		
        		/* QueryResult.get returns CustomerSearchDto (as Group) with groupName as centerName, 
        		 * groupGobalCustNum as centerGobalCustNum and vice versa 
        		 */
        		groupSearchResultDto.setGroupName(customerSearchDto.getCenterName());
        		groupSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
        		groupSearchResultDto.setCenterName(customerSearchDto.getGroupName());
        		groupSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		
        		groupSearchResultDto.setBranchName(customerSearchDto.getBranchName());
        		groupSearchResultDto.setBranchId(customerSearchDto.getBranchGlobalNum());
        		groupSearchResultDto.setCustomerStatusId(customerSearchDto.getCustomerStatus());
        		
        		groupSearchResultDto.setLoanOfficerName(customerSearchDto.getLoanOfficerName());
        		groupSearchResultDto.setLoanOfficerId(customerSearchDto.getLoanOffcerGlobalNum());
        		
        		for (Object loanGlobalAccount : customerSearchDto.getLoanGlobalAccountNum()){
        			groupSearchResultDto.getLoanGlobalAccountNum().add((String) loanGlobalAccount);
        		}
        		for (Object savingGlobalAccount : customerSearchDto.getSavingsGlobalAccountNum()){
        			groupSearchResultDto.getSavingsGlobalAccountNum().add((String) savingGlobalAccount);
        		}
        		
        		groupSearchResultDto.setStatus(customerSearchDto.getStatus());
        		
        		customerHierarchyDto.getGroups().add(groupSearchResultDto);
        	} else if ( customerSearchDto.getCustomerType() == 3 ){ 
        		
        		CenterSearchResultDto centerSearchResultDto = new CenterSearchResultDto();
        		
        		centerSearchResultDto.setOfficeId(customerSearchDto.getOfficeId());
        		centerSearchResultDto.setOfficeName(customerSearchDto.getOfficeName());
        		
        		centerSearchResultDto.setCenterName(customerSearchDto.getCenterName());
        		centerSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
        		
        		centerSearchResultDto.setBranchName(customerSearchDto.getBranchName());
        		centerSearchResultDto.setBranchId(customerSearchDto.getBranchGlobalNum());
        		centerSearchResultDto.setCustomerStatusId(customerSearchDto.getCustomerStatus());
        		
        		centerSearchResultDto.setLoanOfficerName(customerSearchDto.getLoanOfficerName());
        		centerSearchResultDto.setLoanOfficerId(customerSearchDto.getLoanOffcerGlobalNum());
        		
        		for (Object savingGlobalAccount : customerSearchDto.getSavingsGlobalAccountNum()){
        			centerSearchResultDto.getSavingsGlobalAccountNum().add((String) savingGlobalAccount);
        		}
        		
        		centerSearchResultDto.setStatus(customerSearchDto.getStatus());
        		
        		customerHierarchyDto.getCenters().add(centerSearchResultDto);
        	} else if ( customerSearchDto.getLoanGlobalAccountNumber() != null && (customerSearchDto.getCustomerType() == 5 || customerSearchDto.getCustomerType() == 4) ) { 
        		
        		LoanAccountSearchResultDto loanAccountSearchResultDto = new LoanAccountSearchResultDto();
        		
        		loanAccountSearchResultDto.setLoanGlobalAccountNum(customerSearchDto.getLoanGlobalAccountNumber());
        		
        		loanAccountSearchResultDto.setOfficeId(customerSearchDto.getOfficeId());
        		loanAccountSearchResultDto.setOfficeName(customerSearchDto.getOfficeName());
        		
        		loanAccountSearchResultDto.setBranchName(customerSearchDto.getBranchName());
        		loanAccountSearchResultDto.setBranchId(customerSearchDto.getBranchGlobalNum());
        		loanAccountSearchResultDto.setAccountStatusId(customerSearchDto.getCustomerStatus());
        		
        		loanAccountSearchResultDto.setLoanOfficerName(customerSearchDto.getLoanOfficerName());
        		loanAccountSearchResultDto.setLoanOfficerId(customerSearchDto.getLoanOffcerGlobalNum());
        		
        		if ( customerSearchDto.getClientGlobalCustNum() != null){
            		loanAccountSearchResultDto.setCenterName(customerSearchDto.getClientName());
            		loanAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getClientGlobalCustNum());
            		loanAccountSearchResultDto.setClientName(customerSearchDto.getCenterName());
            		loanAccountSearchResultDto.setClientGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
            		loanAccountSearchResultDto.setGroupName(customerSearchDto.getGroupName());
            		loanAccountSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		} else if( customerSearchDto.getCustomerType() == 5 ) {
                    loanAccountSearchResultDto.setGroupName(customerSearchDto.getCenterName());
                    loanAccountSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
                    loanAccountSearchResultDto.setCenterName(customerSearchDto.getGroupName());
                    loanAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		} 
        		else {
            		loanAccountSearchResultDto.setClientName(customerSearchDto.getCenterName());
            		loanAccountSearchResultDto.setClientGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
            		loanAccountSearchResultDto.setGroupName(customerSearchDto.getClientName());
            		loanAccountSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getClientGlobalCustNum());
            		loanAccountSearchResultDto.setCenterName(customerSearchDto.getGroupName());
            		loanAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		}
        		
        		loanAccountSearchResultDto.setStatus(customerSearchDto.getStatus());
        		
        		customerHierarchyDto.setLoan(loanAccountSearchResultDto);
        	} else if ( customerSearchDto.getLoanGlobalAccountNumber() != null || customerSearchDto.getCustomerType() == 6){
        		
        		SavingsAccountSearchResultDto savingsAccountSearchResultDto = new SavingsAccountSearchResultDto();
        		
        		savingsAccountSearchResultDto.setSavingsGlobalAccountNum(customerSearchDto.getLoanGlobalAccountNumber());
        		
        		savingsAccountSearchResultDto.setOfficeId(customerSearchDto.getOfficeId());
        		savingsAccountSearchResultDto.setOfficeName(customerSearchDto.getOfficeName());
        		
        		savingsAccountSearchResultDto.setBranchName(customerSearchDto.getBranchName());
        		savingsAccountSearchResultDto.setBranchId(customerSearchDto.getBranchGlobalNum());
        		savingsAccountSearchResultDto.setAccountStatusId(customerSearchDto.getCustomerStatus());
        		
        		savingsAccountSearchResultDto.setLoanOfficerName(customerSearchDto.getLoanOfficerName());
        		savingsAccountSearchResultDto.setLoanOfficerId(customerSearchDto.getLoanOffcerGlobalNum());
        		
        		if ( customerSearchDto.getClientGlobalCustNum() != null){
            		savingsAccountSearchResultDto.setCenterName(customerSearchDto.getClientName());
            		savingsAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getClientGlobalCustNum());
            		savingsAccountSearchResultDto.setClientName(customerSearchDto.getCenterName());
            		savingsAccountSearchResultDto.setClientGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
            		savingsAccountSearchResultDto.setGroupName(customerSearchDto.getGroupName());
            		savingsAccountSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		} else if ( customerSearchDto.getGroupGlobalCustNum() != null ){
            		savingsAccountSearchResultDto.setClientName(customerSearchDto.getClientName());
            		savingsAccountSearchResultDto.setClientGlobalCustNum(customerSearchDto.getClientGlobalCustNum());
            		savingsAccountSearchResultDto.setGroupName(customerSearchDto.getCenterName());
            		savingsAccountSearchResultDto.setGroupGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
            		savingsAccountSearchResultDto.setCenterName(customerSearchDto.getGroupName());
            		savingsAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getGroupGlobalCustNum());
        		} else {
            		savingsAccountSearchResultDto.setCenterName(customerSearchDto.getCenterName());
            		savingsAccountSearchResultDto.setCenterGlobalCustNum(customerSearchDto.getCenterGlobalCustNum());
        		}
        		
        		savingsAccountSearchResultDto.setStatus(customerSearchDto.getStatus());
        		
        		customerHierarchyDto.setSavings(savingsAccountSearchResultDto);
        	}
        }
        
		return customerHierarchyDto;
	}

}

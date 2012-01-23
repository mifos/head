package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.customers.business.CustomerSearchDto;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.screen.CenterSearchResultDto;
import org.mifos.dto.screen.ClientSearchResultDto;
import org.mifos.dto.screen.CustomerHierarchyDto;
import org.mifos.dto.screen.GroupSearchResultDto;
import org.mifos.framework.exceptions.ApplicationException;
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
	public CustomerHierarchyDto search(String searchString, Short officeId, int pageNumber, int pageSize  ) throws ApplicationException{
		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);

        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        QueryResult searchResult = new CustomerPersistence().search(normalisedSearchString, officeId, userContext.getId(), userContext.getBranchId());
        List<CustomerSearchDto> resultList = searchResult.get(pageNumber, pageSize);
        
        CustomerHierarchyDto customerHierarchyDto = new CustomerHierarchyDto();
        customerHierarchyDto.setSize(searchResult.getSize());
        
        for ( CustomerSearchDto customerSearchDto : resultList ){
        	//client
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
        	} else if ( customerSearchDto.getCustomerType() == 2){ // Group
        		
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
        	} else if ( customerSearchDto.getCustomerType() == 3 ){ // Group
        		
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
        	}
        }
        
		return customerHierarchyDto;
	}

}

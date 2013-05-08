/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.platform.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.config.ClientRules;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.CenterDescriptionDto;
import org.mifos.dto.domain.ClientDescriptionDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerHierarchyDto;
import org.mifos.dto.domain.GroupDescriptionDto;
import org.mifos.dto.domain.OverdueCustomer;
import org.mifos.dto.domain.OverdueLoan;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PersonnelRESTController {

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;
    
    @Autowired
    private LoanAccountServiceFacade loanAccountServiceFacade;

    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
    private LoanDao loanDao;

    @Autowired
    private PersonnelDao personnelDao;

    @RequestMapping(value = "personnel/id-current", method = RequestMethod.GET)
    public @ResponseBody
    PersonnelInformationDto getCurrentPersonnel() {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return personnelServiceFacade.getPersonnelInformationDto((long)user.getUserId(), "");
    }

    @RequestMapping(value = "personnel/id-current/clients", method = RequestMethod.GET)
    public @ResponseBody
    CustomerHierarchyDto getCustomersUnderPersonnel() {
        PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(getCurrentPersonnel().getPersonnelId());

        CustomerHierarchyDto hierarchy = new CustomerHierarchyDto();

        if (ClientRules.getCenterHierarchyExists()) {
            for (CustomerDetailDto center : this.customerDao.findActiveCentersUnderUser(loanOfficer)) {
                CenterDescriptionDto centerDescription = new CenterDescriptionDto();
                centerDescription.setId(center.getCustomerId());
                centerDescription.setDisplayName(center.getDisplayName());
                centerDescription.setGlobalCustNum(center.getGlobalCustNum());
                centerDescription.setSearchId(center.getSearchId());
                hierarchy.getCenters().add(centerDescription);
            }
        }

        allGroups:
        for (CustomerDetailDto group : this.customerDao.findGroupsUnderUser(loanOfficer)) {
            GroupDescriptionDto groupDescription = new GroupDescriptionDto();
            groupDescription.setId(group.getCustomerId());
            groupDescription.setDisplayName(group.getDisplayName());
            groupDescription.setGlobalCustNum(group.getGlobalCustNum());
            groupDescription.setSearchId(group.getSearchId());

            for (ClientBO client : this.customerDao.findAllExceptClosedAndCancelledClientsUnderParent(group.getSearchId(), loanOfficer.getOffice().getOfficeId())) {
                ClientDescriptionDto clientDescription = new ClientDescriptionDto();
                clientDescription.setId(client.getCustomerId());
                clientDescription.setDisplayName(client.getDisplayName());
                clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                clientDescription.setSearchId(client.getSearchId());
                groupDescription.getClients().add(clientDescription);
            }

            for (CenterDescriptionDto center : hierarchy.getCenters()) {
                if (group.getSearchId().startsWith(center.getSearchId())) {
                    center.getGroups().add(groupDescription);
                    continue allGroups;
                }
            }
            hierarchy.getGroups().add(groupDescription);
        }

        for (ClientBO client : this.customerDao.findAllExceptClosedAndCancelledClientsWithoutGroupForLoanOfficer(loanOfficer.getPersonnelId(), loanOfficer.getOffice().getOfficeId())) {
            ClientDescriptionDto clientDescription = new ClientDescriptionDto();
                clientDescription.setId(client.getCustomerId());
                clientDescription.setDisplayName(client.getDisplayName());
                clientDescription.setGlobalCustNum(client.getGlobalCustNum());
                clientDescription.setSearchId(client.getSearchId());
                hierarchy.getClients().add(clientDescription);
        }

        return hierarchy;
    }
    
    @RequestMapping(value = "personnel/id-current/overdue_borrowers", method = RequestMethod.GET)
    public @ResponseBody
    OverdueCustomer[] getOverdueBorrowersUnderPersonnel() {
    	List<OverdueCustomer> overdueCustomers = new ArrayList<OverdueCustomer>();
    	PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(getCurrentPersonnel().getPersonnelId());
        for (ClientBO client : this.customerDao.findAllExceptClosedAndCancelledClientsWithoutGroupForLoanOfficer(loanOfficer.getPersonnelId(), 
        		loanOfficer.getOffice().getOfficeId())) {
        	OverdueCustomer customerToAdd = null;
        	List<LoanBO> loans = client.getOpenLoanAccounts();
        	for (LoanBO loan: loans) {
        		if (loan.getTotalAmountInArrears() != null && loan.getTotalAmountInArrears().isNonZero()) {
                	LoanInformationDto loanInfo = loanAccountServiceFacade.retrieveLoanInformation(loan.getGlobalAccountNum());
                	if (loanInfo.isDisbursed()) {
	                	if (customerToAdd == null) {
	        				customerToAdd = new OverdueCustomer();
	        				customerToAdd.setDisplayName(client.getDisplayName());
	        				customerToAdd.setGlobalCustNum(client.getGlobalCustNum());
	        				customerToAdd.setOverdueLoans(new ArrayList<OverdueLoan>());
	        				overdueCustomers.add(customerToAdd);
	        			}
	        			OverdueLoan overdueLoan = new OverdueLoan(loan.getTotalAmountInArrears().toString(), loan.getGlobalAccountNum(), loanInfo.getPrdOfferingName(),
	        					loan.getAccountState().getName(), new Integer(loan.getAccountState().getId()), loan.getTotalAmountDue().toString());
	        			customerToAdd.getOverdueLoans().add(overdueLoan);
                	}
                }
        	}
        }
        return overdueCustomers.toArray(new OverdueCustomer[]{});
    }

    @RequestMapping(value = "personnel/id-current/meetings-{day}", method = RequestMethod.GET)
    public @ResponseBody
    CustomerHierarchyDto getCustomersUnderPersonnelForSpecifiedDay(@PathVariable String day) {
        DateTime specifiedDay = DateTimeFormat.forPattern("dd-MM-yyyy").parseDateTime(day);

        return personnelServiceFacade.getLoanOfficerCustomersHierarchyForDay(getCurrentPersonnel().getPersonnelId(), specifiedDay);
    }
}

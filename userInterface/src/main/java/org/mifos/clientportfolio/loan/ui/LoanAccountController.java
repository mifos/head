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

package org.mifos.clientportfolio.loan.ui;

import java.util.List;

import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoanAccountController {

	private final LoanAccountServiceFacade loanAccountServiceFacade;

	@Autowired
    public LoanAccountController(LoanAccountServiceFacade loanAccountServiceFacade) {
		this.loanAccountServiceFacade = loanAccountServiceFacade;
    }
	
	public CustomerSearchResultsDto searchCustomers(CustomerSearchFormBean formBean) {
        
    	CustomerSearchDto customerSearchDto = new CustomerSearchDto(formBean.getSearchString(), formBean.getPage(), formBean.getPageSize());
    	List<CustomerSearchResultDto> pagedDetails = this.loanAccountServiceFacade.retrieveCustomersThatQualifyForLoans(customerSearchDto);
    	
    	int firstResult = formBean.getPage() * formBean.getPageSize() - (formBean.getPageSize()-1);
		SearchDetailsDto searchDetails = new SearchDetailsDto(pagedDetails.size(), firstResult, formBean.getPage(), formBean.getPageSize());
        return new CustomerSearchResultsDto(searchDetails, pagedDetails);
    }	
}
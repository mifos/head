/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.ui.client.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.client.service.ClientDto;
import org.mifos.client.service.ClientService;
import org.mifos.loan.service.LoanDto;
import org.mifos.loan.service.LoanService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 */
public class ClientDetailController extends MultiActionController {

	private ClientService clientService;
	private LoanService loanService;
	
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") //rationale: This is the signature of the superclass's method that we're overriding
	public ModelAndView clientDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
        Integer clientId = Integer.valueOf(request.getParameter("id"));
        Map<String, Object> model = new HashMap<String, Object>();
        ClientDto clientDto = getClientService().getClient(clientId); 
        model.put("client", clientDto);
        
        List<LoanDto> loans = getLoanService().findLoansForClient(clientId);
        model.put("loans", loans);

        return new ModelAndView("clientDetail", "model", model);
	}
	
	public ClientService getClientService() {
		return clientService;
	}

	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

    public LoanService getLoanService() {
        return loanService;
    }

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

	
}

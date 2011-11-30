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

import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.core.MifosRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.ArrayList;

@Controller
public class AccountRESTController {

    @Autowired
    private CenterServiceFacade centerServiceFacade;

    @Autowired
    private SavingsServiceFacade savingsServiceFacade;

    @Autowired
    private AccountBusinessService accountBusinessService;

    @RequestMapping(value = "/account/trxnhistory/num-{globalAccountNum}", method = RequestMethod.GET)
    public @ResponseBody
    List<TransactionHistoryDto> getAccountTransactionHistoryByNumber(@PathVariable String globalAccountNum) throws Exception {
        AccountTypes type = accountBusinessService.getTypeBySystemId(globalAccountNum);
        if (AccountTypes.LOAN_ACCOUNT.equals(type)) {
            return centerServiceFacade.retrieveAccountTransactionHistory(globalAccountNum);
        }
        if (AccountTypes.SAVINGS_ACCOUNT.equals(type)) {
            return new ArrayList<TransactionHistoryDto>(savingsServiceFacade.retrieveTransactionHistory(globalAccountNum));
        }
        throw new MifosRuntimeException("Unsupported account type.");
    }
}

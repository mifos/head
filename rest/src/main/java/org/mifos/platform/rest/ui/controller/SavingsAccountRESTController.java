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
package org.mifos.platform.rest.ui.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SavingsAccountRESTController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SavingsDao savingsDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PersonnelDao personnelDao;

    @RequestMapping(value = "account/savings/deposit/num-{globalAccountNum}", method = RequestMethod.POST)
    public final @ResponseBody
    Map<String, String> deposit(@PathVariable String globalAccountNum, HttpServletRequest request)
            throws Exception {

        String amountString = request.getParameter("amount");
        String client = request.getParameter("client");
        BigDecimal amount = new BigDecimal(amountString);

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReferenceDto userDto = new UserReferenceDto((short) user.getUserId());

        SavingsBO savingsBO = savingsDao.findBySystemId(globalAccountNum);

        Integer accountId = savingsBO.getAccountId();

        AccountReferenceDto accountDto = new AccountReferenceDto(accountId);

        PaymentTypeDto paymentType = accountService.getSavingsPaymentTypes().get(0);
        DateTime today = new DateTime();
        LocalDate receiptDate = today.toLocalDate();

        // from where these parameter should come?
        String receiptId = "";


        ClientBO clientBO = customerDao.findClientBySystemId(client);
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(clientBO.getCustomerId());

        AccountPaymentParametersDto payment = new AccountPaymentParametersDto(userDto, accountDto, amount, today.toLocalDate(),
                paymentType, globalAccountNum, receiptDate, receiptId, customer);

        Money balanceBeforePayment = savingsBO.getSavingsBalance();
        accountService.makePayment(payment);

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("clientName", clientBO.getDisplayName());
        map.put("clientNumber", clientBO.getGlobalCustNum());
        map.put("savingsDisplayName", savingsBO.getSavingsOffering().getPrdOfferingName());
        map.put("paymentDate", today.toLocalDate().toString());
        map.put("paymentTime", today.toLocalTime().toString());
        map.put("paymentAmount", savingsBO.getLastPmnt().getAmount().toString());
        map.put("paymentMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("balanceBeforePayment", balanceBeforePayment.toString());
        map.put("balanceAfterPayment", savingsBO.getSavingsBalance().toString());
        return map;
    }
}

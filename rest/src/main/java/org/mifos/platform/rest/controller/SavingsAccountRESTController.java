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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SavingsAccountRESTController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private SavingsServiceFacade savingsServiceFacade;

    @Autowired
    private SavingsDao savingsDao;

    @Autowired
    private PersonnelDao personnelDao;

    @RequestMapping(value = "account/savings/deposit/num-{globalAccountNum}", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> deposit(@PathVariable String globalAccountNum, 
    		                    @RequestParam(value="amount") String amountString) throws Exception {
        return doSavingsTrxn(globalAccountNum, amountString, TrxnTypes.savings_deposit);
    }

    @RequestMapping(value = "account/savings/withdraw/num-{globalAccountNum}", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> withdraw(@PathVariable String globalAccountNum, 
    		                     @RequestParam(value="amount") String amountString) throws Exception {
        return doSavingsTrxn(globalAccountNum, amountString, TrxnTypes.savings_withdrawal);
    }

    @RequestMapping(value = "/account/savings/num-{globalAccountNum}", method = RequestMethod.GET)
    public @ResponseBody
    SavingsAccountDetailDto getSavingsByNumber(@PathVariable String globalAccountNum, HttpServletRequest request) throws Exception {
        SavingsBO savings = this.savingsDao.findBySystemId(globalAccountNum);
        savings.setUserContext((UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession()));
        return savingsServiceFacade.retrieveSavingsAccountDetails(savings.getAccountId().longValue());
    }

    @RequestMapping(value = "/account/savings/due/num-{globalAccountNum}", method = RequestMethod.GET)
    public @ResponseBody
    SavingsAccountDepositDueDto getSavingsDepositDueDetailsByNumber(@PathVariable String globalAccountNum) throws Exception {
        return savingsServiceFacade.retrieveDepositDueDetails(globalAccountNum);
    }

    private Map<String, String> doSavingsTrxn(String globalAccountNum, String amountString, TrxnTypes trxnType) throws Exception {
        BigDecimal amount = new BigDecimal(amountString);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MifosRuntimeException("Amount must be greater than 0");
        }

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        SavingsBO savingsBO = savingsDao.findBySystemId(globalAccountNum);

        Integer accountId = savingsBO.getAccountId();

        PaymentTypeDto paymentType = trxnType.equals(TrxnTypes.savings_deposit) ?
                accountService.getSavingsPaymentTypes().get(0) :
                accountService.getSavingsWithdrawalTypes().get(0);
        DateTime today = new DateTime();
        LocalDate receiptDate = today.toLocalDate();

        // from where these parameter should come?
        String receiptId = "";

        CustomerBO client = savingsBO.getCustomer();
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(client.getCustomerId());

        Money balanceBeforePayment = savingsBO.getSavingsBalance();
        if (trxnType.equals(TrxnTypes.savings_deposit)) {
            SavingsDepositDto savingsDeposit = new SavingsDepositDto(accountId.longValue(), savingsBO.getCustomer().getCustomerId().longValue(),
                    today.toLocalDate(), amount.doubleValue(), paymentType.getValue().intValue(), receiptId, receiptDate,
                    Locale.UK);
            this.savingsServiceFacade.deposit(savingsDeposit);
        }
        else {
            SavingsWithdrawalDto savingsWithdrawal = new SavingsWithdrawalDto(accountId.longValue(), savingsBO.getCustomer().getCustomerId().longValue(),
                    today.toLocalDate(), amount.doubleValue(), paymentType.getValue().intValue(), receiptId, receiptDate,
                    Locale.UK);
            this.savingsServiceFacade.withdraw(savingsWithdrawal);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
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

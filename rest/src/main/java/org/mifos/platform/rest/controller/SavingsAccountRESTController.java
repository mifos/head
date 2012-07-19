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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsAdjustmentDto;
import org.mifos.dto.domain.SavingsDepositDto;
import org.mifos.dto.domain.SavingsWithdrawalDto;
import org.mifos.dto.screen.SavingsAccountDepositDueDto;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
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
    
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private SavingsProductDao savingsProductDao;
    
    @RequestMapping(value = "account/savings/num-{globalAccountNum}/deposit", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> deposit(@PathVariable String globalAccountNum,
    		                    @RequestParam BigDecimal amount,
								@RequestParam String trxnDate,
								@RequestParam(required=false) Short receiptId,
								@RequestParam(required=false) String receiptDate,
								@RequestParam Short paymentTypeId) throws Exception {
        return doSavingsTrxn(globalAccountNum, amount, trxnDate, receiptId, receiptDate, paymentTypeId, TrxnTypes.savings_deposit);
    }

    @RequestMapping(value = "account/savings/num-{globalAccountNum}/withdraw", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> withdraw(@PathVariable String globalAccountNum,
    		                     @RequestParam BigDecimal amount,
								 @RequestParam String trxnDate,
 								 @RequestParam(required=false) Short receiptId,
 								 @RequestParam(required=false) String receiptDate,
 								 @RequestParam Short paymentTypeId) throws Exception {
        return doSavingsTrxn(globalAccountNum, amount, trxnDate, receiptId, receiptDate, paymentTypeId, TrxnTypes.savings_withdrawal);
    }

    @RequestMapping(value = "account/savings/num-{globalAccountNum}/adjustment", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> applyAdjustment(@PathVariable String globalAccountNum,
                                        @RequestParam BigDecimal amount,
                                        @RequestParam String note,
                                        @RequestParam(required=false) Integer paymentId) throws Exception {

    	validateAmount(amount);

        validateNote(note);

    	SavingsBO savingsBO = savingsDao.findBySystemId(globalAccountNum);
    	new SavingsPersistence().initialize(savingsBO);
    	Integer accountId = savingsBO.getAccountId();
    	Long savingsId = Long.valueOf(accountId.toString());

        SavingsAdjustmentDto savingsAdjustment = new SavingsAdjustmentDto(savingsId, amount.doubleValue(), note,
                (paymentId == null) ? savingsBO.getLastPmnt().getPaymentId() : paymentId, new LocalDate(savingsBO.getLastPmnt().getPaymentDate()));
        Money balanceBeforePayment = savingsBO.getSavingsBalance();

    	this.savingsServiceFacade.adjustTransaction(savingsAdjustment);

    	MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    	DateTime today = new DateTime();
    	CustomerBO client = savingsBO.getCustomer();

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("savingsDisplayName", savingsBO.getSavingsOffering().getPrdOfferingName());
        map.put("adjustmentDate", today.toLocalDate().toString());
        map.put("adjustmentTime", today.toLocalTime().toString());
        map.put("adjustmentAmount", savingsBO.getLastPmnt().getAmount().toString());
        map.put("adjustmentMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("balanceBeforeAdjustment", balanceBeforePayment.toString());
        map.put("balanceAfterAdjustment", savingsBO.getSavingsBalance().toString());
        map.put("note", note);

    	return map;
    }

    @RequestMapping(value = "/account/savings/num-{globalAccountNum}", method = RequestMethod.GET)
    public @ResponseBody
    SavingsAccountDetailDto getSavingsByNumber(@PathVariable String globalAccountNum, HttpServletRequest request) throws Exception {
        SavingsBO savings = this.savingsDao.findBySystemId(globalAccountNum);
        savings.setUserContext((UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession()));
        return savingsServiceFacade.retrieveSavingsAccountDetails(savings.getAccountId().longValue());
    }

    @RequestMapping(value = "/account/savings/num-{globalAccountNum}/due", method = RequestMethod.GET)
    public @ResponseBody
    SavingsAccountDepositDueDto getSavingsDepositDueDetailsByNumber(@PathVariable String globalAccountNum) throws Exception {
        return savingsServiceFacade.retrieveDepositDueDetails(globalAccountNum);
    }

    private Map<String, String> doSavingsTrxn(String globalAccountNum, BigDecimal amount, 
    		String trxnDate, Short receiptId, String receiptDate, Short paymentTypeId, TrxnTypes trxnType) throws Exception {

    	validateAmount(amount);
    	
    	String format = "dd-MM-yyyy";   	
    	DateTime trnxDate = validateDateString(trxnDate, format);
    	validateSavingsDate(trnxDate);
    	DateTime receiptDateTime = null;
    	if (receiptDate != null && !receiptDate.isEmpty()){
    	 	receiptDateTime = validateDateString(receiptDate, format);
    	 	validateSavingsDate(receiptDateTime);
    	} else {
    		receiptDateTime = new DateTime(trnxDate);
    	}
    	
        SavingsBO savingsBO = savingsDao.findBySystemId(globalAccountNum);

        validateAccountState(savingsBO);

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integer accountId = savingsBO.getAccountId();
        
        DateTime today = new DateTime();
        String receiptIdString;
        if ( receiptId == null ){
        	receiptIdString = "";
        } else {
        	receiptIdString = receiptId.toString();
        }

        CustomerBO client = savingsBO.getCustomer();
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(client.getCustomerId());

        Money balanceBeforePayment = savingsBO.getSavingsBalance();
        if (trxnType.equals(TrxnTypes.savings_deposit)) {
        	validateSavingsPaymentTypeId(paymentTypeId, accountService.getSavingsPaymentTypes());
            SavingsDepositDto savingsDeposit = new SavingsDepositDto(accountId.longValue(), savingsBO.getCustomer().getCustomerId().longValue(),
                    trnxDate.toLocalDate(), amount.doubleValue(), paymentTypeId.intValue(), receiptIdString, receiptDateTime.toLocalDate(),
                    Locale.UK);
            this.savingsServiceFacade.deposit(savingsDeposit);
        }
        else {
        	validateSavingsPaymentTypeId(paymentTypeId, accountService.getSavingsWithdrawalTypes());
            SavingsWithdrawalDto savingsWithdrawal = new SavingsWithdrawalDto(accountId.longValue(), savingsBO.getCustomer().getCustomerId().longValue(),
                    trnxDate.toLocalDate(), amount.doubleValue(), paymentTypeId.intValue(), receiptIdString, receiptDateTime.toLocalDate(),
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

    @RequestMapping(value = "/account/savings/create", method=RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createSavingsAccount(@RequestParam String globalCustomerNum,
                                            @RequestParam(required=false) String amount,
                                            @RequestParam Integer productId) throws Throwable {
        
        String recommendedAmount = null != amount ? amount : "0";
        
        CustomerBO customer = validateGlobalCustNum(globalCustomerNum);
        SavingsOfferingBO product = validateProductId(productId);
        
        Integer customerId = customer.getCustomerId();
        AccountState accountState = AccountState.SAVINGS_PENDING_APPROVAL;
        
        SavingsAccountCreationDto savingsAccountCreation = new SavingsAccountCreationDto(
                productId, customerId, accountState.getValue(), recommendedAmount);
        Long savings = savingsServiceFacade.createSavingsAccount(savingsAccountCreation);
        SavingsAccountDetailDto details = savingsServiceFacade.retrieveSavingsAccountDetails(savings);
        
        
        Map<String,String> map = new HashMap<String, String>();
        map.put("customerName", customer.getDisplayName());
        map.put("productName", product.getPrdOfferingName());
        map.put("interesRate", details.getProductDetails().getInterestRate().toString());
        map.put("interestRatePeriod", details.getProductDetails().getInterestCalculationFrequencyPeriod().toString());
        map.put("recommendedAmount", recommendedAmount);
        return map;
    }
    
    private void validateAmount(BigDecimal amount) throws ParamValidationException {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamValidationException(ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
    }

    private void validateNote(String note) throws ParamValidationException {
        if (note == null || note.isEmpty()){
            throw new ParamValidationException(ErrorMessage.INVALID_NOTE);
        }
    }
    
    private SavingsOfferingBO validateProductId(Integer productId) throws ParamValidationException {
        SavingsOfferingBO product = savingsProductDao.findById(productId);
        if (null == product) {
            throw new ParamValidationException(ErrorMessage.INVALID_PRODUCT_ID);
        }
        else {
            return product;
        }
    }
    
    private CustomerBO validateGlobalCustNum(String globalCustomerNum) throws ParamValidationException {
        CustomerBO client = customerDao.findCustomerBySystemId(globalCustomerNum);
        if (null == client) {
            throw new ParamValidationException(ErrorMessage.INVALID_GLOABAL_CUSTOMER_NUM);
        }
        else {
            return client;
        }
    }

    public void validateAccountState(SavingsBO savingsBO) throws ParamValidationException {
        if (!savingsBO.getState().isActiveSavingsAccountState() ){
            throw new ParamValidationException(ErrorMessage.NOT_ACTIVE_ACCOUNT);
        }
    }
    
    public void validateSavingsPaymentTypeId(Short paymentTypeId, List<PaymentTypeDto> savingsPaymentTypes) throws ParamValidationException{
    	boolean valid = false;
    	for ( PaymentTypeDto paymentType : savingsPaymentTypes){
    		if ( paymentType.getValue().equals(paymentTypeId) ){
    			valid = true;
    		}
    	}
    	if ( !valid) {
    		throw new ParamValidationException(ErrorMessage.INVALID_PAYMENT_TYPE_ID);
    	}
    }
    
    public DateTime validateDateString(String dateString, String format) throws ParamValidationException {
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    	try {
    		return new DateTime(dateFormat.parse(dateString));
    	} catch (ParseException e){
    		throw new ParamValidationException(ErrorMessage.INVALID_DATE_STRING + "in format " + format);
    	}
    }
    
    public void validateSavingsDate(DateTime date) throws ParamValidationException {
    	DateTime today = new DateTime();
    	if (date.isAfter(today)){
    		throw new ParamValidationException(ErrorMessage.FUTURE_DATE);
    	}
    }

}

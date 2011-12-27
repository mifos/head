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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanRepaymentScheduleItemDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoanAccountRESTController {

    private static String format = "dd-MM-yyyy";

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoanAccountServiceFacade loanAccountServiceFacade;

    @Autowired
    private AccountServiceFacade accountServiceFacade;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private PersonnelDao personnelDao;

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/repay", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> repay(@PathVariable String globalAccountNum,
                              @RequestParam BigDecimal amount,
                              @RequestParam(required=false) String paymentDate,
                              @RequestParam(required=false) Short receiptId,
                              @RequestParam(required=false) String receiptDate,
                              @RequestParam(required=false) Short paymentModeId) throws Exception {

        validateAmount(amount);

        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);

        validateLoanAccountState(loan);

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserReferenceDto userDto = new UserReferenceDto((short) user.getUserId());

        Money outstandingBeforePayment = loan.getLoanSummary().getOutstandingBalance();

        AccountReferenceDto accountDto = new AccountReferenceDto(loan.getAccountId());

        DateTime today = new DateTime();

        DateTime paymentDateTime = today;
        if (paymentDate != null && !paymentDate.isEmpty()) {
            paymentDateTime = validateDateString(paymentDate, format);
        }

        String receiptIdString = null;
        if (receiptId != null) {
            receiptIdString = receiptId.toString();
        }

        LocalDate receiptLocalDate = null;
        if (receiptDate != null && !receiptDate.isEmpty()) {
            receiptLocalDate = validateDateString(receiptDate, format).toLocalDate();
        }

        PaymentTypeDto paymentType = accountService.getLoanPaymentTypes().get(0);
        if (paymentModeId != null) {
            for (PaymentTypeDto loanPaymentType : accountService.getLoanPaymentTypes()) {
                if (loanPaymentType.getValue().equals(paymentModeId)) {
                    paymentType = loanPaymentType;
                    break;
                }
            }
        }

        CustomerBO client = loan.getCustomer();
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(client.getCustomerId());
        AccountPaymentParametersDto payment = new AccountPaymentParametersDto(userDto, accountDto, amount, paymentDateTime.toLocalDate(),
                paymentType, globalAccountNum, receiptLocalDate, receiptIdString, customer);

        accountService.makePayment(payment);

        Map<String, String> map = new HashMap<String, String>();
        map.put("status", "success");
        map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("loanDisplayName", loan.getLoanOffering().getPrdOfferingName());
        map.put("paymentDate", today.toLocalDate().toString());
        map.put("paymentTime", today.toLocalTime().toString());
        map.put("paymentAmount", loan.getLastPmnt().getAmount().toString());
        map.put("paymentMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("outstandingBeforePayment", outstandingBeforePayment.toString());
        map.put("outstandingAfterPayment", loan.getLoanSummary().getOutstandingBalance().toString());
        return map;
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/fullrepay", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> fullRepay(@PathVariable String globalAccountNum,
                                  @RequestParam(required = false) String paymentDate,
                                  @RequestParam(required = false) Short receiptId,
                                  @RequestParam(required = false) String receiptDate,
                                  @RequestParam(required = false) Short paymentModeId,
                                  @RequestParam Boolean waiveInterest) throws Exception {


    	LoanBO loan = this.loanDao.findByGlobalAccountNum(globalAccountNum);
    	validateLoanAccountState(loan);

    	MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        RepayLoanDto repayLoanDto = this.loanAccountServiceFacade.retrieveLoanRepaymentDetails(globalAccountNum);

        DateTime today = new DateTime();

        Date paymentDateTime = new Date(today.toDate().getTime());
        if (paymentDate != null && !paymentDate.isEmpty()) {
            paymentDateTime = new Date(validateDateString(paymentDate, format).toDate().getTime());
        }

        String receiptIdString = null;
        if (receiptId != null) {
            receiptIdString = receiptId.toString();
        }

        Date receiptDateTime = null;
        if (receiptDate != null && !receiptDate.isEmpty()) {
            receiptDateTime = new Date(validateDateString(receiptDate, format).toDate().getTime());
        }

        String paymentTypeId = "1";
        if (paymentModeId != null) {
            paymentTypeId = paymentModeId.toString();
        }

        BigDecimal totalRepaymentAmount = (new Money(loan.getCurrency(), repayLoanDto.getEarlyRepaymentMoney())).getAmount();
        BigDecimal waivedAmount = (new Money(loan.getCurrency(), repayLoanDto.getWaivedRepaymentMoney())).getAmount();

        RepayLoanInfoDto repayLoanInfoDto = new RepayLoanInfoDto(globalAccountNum,
                Long.toString(totalRepaymentAmount.longValue()), receiptIdString,
                receiptDateTime, paymentTypeId, (short) user.getUserId(),
                waiveInterest,
                paymentDateTime, totalRepaymentAmount, waivedAmount);

        Money outstandingBeforePayment = loan.getLoanSummary().getOutstandingBalance();

        this.loanAccountServiceFacade.makeEarlyRepaymentWithCommit(repayLoanInfoDto);

        CustomerBO client = loan.getCustomer();

        Map<String, String> map = new HashMap<String, String>();
    	map.put("status", "success");
        map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("loanDisplayName", loan.getLoanOffering().getPrdOfferingName());
        map.put("paymentDate", today.toLocalDate().toString());
        map.put("paymentTime", today.toLocalTime().toString());
        map.put("paymentAmount", loan.getLastPmnt().getAmount().toString());
        map.put("paymentMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("outstandingBeforePayment", outstandingBeforePayment.toString());
        map.put("outstandingAfterPayment", loan.getLoanSummary().getOutstandingBalance().toString());

    	return map;
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/disburse", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> disburseLoan(@PathVariable String globalAccountNum, 
    								 @RequestParam String disbursalDate,
    								 @RequestParam(required=false) Short receiptId,
    								 @RequestParam(required=false) String receiptDate,
    								 @RequestParam Short disbursePaymentTypeId,
    								 @RequestParam(required=false) Short paymentModeOfPayment) throws Exception {
    	String format = "dd-MM-yyyy";   	
    	DateTime trnxDate = validateDateString(disbursalDate, format);
    	validateDisbursementDate(trnxDate);
    	DateTime receiptDateTime = null;
    	if (receiptDate != null && !receiptDate.isEmpty()){
    	 	receiptDateTime = validateDateString(receiptDate, format);
    	 	validateDisbursementDate(receiptDateTime);
    	}
    	validateDisbursementPaymentTypeId(disbursePaymentTypeId, accountService.getLoanDisbursementTypes());
    	
    	MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
	
    	String comment = "";
    	Short paymentTypeId = Short.valueOf(disbursePaymentTypeId);

       	Money outstandingBeforeDisbursement = loan.getLoanSummary().getOutstandingBalance();
       	
       	CustomerDto customerDto = null;
       	PaymentTypeDto paymentType = null;
       	AccountPaymentParametersDto loanDisbursement;
       	if (receiptId == null || receiptDateTime == null){
        	loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto((short)user.getUserId()),
        			new AccountReferenceDto(loan.getAccountId()), loan.getLoanAmount().getAmount(), trnxDate.toLocalDate(), paymentType, comment);
       	} else {
        	loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto((short)user.getUserId()),
        			new AccountReferenceDto(loan.getAccountId()), loan.getLoanAmount().getAmount(), trnxDate.toLocalDate(), paymentType, comment,
        			receiptDateTime.toLocalDate(), receiptId.toString(), customerDto);	
       	}

    	this.loanAccountServiceFacade.disburseLoan(loanDisbursement, paymentTypeId);

    	CustomerBO client = loan.getCustomer();

    	Map<String, String> map = new HashMap<String, String>();
    	map.put("status", "success");
    	map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("loanDisplayName", loan.getLoanOffering().getPrdOfferingName());
        map.put("disbursementDate", trnxDate.toLocalDate().toString());
        map.put("disbursementTime", new DateTime().toLocalTime().toString());
        map.put("disbursementAmount", loan.getLastPmnt().getAmount().toString());
        map.put("disbursementMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("outstandingBeforeDisbursement", outstandingBeforeDisbursement.toString());
        map.put("outstandingAfterDisbursement", loan.getLoanSummary().getOutstandingBalance().toString());

    	return map;
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/adjustment", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> applyAdjustment(@PathVariable String globalAccountNum,
                                        @RequestParam String note) throws Exception {

        validateNote(note);

		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
    	CustomerBO client = loan.getCustomer();

		String adjustmentAmount = loan.getLastPmnt().getAmount().toString();
		String outstandingBeforeAdjustment = loan.getLoanSummary().getOutstandingBalance().toString();

		try{
			accountServiceFacade.applyAdjustment(globalAccountNum, note, (short)user.getUserId());
		} catch (MifosRuntimeException e){
        	String error = e.getCause().getMessage();
        	throw new MifosRuntimeException(error);
		}

		DateTime today = new DateTime();

        Map<String, String> map = new HashMap<String, String>();
		map.put("status", "success");
		map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("loanDisplayName", loan.getLoanOffering().getPrdOfferingName());
        map.put("adjustmentDate", today.toLocalDate().toString());
        map.put("adjustmentTime", today.toLocalTime().toString());
        map.put("adjustmentAmount", adjustmentAmount);
        map.put("adjustmentMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("outstandingBeforeAdjustment", outstandingBeforeAdjustment);
        map.put("outstandingAfterAdjustment", loan.getLoanSummary().getOutstandingBalance().toString());
        map.put("note", note);

    	return map;
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/charge", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> applyCharge(@PathVariable String globalAccountNum,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam Short feeId) throws Exception {

        validateAmount(amount);

        List<String> applicableFees = new ArrayList<String>();
        for (Map<String, String> feeMap : this.getApplicableFees(globalAccountNum).values()) {
            applicableFees.add(feeMap.get("feeId"));
        }
        validateFeeId(feeId, applicableFees);
        
        Map<String, String> map = new HashMap<String, String>();
		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
		Integer accountId = loan.getAccountId();
		CustomerBO client = loan.getCustomer();

		String outstandingBeforeCharge = loan.getLoanSummary().getOutstandingBalance().toString();

    	this.accountServiceFacade.applyCharge(accountId, feeId, amount.doubleValue());

    	DateTime today = new DateTime();

		map.put("status", "success");
		map.put("clientName", client.getDisplayName());
        map.put("clientNumber", client.getGlobalCustNum());
        map.put("loanDisplayName", loan.getLoanOffering().getPrdOfferingName());
        map.put("chargeDate", today.toLocalDate().toString());
        map.put("chargeTime", today.toLocalTime().toString());
        map.put("chargeAmount", Double.valueOf(amount.doubleValue()).toString());
        map.put("chargeMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("outstandingBeforeCharge", outstandingBeforeCharge);
        map.put("outstandingAfterCharge", loan.getLoanSummary().getOutstandingBalance().toString());

    	return map;
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/fees", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Map<String, String>> getApplicableFees(@PathVariable String globalAccountNum) throws Exception {
		LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
		Integer accountId = loan.getAccountId();
		List<ApplicableCharge> applicableCharges = this.accountServiceFacade.getApplicableFees(accountId);
		
    	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
    	
    	for (ApplicableCharge applicableCharge : applicableCharges ){
    		Map<String, String> feeMap = new HashMap<String, String>();
            feeMap.put("feeId", applicableCharge.getFeeId());
            feeMap.put("amountOrRate", applicableCharge.getAmountOrRate());
            feeMap.put("formula", applicableCharge.getFormula());
            feeMap.put("periodicity", applicableCharge.getPeriodicity());
            feeMap.put("paymentType", applicableCharge.getPaymentType());
            feeMap.put("isRateType", applicableCharge.getIsRateType());
    		map.put(applicableCharge.getFeeName(), feeMap);
    	}
  
    	return map;
    }
    
    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/interestWaivable", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> isLoanInterestWaivable(@PathVariable String globalAccountNum) throws Exception {
		LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
		boolean interestWaivable = loan.getLoanOffering().isInterestWaived();
    	
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("isInterestWaivable", Boolean.toString(interestWaivable));
		map.put("defaultValue", "false");
		
    	return map;
    }
    
    @RequestMapping(value = "/account/loan/num-{globalAccountNum}", method = RequestMethod.GET)
    public @ResponseBody
    LoanInformationDto getLoanByNumber(@PathVariable String globalAccountNum) throws Exception {
        return loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/installment", method = RequestMethod.GET)
    public @ResponseBody
    LoanInstallmentDetailsDto getLoanInstallmentByNumber(@PathVariable String globalAccountNum) throws Exception {
        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
        return loanAccountServiceFacade.retrieveInstallmentDetails(loan.getAccountId());
    }

    @RequestMapping(value = "/account/loan/num-{globalAccountNum}/schedule", method = RequestMethod.GET)
    public @ResponseBody
    List<LoanRepaymentScheduleItemDto> getLoanRepaymentScheduleByNumber(@PathVariable String globalAccountNum) throws Exception {
        return loanAccountServiceFacade.retrieveLoanRepaymentSchedule(globalAccountNum);
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

    public void validateLoanAccountState(LoanBO loan) throws ParamValidationException {
        if (!loan.getState().isActiveLoanAccountState()){
            throw new ParamValidationException(ErrorMessage.NOT_ACTIVE_ACCOUNT);
        }
    }
    
    public static void validateFeeId(Short feeId, List<String> applicableFees) throws ParamValidationException{
       if (!applicableFees.contains(Short.toString(feeId))){
               throw new ParamValidationException(ErrorMessage.INVALID_FEE_ID);
       }
    }
    
    public void validateDisbursementPaymentTypeId(Short paymentTypeId, List<PaymentTypeDto> disbursementPaymentTypes) throws ParamValidationException{
    	boolean valid = false;
    	for ( PaymentTypeDto paymentType : disbursementPaymentTypes){
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
    
    public void validateDisbursementDate(DateTime date) throws ParamValidationException {
    	DateTime today = new DateTime();
    	if (date.isAfter(today)){
    		throw new ParamValidationException(ErrorMessage.FUTURE_DATE);
    	}
    }
}

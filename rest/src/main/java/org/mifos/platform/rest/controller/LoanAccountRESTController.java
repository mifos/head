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

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.api.AccountService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.application.servicefacade.CreationAccountPenaltyDto;
import org.mifos.application.servicefacade.CreationFeeDto;
import org.mifos.application.servicefacade.CreationGLIMAccountsDto;
import org.mifos.application.servicefacade.CreationLoanAccountDto;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGlimLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
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
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationAccountPenaltyDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationFeeDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationGLIMAccountsDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.CreationLoanAccountDtoMixIn;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    Map<String, String> repay(@PathVariable String globalAccountNum, @RequestParam BigDecimal amount,
            @RequestParam(required = false) String paymentDate, @RequestParam(required = false) Short receiptId,
            @RequestParam(required = false) String receiptDate, @RequestParam Short paymentModeId) throws Exception {

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

        PaymentTypeDto paymentType = new PaymentTypeDto(paymentModeId, "");
        validatePaymentTypeId(paymentType, accountService.getLoanPaymentTypes());

        CustomerBO client = loan.getCustomer();
        CustomerDto customer = new CustomerDto();
        customer.setCustomerId(client.getCustomerId());
        AccountPaymentParametersDto payment = new AccountPaymentParametersDto(userDto, accountDto, amount,
                paymentDateTime.toLocalDate(), paymentType, globalAccountNum, receiptLocalDate, receiptIdString,
                customer);

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
            @RequestParam(required = false) String paymentDate, @RequestParam(required = false) Short receiptId,
            @RequestParam(required = false) String receiptDate, @RequestParam Short paymentModeId,
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
        validateDisbursementPaymentTypeId(paymentModeId, accountService.getLoanPaymentTypes());
        BigDecimal totalRepaymentAmount = (new Money(loan.getCurrency(), repayLoanDto.getEarlyRepaymentMoney()))
                .getAmount();
        BigDecimal waivedAmount = (new Money(loan.getCurrency(), repayLoanDto.getWaivedRepaymentMoney())).getAmount();
        BigDecimal earlyRepayAmount = totalRepaymentAmount;
        if (Boolean.TRUE.equals(waiveInterest)) {
            earlyRepayAmount = waivedAmount;
        }
        RepayLoanInfoDto repayLoanInfoDto = new RepayLoanInfoDto(globalAccountNum, Double.toString(earlyRepayAmount
                .doubleValue()), receiptIdString, receiptDateTime, paymentTypeId, (short) user.getUserId(),
                waiveInterest.booleanValue(), paymentDateTime, totalRepaymentAmount, waivedAmount);

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
    Map<String, String> disburseLoan(@PathVariable String globalAccountNum, @RequestParam String disbursalDate,
            @RequestParam(required = false) Short receiptId, @RequestParam(required = false) String receiptDate,
            @RequestParam Short disbursePaymentTypeId, @RequestParam(required = false) Short paymentModeOfPayment)
            throws Exception {
        String format = "dd-MM-yyyy";
        DateTime trnxDate = validateDateString(disbursalDate, format);
        validateDisbursementDate(trnxDate);
        DateTime receiptDateTime = null;
        if (receiptDate != null && !receiptDate.isEmpty()) {
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
        if (receiptId == null || receiptDateTime == null) {
            loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto((short) user.getUserId()),
                    new AccountReferenceDto(loan.getAccountId()), loan.getLoanAmount().getAmount(),
                    trnxDate.toLocalDate(), paymentType, comment);
        } else {
            loanDisbursement = new AccountPaymentParametersDto(new UserReferenceDto((short) user.getUserId()),
                    new AccountReferenceDto(loan.getAccountId()), loan.getLoanAmount().getAmount(),
                    trnxDate.toLocalDate(), paymentType, comment, receiptDateTime.toLocalDate(), receiptId.toString(),
                    customerDto);
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
    Map<String, String> applyAdjustment(@PathVariable String globalAccountNum, @RequestParam String note)
            throws Exception {

        validateNote(note);

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoanBO loan = loanDao.findByGlobalAccountNum(globalAccountNum);
        CustomerBO client = loan.getCustomer();

        String adjustmentAmount = loan.getLastPmnt().getAmount().toString();
        String outstandingBeforeAdjustment = loan.getLoanSummary().getOutstandingBalance().toString();

        try {
            accountServiceFacade.applyAdjustment(globalAccountNum, note, (short) user.getUserId());
        } catch (MifosRuntimeException e) {
            String error = e.getCause().getMessage();
            throw new MifosRuntimeException(error);
        }

        DateTime today = new DateTime();

        loan = loanDao.findByGlobalAccountNum(globalAccountNum);
        client = loan.getCustomer();

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
    Map<String, String> applyCharge(@PathVariable String globalAccountNum, @RequestParam BigDecimal amount,
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

        this.accountServiceFacade.applyCharge(accountId, feeId, amount.doubleValue(), false);

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

        for (ApplicableCharge applicableCharge : applicableCharges) {
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
        RepayLoanDto repayLoanDto = this.loanAccountServiceFacade.retrieveLoanRepaymentDetails(globalAccountNum);
        Map<String, String> map = new HashMap<String, String>();

        map.put("isInterestWaivable", Boolean.toString(repayLoanDto.shouldWaiverInterest()));
        map.put("defaultValue", Boolean.toString(repayLoanDto.shouldWaiverInterest()));

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
    List<LoanRepaymentScheduleItemDto> getLoanRepaymentScheduleByNumber(@PathVariable String globalAccountNum)
            throws Exception {
        return loanAccountServiceFacade.retrieveLoanRepaymentSchedule(globalAccountNum, new DateTime().toDate());
    }

    @RequestMapping(value = "/account/loan/create", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> createLoanAccount(@RequestBody String request) throws Throwable {
        ObjectMapper om = createLoanAccountMapping();
        Map<String, String> map = new HashMap<String, String>();
        CreationLoanAccountDto creationDetails = null;

        try {
            creationDetails = om.readValue(request, CreationLoanAccountDto.class);
        } catch (JsonMappingException e) {
            e.getCause();
        }

        loanValidator(creationDetails);
        
        List<QuestionGroupDetail> questionGroups = new ArrayList<QuestionGroupDetail>();
        LoanCreationResultDto loanResult = null;
        LoanBO loanInfo = null;
        CreateLoanAccount loanAccount = createLoan(creationDetails);
        
        if (creationDetails.getGlim()) {
            List<GroupMemberAccountDto> memberAccounts = creationDetails.glimsAsGroupMemberAccountDto(creationDetails.getGlimAccounts());
            CreateGlimLoanAccount createGroupLoanAccount = new CreateGlimLoanAccount(memberAccounts, creationDetails.getLoanAmount(), loanAccount);
            loanResult = loanAccountServiceFacade.createGroupLoanWithIndividualMonitoring(createGroupLoanAccount, questionGroups, null);
            List<LoanBO> individuals = loanDao.findIndividualLoans(loanResult.getAccountId());
            for (int i = 0; i < individuals.size(); i++) {
                map.put("individualCustomer[" + i + "]", individuals.get(i).getCustomer().getDisplayName());
                map.put("individualAccount[" + i + "]", individuals.get(i).getGlobalAccountNum());
                map.put("individualAmmount[" + i + "]", individuals.get(i).getLoanAmount().toString());
            }
        }
        else {
            loanResult = loanAccountServiceFacade.createLoan(loanAccount, questionGroups, null);
        } 
        loanInfo = loanDao.findByGlobalAccountNum(loanResult.getGlobalAccountNum());
        
        map.put("status", "success");
        map.put("accountNum", loanInfo.getGlobalAccountNum());
        map.put("GLIM", creationDetails.getGlim().toString());
        map.put("customer", loanInfo.getCustomer().getDisplayName());
        map.put("loanAmmount", loanInfo.getLoanAmount().toString());
        map.put("noOfInstallments", loanInfo.getNoOfInstallments().toString());
        map.put("externalId", loanInfo.getExternalId());
        return map;
    }

    private ObjectMapper createLoanAccountMapping() {
        ObjectMapper om = new ObjectMapper();
        om.getJsonFactory().configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        om.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.getDeserializationConfig().addMixInAnnotations(CreationLoanAccountDto.class,
                CreationLoanAccountDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationFeeDto.class, CreationFeeDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationAccountPenaltyDto.class,
                CreationAccountPenaltyDtoMixIn.class);
        om.getDeserializationConfig().addMixInAnnotations(CreationGLIMAccountsDto.class, CreationGLIMAccountsDtoMixIn.class);
        return om;
    }

    private CreateLoanAccount createLoan(CreationLoanAccountDto creationDetails) {
        return new CreateLoanAccount(creationDetails.getCustomerId(), creationDetails.getProductId(),
                    creationDetails.getAccountState(), creationDetails.getLoanAmount(),
                    creationDetails.getMinAllowedLoanAmount(), creationDetails.getMaxAllowedLoanAmount(),
                    creationDetails.getInterestRate(), creationDetails.getDisbursementDate(),
                    creationDetails.getDisbursalPaymentTypeId(), creationDetails.getNumberOfInstallments(),
                    creationDetails.getMinNumOfInstallments(), creationDetails.getMaxNumOfInstallments(),
                    creationDetails.getGraceDuration(), creationDetails.getSourceOfFundId(),
                    creationDetails.getLoanPurposeId(), creationDetails.getCollateralTypeId(),
                    creationDetails.getCollateralNotes(), creationDetails.getExternalId(), false,
                    creationDetails.getRecurringSchedule(), creationDetails.feeAsAccountFeeDto(creationDetails
                            .getAccountFees()), creationDetails.penaltiesAsAccountPenaltiesDto(creationDetails
                            .getAccountPenalties()));
    }
    
    private void loanValidator(CreationLoanAccountDto creationDetails) throws ParamValidationException {
        if (creationDetails.getGlim()) {
            List<CreationGLIMAccountsDto> glims = creationDetails.getGlimAccounts();
            validateGLIMAmount(creationDetails.getLoanAmount(), glims);
        }
        validateAmount(creationDetails.getLoanAmount());
    }
    
    private void validateGLIMAmount(BigDecimal amount,List<CreationGLIMAccountsDto> glims) throws ParamValidationException {
        Double sum = 0.0;
        for(CreationGLIMAccountsDto accountsDto : glims) {
            sum += accountsDto.getLoanAmount().doubleValue();
        }
        if(amount.doubleValue() != sum) {
            throw new ParamValidationException(ErrorMessage.INVALID_GLIM_AMOUNT);
        }
    }

    private void validateAmount(BigDecimal amount) throws ParamValidationException {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamValidationException(ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
    }

    private void validateNote(String note) throws ParamValidationException {
        if (note == null || note.isEmpty()) {
            throw new ParamValidationException(ErrorMessage.INVALID_NOTE);
        }
    }

    public void validateLoanAccountState(LoanBO loan) throws ParamValidationException {
        if (!loan.getState().isActiveLoanAccountState()) {
            throw new ParamValidationException(ErrorMessage.NOT_ACTIVE_ACCOUNT);
        }
    }

    public static void validateFeeId(Short feeId, List<String> applicableFees) throws ParamValidationException {
        if (!applicableFees.contains(Short.toString(feeId))) {
            throw new ParamValidationException(ErrorMessage.INVALID_FEE_ID);
        }
    }

    public void validateDisbursementPaymentTypeId(Short paymentTypeId, List<PaymentTypeDto> disbursementPaymentTypes)
            throws ParamValidationException {
        boolean valid = false;
        for (PaymentTypeDto paymentType : disbursementPaymentTypes) {
            if (paymentType.getValue().equals(paymentTypeId)) {
                valid = true;
            }
        }
        if (!valid) {
            throw new ParamValidationException(ErrorMessage.INVALID_PAYMENT_TYPE_ID);
        }
    }

    public void validatePaymentTypeId(PaymentTypeDto paymentModeId, List<PaymentTypeDto> paymentTypes)
            throws ParamValidationException {
        boolean valid = false;
        for (PaymentTypeDto paymentType : paymentTypes) {
            if (paymentType.getValue().equals(paymentModeId.getValue())) {
                valid = true;
            }
        }
        if (!valid) {
            throw new ParamValidationException(ErrorMessage.INVALID_PAYMENT_TYPE_ID);
        }
    }

    public DateTime validateDateString(String dateString, String format) throws ParamValidationException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return new DateTime(dateFormat.parse(dateString));
        } catch (ParseException e) {
            throw new ParamValidationException(ErrorMessage.INVALID_DATE_STRING + "in format " + format);
        }
    }

    public void validateDisbursementDate(DateTime date) throws ParamValidationException {
        DateTime today = new DateTime();
        if (date.isAfter(today)) {
            throw new ParamValidationException(ErrorMessage.FUTURE_DATE);
        }
    }
}

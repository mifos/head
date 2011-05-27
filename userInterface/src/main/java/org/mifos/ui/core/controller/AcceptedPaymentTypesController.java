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

package org.mifos.ui.core.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.domain.AcceptedPaymentTypeDto;
import org.mifos.dto.screen.PaymentTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@SuppressWarnings("PMD")
@Controller
@RequestMapping("/defineAcceptedPaymentTypes.ftl")
@SessionAttributes("formBean")
public class AcceptedPaymentTypesController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected AcceptedPaymentTypesController() {
        // default contructor for spring autowiring
    }

    public AcceptedPaymentTypesController(final AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("OrganizationPreferences.defineAcceptedPaymentTypes", "defineAcceptedPaymentTypes.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("acceptedPaymentTypesBean")
    public AcceptedPaymentTypesBean showPopulatedForm() {
        AcceptedPaymentTypeDto acceptedPaymentTypeDto = adminServiceFacade.retrieveAcceptedPaymentTypes();

        AcceptedPaymentTypesBean paymentTypes = populateAcceptedPaymentTypesBean(acceptedPaymentTypeDto);
        return paymentTypes;
    }

    private AcceptedPaymentTypesBean populateAcceptedPaymentTypesBean(AcceptedPaymentTypeDto acceptedPaymentTypeDto) {

        Map<String, String> nonAcceptedFees = new LinkedHashMap<String, String>();
        Map<String, String> acceptedFees = new LinkedHashMap<String, String>();

        List<PaymentTypeDto> outFees = acceptedPaymentTypeDto.getOutFeeList();
        for (PaymentTypeDto paymentType : outFees) {
            acceptedFees.put(paymentType.getId().toString(), paymentType.getName());
        }

        List<PaymentTypeDto> inFees = acceptedPaymentTypeDto.getInFeeList();
        for (PaymentTypeDto paymentType : inFees) {
            nonAcceptedFees.put(paymentType.getId().toString(), paymentType.getName());
        }

        Map<String, String> nonAcceptedLoanDisbursements = new LinkedHashMap<String, String>();
        Map<String, String> acceptedLoanDisbursements = new LinkedHashMap<String, String>();

        List<PaymentTypeDto> outDisbursements = acceptedPaymentTypeDto.getOutDisbursementList();
        for (PaymentTypeDto paymentType : outDisbursements) {
            acceptedLoanDisbursements.put(paymentType.getId().toString(), paymentType.getName());
        }

        List<PaymentTypeDto> inDibursements = acceptedPaymentTypeDto.getInDisbursementList();
        for (PaymentTypeDto paymentType : inDibursements) {
            nonAcceptedLoanDisbursements.put(paymentType.getId().toString(), paymentType.getName());
        }

        Map<String, String> nonAcceptedLoanRepayments = new LinkedHashMap<String, String>();
        Map<String, String> acceptedLoanRepayments = new LinkedHashMap<String, String>();

        List<PaymentTypeDto> outRepayments = acceptedPaymentTypeDto.getOutRepaymentList();
        for (PaymentTypeDto paymentType : outRepayments) {
            acceptedLoanRepayments.put(paymentType.getId().toString(), paymentType.getName());
        }

        List<PaymentTypeDto> inRepayments = acceptedPaymentTypeDto.getInRepaymentList();
        for (PaymentTypeDto paymentType : inRepayments) {
            nonAcceptedLoanRepayments.put(paymentType.getId().toString(), paymentType.getName());
        }

        Map<String, String> nonAcceptedSavingWithdrawals = new LinkedHashMap<String, String>();
        Map<String, String> acceptedSavingWithdrawals = new LinkedHashMap<String, String>();

        List<PaymentTypeDto> outWithdrawals = acceptedPaymentTypeDto.getOutWithdrawalList();
        for (PaymentTypeDto paymentType : outWithdrawals) {
            acceptedSavingWithdrawals.put(paymentType.getId().toString(), paymentType.getName());
        }

        List<PaymentTypeDto> inWithdrawals = acceptedPaymentTypeDto.getInWithdrawalList();
        for (PaymentTypeDto paymentType : inWithdrawals) {
            nonAcceptedSavingWithdrawals.put(paymentType.getId().toString(), paymentType.getName());
        }

        Map<String, String> nonAcceptedSavingDeposits = new LinkedHashMap<String, String>();
        Map<String, String> acceptedSavingDeposits = new LinkedHashMap<String, String>();

        List<PaymentTypeDto> outDeposits = acceptedPaymentTypeDto.getOutDepositList();
        for (PaymentTypeDto paymentType : outDeposits) {
            acceptedSavingDeposits.put(paymentType.getId().toString(), paymentType.getName());
        }

        List<PaymentTypeDto> inDeposits = acceptedPaymentTypeDto.getInDepositList();
        for (PaymentTypeDto paymentType : inDeposits) {
            nonAcceptedSavingDeposits.put(paymentType.getId().toString(), paymentType.getName());
        }

        AcceptedPaymentTypesBean paymentTypes = new AcceptedPaymentTypesBean();
        paymentTypes.setNonAcceptedFeePaymentTypes(nonAcceptedFees);
        paymentTypes.setAcceptedFeePaymentTypes(acceptedFees);

        paymentTypes.setNonAcceptedLoanDisbursementPaymentTypes(nonAcceptedLoanDisbursements);
        paymentTypes.setAcceptedLoanDisbursementPaymentTypes(acceptedLoanDisbursements);

        paymentTypes.setNonAcceptedLoanRepaymentPaymentTypes(nonAcceptedLoanRepayments);
        paymentTypes.setAcceptedLoanRepaymentPaymentTypes(acceptedLoanRepayments);

        paymentTypes.setNonAcceptedSavingWithdrawalPaymentTypes(nonAcceptedSavingWithdrawals);
        paymentTypes.setAcceptedSavingWithdrawalPaymentTypes(acceptedSavingWithdrawals);

        paymentTypes.setNonAcceptedSavingDepositsPaymentTypes(nonAcceptedSavingDeposits);
        paymentTypes.setAcceptedSavingDepositsPaymentTypes(acceptedSavingDeposits);

        return paymentTypes;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("acceptedPaymentTypesBean") AcceptedPaymentTypesBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {
        String viewName = REDIRECT_TO_ADMIN_SCREEN;

        if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            status.setComplete();
        } else if (result.hasErrors()) {
            viewName = "defineAcceptedPaymentTypes";
        } else {
            this.adminServiceFacade.updateAcceptedPaymentTypes(formBean.getChosenAcceptedFees(), formBean.getChosenAcceptedLoanDisbursements(), formBean.getChosenAcceptedLoanRepayments(), formBean.getChosenAcceptedSavingDeposits(), formBean.getChosenAcceptedSavingWithdrawals());
            status.setComplete();
        }
        return viewName;
    }
}
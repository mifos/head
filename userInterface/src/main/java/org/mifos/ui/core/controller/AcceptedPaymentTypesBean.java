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
import java.util.Map;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="..")
public class AcceptedPaymentTypesBean {

    private Map<String, String> acceptedFeePaymentTypes = new LinkedHashMap<String, String>();
    private Map<String, String> nonAcceptedFeePaymentTypes = new LinkedHashMap<String, String>();
    private String[] chosenAcceptedFees;
    private String[] chosenNonAcceptedFees;

    private Map<String, String> acceptedLoanDisbursementPaymentTypes = new LinkedHashMap<String, String>();
    private Map<String, String> nonAcceptedLoanDisbursementPaymentTypes = new LinkedHashMap<String, String>();
    private String[] chosenAcceptedLoanDisbursements;
    private String[] chosenNonAcceptedLoanDisbursements;

    private Map<String, String> acceptedLoanRepaymentPaymentTypes = new LinkedHashMap<String, String>();
    private Map<String, String> nonAcceptedLoanRepaymentPaymentTypes = new LinkedHashMap<String, String>();
    private String[] chosenAcceptedLoanRepayments;
    private String[] chosenNonAcceptedLoanRepayments;

    private Map<String, String> acceptedSavingWithdrawalPaymentTypes = new LinkedHashMap<String, String>();
    private Map<String, String> nonAcceptedSavingWithdrawalPaymentTypes = new LinkedHashMap<String, String>();
    private String[] chosenAcceptedSavingWithdrawals;
    private String[] chosenNonAcceptedSavingWithdrawals;

    private Map<String, String> acceptedSavingDepositsPaymentTypes = new LinkedHashMap<String, String>();
    private Map<String, String> nonAcceptedSavingDepositsPaymentTypes = new LinkedHashMap<String, String>();
    private String[] chosenAcceptedSavingDeposits;
    private String[] chosenNonAcceptedSavingDeposits;

    public Map<String, String> getAcceptedFeePaymentTypes() {
        return this.acceptedFeePaymentTypes;
    }

    public void setAcceptedFeePaymentTypes(Map<String, String> acceptedFeePaymentTypes) {
        this.acceptedFeePaymentTypes = acceptedFeePaymentTypes;
    }

    public Map<String, String> getNonAcceptedFeePaymentTypes() {
        return this.nonAcceptedFeePaymentTypes;
    }

    public void setNonAcceptedFeePaymentTypes(Map<String, String> nonAcceptedFeePaymentTypes) {
        this.nonAcceptedFeePaymentTypes = nonAcceptedFeePaymentTypes;
    }

    public String[] getChosenAcceptedFees() {
        return this.chosenAcceptedFees;
    }

    public void setChosenAcceptedFees(String[] chosenAcceptedFees) {
        this.chosenAcceptedFees = chosenAcceptedFees;
    }

    public String[] getChosenNonAcceptedFees() {
        return this.chosenNonAcceptedFees;
    }

    public void setChosenNonAcceptedFees(String[] chosenNonAcceptedFees) {
        this.chosenNonAcceptedFees = chosenNonAcceptedFees;
    }

    public Map<String, String> getAcceptedLoanDisbursementPaymentTypes() {
        return this.acceptedLoanDisbursementPaymentTypes;
    }

    public void setAcceptedLoanDisbursementPaymentTypes(Map<String, String> acceptedLoanDisbursementPaymentTypes) {
        this.acceptedLoanDisbursementPaymentTypes = acceptedLoanDisbursementPaymentTypes;
    }

    public Map<String, String> getNonAcceptedLoanDisbursementPaymentTypes() {
        return this.nonAcceptedLoanDisbursementPaymentTypes;
    }

    public void setNonAcceptedLoanDisbursementPaymentTypes(Map<String, String> nonAcceptedLoanDisbursementPaymentTypes) {
        this.nonAcceptedLoanDisbursementPaymentTypes = nonAcceptedLoanDisbursementPaymentTypes;
    }

    public String[] getChosenAcceptedLoanDisbursements() {
        return this.chosenAcceptedLoanDisbursements;
    }

    public void setChosenAcceptedLoanDisbursements(String[] chosenAcceptedLoanDisbursements) {
        this.chosenAcceptedLoanDisbursements = chosenAcceptedLoanDisbursements;
    }

    public String[] getChosenNonAcceptedLoanDisbursements() {
        return this.chosenNonAcceptedLoanDisbursements;
    }

    public void setChosenNonAcceptedLoanDisbursements(String[] chosenNonAcceptedLoanDisbursements) {
        this.chosenNonAcceptedLoanDisbursements = chosenNonAcceptedLoanDisbursements;
    }

    public Map<String, String> getAcceptedLoanRepaymentPaymentTypes() {
        return this.acceptedLoanRepaymentPaymentTypes;
    }

    public void setAcceptedLoanRepaymentPaymentTypes(Map<String, String> acceptedLoanRepaymentPaymentTypes) {
        this.acceptedLoanRepaymentPaymentTypes = acceptedLoanRepaymentPaymentTypes;
    }

    public Map<String, String> getNonAcceptedLoanRepaymentPaymentTypes() {
        return this.nonAcceptedLoanRepaymentPaymentTypes;
    }

    public void setNonAcceptedLoanRepaymentPaymentTypes(Map<String, String> nonAcceptedLoanRepaymentPaymentTypes) {
        this.nonAcceptedLoanRepaymentPaymentTypes = nonAcceptedLoanRepaymentPaymentTypes;
    }

    public String[] getChosenAcceptedLoanRepayments() {
        return this.chosenAcceptedLoanRepayments;
    }

    public void setChosenAcceptedLoanRepayments(String[] chosenAcceptedLoanRepayments) {
        this.chosenAcceptedLoanRepayments = chosenAcceptedLoanRepayments;
    }

    public String[] getChosenNonAcceptedLoanRepayments() {
        return this.chosenNonAcceptedLoanRepayments;
    }

    public void setChosenNonAcceptedLoanRepayments(String[] chosenNonAcceptedLoanRepayments) {
        this.chosenNonAcceptedLoanRepayments = chosenNonAcceptedLoanRepayments;
    }

    public Map<String, String> getAcceptedSavingWithdrawalPaymentTypes() {
        return this.acceptedSavingWithdrawalPaymentTypes;
    }

    public void setAcceptedSavingWithdrawalPaymentTypes(Map<String, String> acceptedSavingWithdrawalPaymentTypes) {
        this.acceptedSavingWithdrawalPaymentTypes = acceptedSavingWithdrawalPaymentTypes;
    }

    public Map<String, String> getNonAcceptedSavingWithdrawalPaymentTypes() {
        return this.nonAcceptedSavingWithdrawalPaymentTypes;
    }

    public void setNonAcceptedSavingWithdrawalPaymentTypes(Map<String, String> nonAcceptedSavingWithdrawalPaymentTypes) {
        this.nonAcceptedSavingWithdrawalPaymentTypes = nonAcceptedSavingWithdrawalPaymentTypes;
    }

    public String[] getChosenAcceptedSavingWithdrawals() {
        return this.chosenAcceptedSavingWithdrawals;
    }

    public void setChosenAcceptedSavingWithdrawals(String[] chosenAcceptedSavingWithdrawals) {
        this.chosenAcceptedSavingWithdrawals = chosenAcceptedSavingWithdrawals;
    }

    public String[] getChosenNonAcceptedSavingWithdrawals() {
        return this.chosenNonAcceptedSavingWithdrawals;
    }

    public void setChosenNonAcceptedSavingWithdrawals(String[] chosenNonAcceptedSavingWithdrawals) {
        this.chosenNonAcceptedSavingWithdrawals = chosenNonAcceptedSavingWithdrawals;
    }

    public Map<String, String> getAcceptedSavingDepositsPaymentTypes() {
        return this.acceptedSavingDepositsPaymentTypes;
    }

    public void setAcceptedSavingDepositsPaymentTypes(Map<String, String> acceptedSavingDepositsPaymentTypes) {
        this.acceptedSavingDepositsPaymentTypes = acceptedSavingDepositsPaymentTypes;
    }

    public Map<String, String> getNonAcceptedSavingDepositsPaymentTypes() {
        return this.nonAcceptedSavingDepositsPaymentTypes;
    }

    public void setNonAcceptedSavingDepositsPaymentTypes(Map<String, String> nonAcceptedSavingDepositsPaymentTypes) {
        this.nonAcceptedSavingDepositsPaymentTypes = nonAcceptedSavingDepositsPaymentTypes;
    }

    public String[] getChosenAcceptedSavingDeposits() {
        return this.chosenAcceptedSavingDeposits;
    }

    public void setChosenAcceptedSavingDeposits(String[] chosenAcceptedSavingDeposits) {
        this.chosenAcceptedSavingDeposits = chosenAcceptedSavingDeposits;
    }

    public String[] getChosenNonAcceptedSavingDeposits() {
        return this.chosenNonAcceptedSavingDeposits;
    }

    public void setChosenNonAcceptedSavingDeposits(String[] chosenNonAcceptedSavingDeposits) {
        this.chosenNonAcceptedSavingDeposits = chosenNonAcceptedSavingDeposits;
    }
}
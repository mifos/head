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

package org.mifos.dto.domain;

import java.util.List;

import org.mifos.dto.screen.PaymentTypeDto;

public class AcceptedPaymentTypeDto {
    private List<PaymentTypeDto> inFeeList;
    private List<PaymentTypeDto> outFeeList;
    private List<PaymentTypeDto> inDisbursementList;
    private List<PaymentTypeDto> outDisbursementList;
    private List<PaymentTypeDto> inRepaymentList;
    private List<PaymentTypeDto> outRepaymentList;
    private List<PaymentTypeDto> inWithdrawalList;
    private List<PaymentTypeDto> outWithdrawalList;
    private List<PaymentTypeDto> inDepositList;
    private List<PaymentTypeDto> outDepositList;

    public List<PaymentTypeDto> getInFeeList() {
        return this.inFeeList;
    }

    public void setInFeeList(List<PaymentTypeDto> inFeeList) {
        this.inFeeList = inFeeList;
    }

    public List<PaymentTypeDto> getOutFeeList() {
        return this.outFeeList;
    }

    public void setOutFeeList(List<PaymentTypeDto> outFeeList) {
        this.outFeeList = outFeeList;
    }

    public List<PaymentTypeDto> getInDisbursementList() {
        return this.inDisbursementList;
    }

    public void setInDisbursementList(List<PaymentTypeDto> inDisbursementList) {
        this.inDisbursementList = inDisbursementList;
    }

    public List<PaymentTypeDto> getOutDisbursementList() {
        return this.outDisbursementList;
    }

    public void setOutDisbursementList(List<PaymentTypeDto> outDisbursementList) {
        this.outDisbursementList = outDisbursementList;
    }

    public List<PaymentTypeDto> getInRepaymentList() {
        return this.inRepaymentList;
    }

    public void setInRepaymentList(List<PaymentTypeDto> inRepaymentList) {
        this.inRepaymentList = inRepaymentList;
    }

    public List<PaymentTypeDto> getOutRepaymentList() {
        return this.outRepaymentList;
    }

    public void setOutRepaymentList(List<PaymentTypeDto> outRepaymentList) {
        this.outRepaymentList = outRepaymentList;
    }

    public List<PaymentTypeDto> getInWithdrawalList() {
        return this.inWithdrawalList;
    }

    public void setInWithdrawalList(List<PaymentTypeDto> inWithdrawalList) {
        this.inWithdrawalList = inWithdrawalList;
    }

    public List<PaymentTypeDto> getOutWithdrawalList() {
        return this.outWithdrawalList;
    }

    public void setOutWithdrawalList(List<PaymentTypeDto> outWithdrawalList) {
        this.outWithdrawalList = outWithdrawalList;
    }

    public List<PaymentTypeDto> getInDepositList() {
        return this.inDepositList;
    }

    public void setInDepositList(List<PaymentTypeDto> inDepositList) {
        this.inDepositList = inDepositList;
    }

    public List<PaymentTypeDto> getOutDepositList() {
        return this.outDepositList;
    }

    public void setOutDepositList(List<PaymentTypeDto> outDepositList) {
        this.outDepositList = outDepositList;
    }
}

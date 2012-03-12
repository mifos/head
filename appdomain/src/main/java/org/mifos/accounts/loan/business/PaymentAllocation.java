/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.business;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class PaymentAllocation {
    private Money principalPaid;

    private Money interestPaid;

    private Money penaltyPaid;

    private Money extraInterestPaid;

    private Money miscFeePaid;

    private Money miscPenaltyPaid;

    private Map<Integer, Money> feesPaid;
    
    private Map<Integer, Money> penaltiesPaid;

    private MifosCurrency currency;

    public PaymentAllocation(MifosCurrency currency) {
        this.currency = currency;
        principalPaid = Money.zero(currency);
        interestPaid = Money.zero(this.currency);
        penaltyPaid = Money.zero(this.currency);
        extraInterestPaid = Money.zero(this.currency);
        miscFeePaid = Money.zero(this.currency);
        miscPenaltyPaid = Money.zero(this.currency);
        feesPaid = new HashMap<Integer, Money>();
        penaltiesPaid = new HashMap<Integer, Money>();
    }

    public void allocateForMiscPenalty(Money payable) {
        this.miscPenaltyPaid = payable;
    }

    public void allocateForPenalty(Money payable) {
        this.penaltyPaid = payable;
    }
    
    public void allocateForPenalty(Integer penaltyId, Money penaltyAmount) {
        penaltiesPaid.put(penaltyId, penaltyAmount);
    }

    public void allocateForMiscFees(Money payable) {
        this.miscFeePaid = payable;
    }

    public void allocateForExtraInterest(Money payable) {
        this.extraInterestPaid = payable;
    }

    public void allocateForInterest(Money payable) {
        this.interestPaid = payable;
    }

    public void allocateForPrincipal(Money payable) {
        this.principalPaid = payable;
    }

    public void allocateForFee(Integer feeId, Money feeAllocated) {
        feesPaid.put(feeId, feeAllocated);
    }

    public Money getPrincipalPaid() {
        return principalPaid;
    }

    public Money getInterestPaid() {
        return interestPaid;
    }

    public Money getPenaltyPaid() {
        return penaltyPaid;
    }

    public Money getExtraInterestPaid() {
        return extraInterestPaid;
    }

    public Money getMiscFeePaid() {
        return miscFeePaid;
    }

    public Money getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public Money getFeePaid(Integer feeId) {
        return feesPaid.get(feeId);
    }
    
    public Money getPenaltyPaid(Integer penaltyId) {
        return penaltiesPaid.get(penaltyId);
    }

    public Money getTotalPaid() {
        return interestPaid.add(extraInterestPaid).add(penaltyPaid).add(principalPaid).add(miscFeePaid).add(miscPenaltyPaid).
                add(getTotalFeesPaid()).add(getTotalPenaltiesPaid());
    }

    public Money getTotalFeesPaid() {
        Money totalFeePaid = Money.zero(currency);
        for (Money feePaid : feesPaid.values()) {
            totalFeePaid = totalFeePaid.add(feePaid);
        }
        return totalFeePaid;
    }
    
    public Money getTotalPenaltiesPaid() {
        Money totalPenaltyPaid = Money.zero(currency);
        for (Money penaltyPaid : penaltiesPaid.values()) {
            totalPenaltyPaid = totalPenaltyPaid.add(penaltyPaid);
        }
        return totalPenaltyPaid;
    }

    boolean isFeeAllocated(Integer feeId) {
        return getFeePaid(feeId) != null;
    }
    
    boolean isPenaltyAllocated(Integer penaltyId) {
        return getPenaltyPaid(penaltyId) != null;
    }

    Money getTotalPenaltyPaid() {
        return getPenaltyPaid().add(getMiscPenaltyPaid());
    }

    Money getTotalAndMiscFeesPaid() {
        return getTotalFeesPaid().add(getMiscFeePaid());
    }

    boolean hasAllocation() {
        return getTotalPaid().isGreaterThanZero();
    }

    Money getTotalInterestPaid() {
        return getInterestPaid().add(getExtraInterestPaid());
    }
}

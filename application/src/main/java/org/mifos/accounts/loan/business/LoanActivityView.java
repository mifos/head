/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.loan.business;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanActivityView implements Serializable {

    private Integer id;
    private Date actionDate;
    private String activity;
    private Money principal;
    private Money interest;
    private Money fees;
    private Money penalty;
    private Money total;
    private Money runningBalancePrinciple;
    private Money runningBalanceInterest;
    private Money runningBalanceFees;
    private Money runningBalancePenalty;
    private Locale locale = null;
    private java.sql.Timestamp timeStamp;
    
    public LoanActivityView(MifosCurrency currency) {
        this.principal = new Money(currency);
        this.interest = new Money(currency);
        this.fees = new Money(currency);
        this.penalty = new Money(currency);
        this.total = new Money(currency);
        this.runningBalancePrinciple = new Money(currency);
        this.runningBalanceInterest = new Money(currency);
        this.runningBalanceFees = new Money(currency);
        this.runningBalancePenalty = new Money(currency);
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Money getFees() {
        return fees;
    }

    public void setFees(Money fees) {
        this.fees = fees;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getInterest() {
        return interest;
    }

    public void setInterest(Money interest) {
        this.interest = interest;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Money getPenalty() {
        return penalty;
    }

    public void setPenalty(Money penalty) {
        this.penalty = penalty;
    }

    public Money getPrincipal() {
        return principal;
    }

    public void setPrincipal(Money principal) {
        this.principal = principal;
    }

    public Money getRunningBalanceFees() {
        return runningBalanceFees;
    }

    public void setRunningBalanceFees(Money runningBalanceFees) {
        this.runningBalanceFees = runningBalanceFees;
    }

    public Money getRunningBalanceInterest() {
        return runningBalanceInterest;
    }

    public void setRunningBalanceInterest(Money runningBalanceInterest) {
        this.runningBalanceInterest = runningBalanceInterest;
    }

    public Money getRunningBalancePrinciple() {
        return runningBalancePrinciple;
    }

    public void setRunningBalancePrinciple(Money runningBalancePrinciple) {
        this.runningBalancePrinciple = runningBalancePrinciple;
    }

    public java.sql.Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(java.sql.Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Money getTotal() {
        return total;
    }

    public void setTotal(Money total) {
        this.total = total;
    }

    public Money getRunningBalancePenalty() {
        return runningBalancePenalty;
    }

    public void setRunningBalancePenalty(Money runningBalancePenalty) {
        this.runningBalancePenalty = runningBalancePenalty;
    }

    public String getUserPrefferedDate() {
        return DateUtils.getUserLocaleDate(getLocale(), getActionDate().toString());
    }

}

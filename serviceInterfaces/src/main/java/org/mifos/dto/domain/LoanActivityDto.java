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

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanActivityDto implements Serializable {

    private Integer id;
    private Date actionDate;
    private String activity;
    private String principal;
    private String interest;
    private String fees;
    private String penalty;
    private String total;
    private String runningBalancePrinciple;
    private String runningBalanceInterest;
    private String runningBalanceFees;
    private String runningBalancePenalty;
    private Locale locale = null;
    private java.sql.Timestamp timeStamp;
    private String runningBalancePrincipleWithInterestAndFees;
    private double totalValue;
    private String userPrefferedDate;

    public LoanActivityDto() {
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

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getRunningBalanceFees() {
        return runningBalanceFees;
    }

    public void setRunningBalanceFees(String runningBalanceFees) {
        this.runningBalanceFees = runningBalanceFees;
    }

    public String getRunningBalanceInterest() {
        return runningBalanceInterest;
    }

    public void setRunningBalanceInterest(String runningBalanceInterest) {
        this.runningBalanceInterest = runningBalanceInterest;
    }

    public String getRunningBalancePrinciple() {
        return runningBalancePrinciple;
    }

    public void setRunningBalancePrinciple(String runningBalancePrinciple) {
        this.runningBalancePrinciple = runningBalancePrinciple;
    }

    public java.sql.Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(java.sql.Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRunningBalancePenalty() {
        return runningBalancePenalty;
    }

    public void setRunningBalancePenalty(String runningBalancePenalty) {
        this.runningBalancePenalty = runningBalancePenalty;
    }

    public String getUserPrefferedDate() {
        return userPrefferedDate;
    }

    public void setUserPrefferedDate(String userPrefferedDate) {
        this.userPrefferedDate = userPrefferedDate;
    }

    public String getRunningBalancePrincipleWithInterestAndFees() {
        return runningBalancePrincipleWithInterestAndFees;
    }

    public void setRunningBalancePrincipleWithInterestAndFees(String runningBalancePrincipleWithInterestAndFees) {
        this.runningBalancePrincipleWithInterestAndFees = runningBalancePrincipleWithInterestAndFees;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getTotalValue() {
        return totalValue;
    }
}
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

package org.mifos.application.reports.business.dto;

import java.math.BigDecimal;

public class CollectionSheetReportData {

    private final String meetingDate;
    private final Integer officeId;
    private final String officeName;
    private final Integer staffId;
    private final String loanOfficerName;
    private final Integer centerId;
    private final String centerName;
    private final Integer groupId;
    private final String groupName;
    private final Integer memberId;
    private final String memberName;
    private final String loanBalance;
    private final BigDecimal totalLoanBalance;
    private final String loanDue;
    private final BigDecimal totalLoanDue;
    private final String loanDisbursed;
    private final BigDecimal totalLoanDisbursed;
    private final String loanFees;
    private final BigDecimal totalLoanFees;

    public CollectionSheetReportData(Object[] objects) {
        this((String) objects[0], (Integer) objects[1], (String) objects[2], (Integer) objects[3], (String) objects[4],
                (Integer) objects[5], (String) objects[6], (Integer) objects[7], (String) objects[8],
                (Integer) objects[9], (String) objects[10], (String) objects[11], (BigDecimal) objects[12],
                (String) objects[13], (BigDecimal) objects[14], (String) objects[15], (BigDecimal) objects[16],
                (String) objects[17], (BigDecimal) objects[18]);
    }

    public CollectionSheetReportData(String meetingDate, Integer officeId, String officeName, Integer staffId,
            String loanOfficerName, Integer centerId, String centerName, Integer groupId, String groupName,
            Integer memberId, String memberName, String loanBalance, BigDecimal totalLoanBalance, String loanDue,
            BigDecimal totalLoanDue, String loanDisbursed, BigDecimal totalLoanDisbursed, String loanFees,
            BigDecimal totalLoanFees) {
        this.meetingDate = meetingDate;
        this.officeId = officeId;
        this.officeName = officeName;
        this.staffId = staffId;
        this.loanOfficerName = loanOfficerName;
        this.centerId = centerId;
        this.centerName = centerName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.loanBalance = loanBalance;
        this.totalLoanBalance = totalLoanBalance;
        this.loanDue = loanDue;
        this.totalLoanDue = totalLoanDue;
        this.loanDisbursed = loanDisbursed;
        this.totalLoanDisbursed = totalLoanDisbursed;
        this.loanFees = loanFees;
        this.totalLoanFees = totalLoanFees;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getLoanBalance() {
        return loanBalance;
    }

    public BigDecimal getTotalLoanBalance() {
        return totalLoanBalance;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public Integer getCenterId() {
        return centerId;
    }

    public String getLoanOfficerName() {
        return loanOfficerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public String getLoanDue() {
        return loanDue;
    }

    public BigDecimal getTotalLoanDue() {
        return totalLoanDue;
    }

    public String getLoanFees() {
        return loanFees;
    }

    public BigDecimal getTotalLoanFees() {
        return totalLoanFees;
    }

    public String getLoanDisbursed() {
        return loanDisbursed;
    }

    public BigDecimal getTotalLoanDisbursed() {
        return totalLoanDisbursed;
    }
}

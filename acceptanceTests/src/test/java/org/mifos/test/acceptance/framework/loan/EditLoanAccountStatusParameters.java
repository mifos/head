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

package org.mifos.test.acceptance.framework.loan;

public class EditLoanAccountStatusParameters {
    // Status constants
    public final static String NEW = "-";
    public final static String APPROVED = "Application Approved";
    public final static String PARTIAL_APPLICATION = "Partial Application";
    public final static String PENDING_APPROVAL = "Application Pending Approval";
    public final static String CANCEL = "Cancel";
    
    public final static String CLOSED_WRITTEN_OFF = "Closed- Written Off";

    public final static String CANCEL_REASON_OTHER = "Other";
    public final static String CANCEL_REASON_WITHDRAW = "Withdraw";
    public final static String CANCEL_REASON_REJECTED = "Rejected";
    public final static String CANCEL_REASON_LOAN_REVERSAL = "Loan reversal";

    private String status;
    private String note;
    private String cancelReason;

    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNote() {
        return this.note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getCancelReason() {
        return this.cancelReason;
    }
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    /**
     * Maps the status string to a value that's used to choose the correct radio box.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getStatusValue() {
        if (PARTIAL_APPLICATION.equals(status)) { return 1; }
        if (PENDING_APPROVAL.equals(status)) { return 2; }
        if (APPROVED.equals(status)) { return 3; }
        if (CANCEL.equals(status)) { return 10; }
        if (CLOSED_WRITTEN_OFF.equals(status)) { return 7; }

        return -1;
    }

    /**
     * Maps the cancel reason string to a value so we can choose the right element in the select drop-down.
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public int getCancelReasonValue() {
        if (CANCEL_REASON_WITHDRAW.equals(cancelReason)) { return 1; }
        if (CANCEL_REASON_REJECTED.equals(cancelReason)) { return 2; }
        if (CANCEL_REASON_OTHER.equals(cancelReason)) { return 3; }

        return -1;
    }
}

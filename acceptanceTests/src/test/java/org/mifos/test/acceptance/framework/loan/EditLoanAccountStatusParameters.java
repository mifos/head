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

package org.mifos.test.acceptance.framework.loan;

public class EditLoanAccountStatusParameters {
    // Status constants
    public final static String APPROVED = "Application Approved";
    public final static String PARTIAL_APPLICATION = "Partial Application";
    public final static String PENDING_APPROVAL = "Pending for Approval";
    public final static String CANCEL = "Cancel";
    
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
        if ("Partial Application".equals(status)) { return 1; }
        if ("Pending for Approval".equals(status)) { return 2; }
        if ("Application Approved".equals(status)) { return 3; }
        if ("Cancel".equals(status)) { return 10; }
        
        return -1;
    }
}

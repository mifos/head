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

public class ProcessRulesDto {

    private final boolean clientPendingApprovalStateEnabled;
    private final boolean governmentIdValidationFailing;
    private final boolean duplicateNameOnClosedClient;
    private final boolean duplicateNameOnBlackListedClient;
    private final boolean governmentIdValidationUnclosedFailing;
    private final boolean duplicateNameOnClient;

    public ProcessRulesDto(boolean clientPendingApprovalStateEnabled, boolean governmentIdValidationFailing,
            boolean duplicateNameOnClosedClient, boolean duplicateNameOnBlackListedClient, boolean governmentIdValidationUnclosedFailing, boolean duplicateNameOnClient) {
        this.clientPendingApprovalStateEnabled = clientPendingApprovalStateEnabled;
        this.governmentIdValidationFailing = governmentIdValidationFailing;
        this.duplicateNameOnClosedClient = duplicateNameOnClosedClient;
        this.duplicateNameOnBlackListedClient = duplicateNameOnBlackListedClient;
        this.governmentIdValidationUnclosedFailing = governmentIdValidationUnclosedFailing;
        this.duplicateNameOnClient = duplicateNameOnClient;
    }

    public boolean isClientPendingApprovalStateEnabled() {
        return this.clientPendingApprovalStateEnabled;
    }

    public boolean isGovernmentIdValidationFailing() {
        return this.governmentIdValidationFailing;
    }

    public boolean isDuplicateNameOnClosedClient() {
        return duplicateNameOnClosedClient;
    }

    public boolean isDuplicateNameOnBlackListedClient() {
        return duplicateNameOnBlackListedClient;
    }
    public boolean isGovermentIdValidationUnclosedFailing() {
        return this.governmentIdValidationUnclosedFailing;
    }
    public boolean isduplicateNameOnClient() {
        return duplicateNameOnClient;
    }
}
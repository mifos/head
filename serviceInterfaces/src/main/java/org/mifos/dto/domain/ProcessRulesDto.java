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
import java.util.List;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class ProcessRulesDto implements Serializable {

    private final boolean clientPendingApprovalStateEnabled;
    private final boolean governmentIdValidationFailing;
    private final boolean duplicateNameOnClosedClient;
    private final boolean duplicateNameOnBlackListedClient;
    private final boolean governmentIdValidationUnclosedFailing;
    private final boolean duplicateNameOnClient;

    private final List<MatchedClientDto> matchedClients;
    
    public ProcessRulesDto(boolean clientPendingApprovalStateEnabled, boolean governmentIdValidationFailing,
            boolean duplicateNameOnClosedClient, boolean duplicateNameOnBlackListedClient, boolean governmentIdValidationUnclosedFailing, boolean duplicateNameOnClient,
            List<MatchedClientDto> matchedClients) {
        this.clientPendingApprovalStateEnabled = clientPendingApprovalStateEnabled;
        this.governmentIdValidationFailing = governmentIdValidationFailing;
        this.duplicateNameOnClosedClient = duplicateNameOnClosedClient;
        this.duplicateNameOnBlackListedClient = duplicateNameOnBlackListedClient;
        this.governmentIdValidationUnclosedFailing = governmentIdValidationUnclosedFailing;
        this.duplicateNameOnClient = duplicateNameOnClient;
        this.matchedClients = matchedClients;
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

	public List<MatchedClientDto> getMatchedClients() {
		return matchedClients;
	}
}
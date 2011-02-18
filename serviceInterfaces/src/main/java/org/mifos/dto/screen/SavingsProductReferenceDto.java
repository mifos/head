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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.List;

import org.mifos.dto.domain.SavingsProductDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class SavingsProductReferenceDto implements Serializable {

    private final List<ListElement> interestCalcTypeOptions;
    private final SavingsProductDto savingsProductDetails;
    private final boolean savingsPendingApprovalEnabled;

    public SavingsProductReferenceDto(List<ListElement> interestCalcTypeOptions, SavingsProductDto savingsProductDetails, boolean savingsPendingApprovalEnabled) {
        this.interestCalcTypeOptions = interestCalcTypeOptions;
        this.savingsProductDetails = savingsProductDetails;
        this.savingsPendingApprovalEnabled = savingsPendingApprovalEnabled;
    }

    public List<ListElement> getInterestCalcTypeOptions() {
        return this.interestCalcTypeOptions;
    }

    public SavingsProductDto getSavingsProductDetails() {
        return this.savingsProductDetails;
    }

    public boolean isSavingsPendingApprovalEnabled() {
        return this.savingsPendingApprovalEnabled;
    }
}
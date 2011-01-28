/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.dto.screen.SavingsRecentActivityDto;

public class SavingsAccountDetailDto implements Serializable {

    private final SavingsProductDto productDetails;
    private final List<SavingsRecentActivityDto> recentActivity;
    private final List<CustomerNoteDto> recentNoteDtos;
    private final String recommendedOrMandatoryAmount;
    private final String globalAccountNum;

    public SavingsAccountDetailDto(SavingsProductDto productDetails, List<SavingsRecentActivityDto> recentActivity, List<CustomerNoteDto> recentNoteDtos, String recommendedOrMandatoryAmount, String globalAccountNum) {
        this.productDetails = productDetails;
        this.recentActivity = recentActivity;
        this.recentNoteDtos = recentNoteDtos;
        this.recommendedOrMandatoryAmount = recommendedOrMandatoryAmount;
        this.globalAccountNum = globalAccountNum;
    }

    public SavingsProductDto getProductDetails() {
        return this.productDetails;
    }

    public List<SavingsRecentActivityDto> getRecentActivity() {
        return this.recentActivity;
    }

    public List<CustomerNoteDto> getRecentNoteDtos() {
        return this.recentNoteDtos;
    }

    public String getRecommendedOrMandatoryAmount() {
        return this.recommendedOrMandatoryAmount;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }
}
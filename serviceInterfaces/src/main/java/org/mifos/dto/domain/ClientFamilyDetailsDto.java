/*
 * Copyright Grameen Foundation USA
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

import java.util.List;


public class ClientFamilyDetailsDto {

    private final boolean familyDetailsRequired;
    private final List<FamilyDetailDto> familyDetails;
    private final List<ValueListElement> genders;
    private final List<ValueListElement> livingStatus;

    public ClientFamilyDetailsDto(boolean familyDetailsRequired, List<FamilyDetailDto> familyDetails,
            List<ValueListElement> genders, List<ValueListElement> livingStatus) {
        this.familyDetailsRequired = familyDetailsRequired;
        this.familyDetails = familyDetails;
        this.genders = genders;
        this.livingStatus = livingStatus;
    }

    public boolean isFamilyDetailsRequired() {
        return this.familyDetailsRequired;
    }

    public List<FamilyDetailDto> getFamilyDetails() {
        return this.familyDetails;
    }

    public List<ValueListElement> getGenders() {
        return this.genders;
    }

    public List<ValueListElement> getLivingStatus() {
        return this.livingStatus;
    }

}

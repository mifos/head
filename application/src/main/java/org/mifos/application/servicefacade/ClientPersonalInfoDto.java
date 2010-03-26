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

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.application.master.business.CustomFieldView;

public class ClientPersonalInfoDto {

    private final ClientDropdownsDto clientDropdowns;
    private final List<CustomFieldView> customFieldViews;
    private final ClientRulesDto clientRules;

    public ClientPersonalInfoDto(ClientDropdownsDto clientDropdowns, List<CustomFieldView> customFieldViews, ClientRulesDto clientRules) {
        this.clientDropdowns = clientDropdowns;
        this.customFieldViews = customFieldViews;
        this.clientRules = clientRules;
    }

    public ClientDropdownsDto getClientDropdowns() {
        return this.clientDropdowns;
    }

    public List<CustomFieldView> getCustomFieldViews() {
        return this.customFieldViews;
    }

    public ClientRulesDto getClientRules() {
        return this.clientRules;
    }
}
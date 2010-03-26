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

import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.util.helpers.CustomerDetailDto;

public class ClientMfiInfoDto {

    private final String groupDisplayName;
    private final String centerDisplayName;
    private final List<PersonnelView> loanOfficersList;
    private final CustomerDetailDto customerDetail;
    private final ClientDetailDto clientDetail;

    public ClientMfiInfoDto(String groupDisplayName, String centerDisplayName, List<PersonnelView> loanOfficersList,
            CustomerDetailDto customerDetail, ClientDetailDto clientDetail) {
        this.groupDisplayName = groupDisplayName;
        this.centerDisplayName = centerDisplayName;
        this.loanOfficersList = loanOfficersList;
        this.customerDetail = customerDetail;
        this.clientDetail = clientDetail;
    }

    public String getGroupDisplayName() {
        return this.groupDisplayName;
    }

    public String getCenterDisplayName() {
        return this.centerDisplayName;
    }

    public List<PersonnelView> getLoanOfficersList() {
        return this.loanOfficersList;
    }

    public CustomerDetailDto getCustomerDetail() {
        return this.customerDetail;
    }

    public ClientDetailDto getClientDetail() {
        return this.clientDetail;
    }
}
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

package org.mifos.domain.builders;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.servicefacade.ClientPersonalInfoUpdate;
import org.mifos.customers.client.business.ClientNameDetailDto;
import org.mifos.customers.client.business.ClientPersonalDetailDto;
import org.mifos.framework.business.util.Address;

public class ClientPersonalInfoUpdateBuilder {

    private Integer customerId = Integer.valueOf(-1);
    private Integer originalClientVersionNumber = Integer.valueOf(1);
    private List<CustomFieldDto> clientCustomFields = new ArrayList<CustomFieldDto>();
    private Address address = null;
    private ClientPersonalDetailDto clientDetail = new ClientPersonalDetailDtoBuilder().build();
    private ClientNameDetailDto clientNameDetails = null;
    private ClientNameDetailDto spouseFather = null;
    private InputStream picture = null;
    private String governmentId = "123xxx";
    private String clientDisplayName = "newBuilder Client";
    private String dateOfBirth = "17/09/1980";

    public ClientPersonalInfoUpdate build() {
        return new ClientPersonalInfoUpdate(customerId, originalClientVersionNumber, clientCustomFields, address, clientDetail, clientNameDetails, spouseFather, picture, governmentId, clientDisplayName, dateOfBirth);
    }
}
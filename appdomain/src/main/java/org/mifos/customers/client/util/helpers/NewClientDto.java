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

package org.mifos.customers.client.util.helpers;

import java.io.Serializable;

import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.util.helpers.CustomerStatus;

public class NewClientDto implements Serializable {

    private static final long serialVersionUID = 2666336429420949976L;

    private ClientBO clientBO;
    private CustomerStatus customerStatus;

    public NewClientDto(ClientBO clientBO, CustomerStatus customerStatus) {
        this.clientBO = clientBO;
        this.customerStatus = customerStatus;
    }

    public ClientBO getClientBO() {
        return clientBO;
    }

    public CustomerStatus getCustomerStatus() {
        return customerStatus;
    }

}

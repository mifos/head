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

import org.joda.time.DateTime;
import org.mifos.application.servicefacade.ClientMfiInfoUpdate;

public class ClientMfiInfoUpdateBuilder {

    private Integer clientId = Integer.valueOf(-1);
    private Integer orginalClientVersionNumber = Integer.valueOf(1);
    private Short personnelId = Short.valueOf("1");
    private String externalId = null;
    private boolean trained = true;
    private DateTime trainedDate = new DateTime().toDateMidnight().toDateTime();

    public ClientMfiInfoUpdate build() {
        return new ClientMfiInfoUpdate(clientId, orginalClientVersionNumber, personnelId, externalId, trained, trainedDate);
    }
}
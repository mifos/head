/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.customers.util.helpers;

import java.util.Date;

import org.joda.time.DateTime;

public class CenterDisplayDtoBuilder {

    final Integer customerId = Integer.valueOf(1);
    final String globalCustNum = "XXX-" + customerId;
    final String displayName = "centerXX";
    final Short branchId = Short.valueOf("1");
    final Date mfiJoiningDate = new DateTime().minusDays(1).toDateMidnight().toDate(); 
    final Date createdDate = new DateTime().minusDays(1).toDateMidnight().toDate();
    final Integer versionNo = Integer.valueOf(1);
    final String externalId = "";
    final Short customerLevelId = Short.valueOf("1");
    final Short customerStatusId = Short.valueOf("1");
    final String customerStatusName = "";
    final Short loanOfficerId = Short.valueOf("1");
    final String loanOfficerName = "loanOfficerXX";
    
    public CenterDisplayDto build() {
        return new CenterDisplayDto(customerId, globalCustNum, displayName, branchId, mfiJoiningDate, createdDate, versionNo, externalId, customerLevelId, customerStatusId, customerStatusName, loanOfficerId, loanOfficerName);
    }

}

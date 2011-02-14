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

package org.mifos.clientportfolio.newloan.applicationservice;

import org.mifos.clientportfolio.newloan.domain.ClientId;
import org.mifos.clientportfolio.newloan.domain.LoanProductId;

public class IndividualLoanRequestBuilder {

    private String loanProductGlobalId = "test-001-loanproduct";
    private String clientGlobalId = "test-001-client";

    public IndividualLoanRequest build() {
        LoanProductId loanProductId = new LoanProductId(loanProductGlobalId);
        ClientId clientId = new ClientId(clientGlobalId);

        return new IndividualLoanRequest(loanProductId, clientId);
    }

    public IndividualLoanRequestBuilder with(LoanProductId loanProductId) {
        this.loanProductGlobalId = loanProductId.globalIdentity();
        return this;
    }

    public IndividualLoanRequestBuilder with(ClientId clientId) {
        this.clientGlobalId = clientId.globalIdentity();
        return this;
    }

}

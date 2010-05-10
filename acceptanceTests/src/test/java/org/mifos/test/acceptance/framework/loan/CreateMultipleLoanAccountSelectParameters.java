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

package org.mifos.test.acceptance.framework.loan;

public class CreateMultipleLoanAccountSelectParameters {
    public String getBranch() {
        return this.branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getLoanOfficer() {
        return this.loanOfficer;
    }
    public void setLoanOfficer(String loanOfficer) {
        this.loanOfficer = loanOfficer;
    }
    public String getCenter() {
        return this.center;
    }
    public void setCenter(String center) {
        this.center = center;
    }
    public String getLoanProduct() {
        return this.loanProduct;
    }
    public void setLoanProduct(String loanProduct) {
        this.loanProduct = loanProduct;
    }

    private String branch;
    private String loanOfficer;
    private String center;
    private String loanProduct;
}

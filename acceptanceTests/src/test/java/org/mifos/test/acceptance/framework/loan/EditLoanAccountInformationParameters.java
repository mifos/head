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

package org.mifos.test.acceptance.framework.loan;

public class EditLoanAccountInformationParameters {

    private String gracePeriod;
    private String purposeOfLoan;
    private String collateralType;
    private String collateralNotes;
    private String externalID;


    public String getGracePeriod() {
        return this.gracePeriod;
    }
    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    public String getPurposeOfLoan() {
        return this.purposeOfLoan;
    }
    public void setPurposeOfLoan(String purposeOfLoan) {
        this.purposeOfLoan = purposeOfLoan;
    }
    public String getCollateralType() {
        return this.collateralType;
    }
    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }
    public String getCollateralNotes() {
        return this.collateralNotes;
    }
    public void setCollateralNotes(String collateralNotes) {
        this.collateralNotes = collateralNotes;
    }
    public String getExternalID() {
        return this.externalID;
    }
    public void setExternalID(String externalID) {
        this.externalID = externalID;
    }


}

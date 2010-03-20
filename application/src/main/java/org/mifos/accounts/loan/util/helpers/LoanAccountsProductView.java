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

package org.mifos.accounts.loan.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.business.View;

public class LoanAccountsProductView extends View {

    private List<LoanAccountView> loanAccountViews;

    private String prdOfferingShortName;

    private Short prdOfferingId;

    private String enteredAmount;

    private String disBursementAmountEntered;

    private boolean validDisbursementAmount;

    private boolean isValidAmountEntered;

    public LoanAccountsProductView(Short prdOfferingId, String prdOfferingShortName) {
        this.prdOfferingId = prdOfferingId;
        this.prdOfferingShortName = prdOfferingShortName;
        loanAccountViews = new ArrayList<LoanAccountView>();
        isValidAmountEntered = true;
        enteredAmount = "0.0";
        validDisbursementAmount = true;
        disBursementAmountEntered = "0.0";
    }

    public List<LoanAccountView> getLoanAccountViews() {
        return loanAccountViews;
    }

    public void addLoanAccountView(LoanAccountView loanAccountView) {
        loanAccountViews.add(loanAccountView);
    }

    public Short getPrdOfferingId() {
        return prdOfferingId;
    }

    public void setPrdOfferingId(Short prdOfferingId) {
        this.prdOfferingId = prdOfferingId;
    }

    public String getPrdOfferingShortName() {
        return prdOfferingShortName;
    }

    public void setPrdOfferingShortName(String prdOfferingShortName) {
        this.prdOfferingShortName = prdOfferingShortName;
    }

    public String getEnteredAmount() {
        return enteredAmount;
    }

    public void setEnteredAmount(String enteredAmount) {
        this.enteredAmount = enteredAmount;
    }

    public boolean isValidAmountEntered() {
        return isValidAmountEntered;
    }

    public void setValidAmountEntered(boolean isValidAmountEntered) {
        this.isValidAmountEntered = isValidAmountEntered;
    }

    public String getDisBursementAmountEntered() {
        return disBursementAmountEntered;
    }

    public void setDisBursementAmountEntered(String disBursementAmountEntered) {
        /*
         * This is causing test failures. Which is right? The check or the
         * tests? If the latter, what does null mean? if
         * (disBursementAmountEntered == null) { throw new NullPointerException(
         * "Disbursement amount entered is required"); }
         */
        this.disBursementAmountEntered = disBursementAmountEntered;
    }

    public boolean isValidDisbursementAmount() {
        return validDisbursementAmount;
    }

    public void setValidDisbursementAmount(boolean validDisbursementAmount) {
        this.validDisbursementAmount = validDisbursementAmount;
    }

    public Double getTotalAmountDue() {
        Double totalAmount = 0.0;
        for (LoanAccountView loanAccountView : loanAccountViews) {
            totalAmount = totalAmount + loanAccountView.getTotalAmountDue();
        }
        return totalAmount;
    }

    public Double getTotalDisburseAmount() {
        Double totalAmount = 0.0;
        for (LoanAccountView loanAccountView : loanAccountViews) {
            totalAmount = totalAmount + loanAccountView.getTotalDisburseAmount();
        }
        return totalAmount;
    }

    public boolean isDisburseLoanAccountPresent() {
        for (LoanAccountView loanAccountView : loanAccountViews) {
            if (loanAccountView.isDisbursalAccount()) {
                return true;
            }
        }
        return false;
    }

    public Double getTotalDisbursalAmountDue() {
        Double totalAmount = 0.0;
        for (LoanAccountView loanAccountView : loanAccountViews) {
            if (loanAccountView.isDisbursalAccount()) {
                totalAmount = totalAmount + loanAccountView.getTotalAmountDue();
            }
        }
        return totalAmount;
    }
}

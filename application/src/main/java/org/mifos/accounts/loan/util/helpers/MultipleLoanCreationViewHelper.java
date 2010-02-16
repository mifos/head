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

package org.mifos.accounts.loan.util.helpers;

import static org.mifos.framework.util.helpers.FormUtils.getDoubleValue;
import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;

import org.apache.commons.lang.StringUtils;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.accounts.productdefinition.business.LoanAmountOption;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.framework.util.helpers.Money;

public class MultipleLoanCreationViewHelper {

    private String loanAmount;

    private String businessActivity;

    private LoanAmountOption loanAmountOption;

    private LoanOfferingInstallmentRange installmentOption;

    private ClientBO client;

    private String selected;
    
    private MifosCurrency currency;

    public MultipleLoanCreationViewHelper(ClientBO client, LoanAmountOption loanAmountOption,
            LoanOfferingInstallmentRange installmentOption, MifosCurrency currency) {
        super();
        this.client = client;
        this.loanAmountOption = loanAmountOption;
        this.installmentOption = installmentOption;
        this.currency = currency;
        this.loanAmount = getDefaultLoanAmount().toString();
    }

    public MultipleLoanCreationViewHelper() {
        this(null, null, null, null);
    }

    public String getBusinessActivity() {
        return businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public Integer getClientId() {
        return client.getCustomerId();
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getClientName() {
        return client.getDisplayName();
    }

    public boolean isLoanAmountInRange() {
        return StringUtils.isNotBlank(this.loanAmount)
                && loanAmountOption.isInRange(getDoubleValue(this.loanAmount));
    }
    //FIXME: Loan are created using double, the better way to do this would be to 
    // make those double argument as Money or BigDecimal. this workaround is added 
    // to fix MIFOS-2698
    public Money getMinLoanAmount() {
        return loanAmountOption == null ? new Money(getCurrency()) : new Money(getCurrency(), loanAmountOption
                .getMinLoanAmount().toString());
    }
    //FIXME: Loan are created using double, the better way to do this would be to 
    // make those double argument as Money or BigDecimal. this workaround is added 
    // to fix MIFOS-2698
    public Money getMaxLoanAmount() {
        return loanAmountOption == null ? new Money(getCurrency()) : new Money(getCurrency(), loanAmountOption
                .getMaxLoanAmount().toString());
    }
    //FIXME: Loan are created using double, the better way to do this would be to 
    // make those double argument as Money or BigDecimal. this workaround is added 
    // to fix MIFOS-2698
    public Money getDefaultLoanAmount() {
        return loanAmountOption == null ? new Money(getCurrency()) : new Money(getCurrency(), loanAmountOption
                .getDefaultLoanAmount().toString());
    }

    public Short getDefaultNoOfInstall() {
        return installmentOption == null ? SHORT_ZERO : installmentOption.getDefaultNoOfInstall();
    }

    public Short getMaxNoOfInstall() {
        return installmentOption == null ? SHORT_ZERO : installmentOption.getMaxNoOfInstall();
    }

    public Short getMinNoOfInstall() {
        return installmentOption == null ? SHORT_ZERO : installmentOption.getMinNoOfInstall();
    }

    public boolean isApplicable() {
        return Boolean.valueOf(selected);
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public ClientBO getClient() {
        return client;
    }

    public void resetSelected() {
        this.selected = Boolean.FALSE.toString();
    }

    public void setCurrency(MifosCurrency currency) {
        this.currency = currency;
    }

    public MifosCurrency getCurrency() {
        return currency;
    }
}

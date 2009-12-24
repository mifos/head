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

package org.mifos.application.cashconfirmationreport;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyFactory;
import org.mifos.framework.util.helpers.MoneyUtils;

public abstract class BranchCashConfirmationInfoBO extends BranchCashConfirmationSubReport {
    private Integer id;
    private String productOffering;
    private Money actual;

    protected BranchCashConfirmationInfoBO() {
    }

    public BranchCashConfirmationInfoBO(String productOffering, Money actual) {
        super();
        this.productOffering = productOffering;
        this.actual = actual;
    }

    public Integer getId() {
        return id;
    }

    public String getProductOffering() {
        return productOffering;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((actual == null) ? 0 : actual.hashCode());
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        result = PRIME * result + ((productOffering == null) ? 0 : productOffering.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BranchCashConfirmationInfoBO other = (BranchCashConfirmationInfoBO) obj;
        if (actual == null) {
            if (other.actual != null)
                return false;
        } else if (!actual.equals(other.actual))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (productOffering == null) {
            if (other.productOffering != null)
                return false;
        } else if (!productOffering.equals(other.productOffering))
            return false;
        return true;
    }

    public static List<BranchCashConfirmationInfoBO> createIssuesBO(List<String> productOfferingNames,
            final MifosCurrency currency) {
        return (List<BranchCashConfirmationInfoBO>) CollectionUtils.collect(productOfferingNames, new Transformer() {
            public Object transform(Object input) {
                return new BranchCashConfirmationIssueBO((String) input, MoneyFactory.zero(currency));
            }
        });
    }

    @Override
    public String toString() {
        return "[" + getClass().getName() + ": id=" + id + " productOffering=" + productOffering + " actual=" + actual
                + "]";
    }

    public String getActual() {
        return MoneyUtils.getMoneyAmount(actual, AccountingRules.getDigitsAfterDecimal()).toString();
    }
}

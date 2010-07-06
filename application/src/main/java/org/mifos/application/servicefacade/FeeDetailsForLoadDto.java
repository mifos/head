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

package org.mifos.application.servicefacade;

import java.util.List;

import org.mifos.accounts.fees.struts.action.FeeParameters;
import org.mifos.application.master.business.MifosCurrency;

public class FeeDetailsForLoadDto {

    private final FeeParameters feeParameters;
    private final boolean isMultiCurrencyEnabled;
    private final List<MifosCurrency> currencies;

    public FeeDetailsForLoadDto(FeeParameters feeParameters, boolean isMultiCurrencyEnabled, List<MifosCurrency> currencies) {
        this.feeParameters = feeParameters;
        this.isMultiCurrencyEnabled = isMultiCurrencyEnabled;
        this.currencies = currencies;
    }

    public FeeParameters getFeeParameters() {
        return this.feeParameters;
    }

    public boolean isMultiCurrencyEnabled() {
        return this.isMultiCurrencyEnabled;
    }

    public List<MifosCurrency> getCurrencies() {
        return this.currencies;
    }
}
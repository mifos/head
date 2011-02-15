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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class SavingsProductValidator {

    public void validateGroup(SavingsProductFormBean savingsProductFormBean, BindingResult result) {
        if (savingsProductFormBean.isGroupSavingAccount()
                && StringUtils.isBlank(savingsProductFormBean.getSelectedGroupSavingsApproach())) {
            ObjectError error = new ObjectError("savingsProduct",
                    new String[] { "NotEmpty.savingsProduct.selectedGroupSavingsApproach" }, new Object[] {},
                    "default: ");
            result.addError(error);
        }
    }

    public void validateManadtorySavingsProduct(SavingsProductFormBean savingsProductFormBean, BindingResult result) {
        if (savingsProductFormBean.isMandatory() && savingsProductFormBean.getAmountForDeposit() == null
                || savingsProductFormBean.getAmountForDeposit().intValue() <= 0) {
            ObjectError error = new ObjectError("savingsProduct",
                    new String[] { "Min.savingsProduct.amountForDesposit" }, new Object[] {}, "default: ");
            result.addError(error);
        }
    }
}

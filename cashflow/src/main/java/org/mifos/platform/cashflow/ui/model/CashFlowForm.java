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
package org.mifos.platform.cashflow.ui.model;

import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.validation.ScreenObject;

import java.util.ArrayList;
import java.util.List;

public class CashFlowForm extends ScreenObject {
    private static final long serialVersionUID = -3806820293757764245L;

    private CashFlowDetail cashFlowDetail;

    @SuppressWarnings({"UnusedDeclaration", "PMD.UnnecessaryConstructor", "PMD.UncommentedEmptyConstructor"})
    public CashFlowForm() {
        super();
    }

    public CashFlowForm(CashFlowDetail cashFlowDetail) {
        super();
        this.cashFlowDetail = cashFlowDetail;
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    @javax.validation.Valid
    public List<MonthlyCashFlowForm> getMonthlyCashFlows() {
        List<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        for (MonthlyCashFlowDetail monthlyCashFlowDetail : cashFlowDetail.getMonthlyCashFlowDetails()) {
            monthlyCashFlows.add(new MonthlyCashFlowForm(monthlyCashFlowDetail));
        }
        return monthlyCashFlows;
    }

}

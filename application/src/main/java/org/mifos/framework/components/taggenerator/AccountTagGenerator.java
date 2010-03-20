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

package org.mifos.framework.components.taggenerator;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.framework.business.BusinessObject;

public class AccountTagGenerator extends TagGenerator {

    public AccountTagGenerator() {
        setAssociatedGenerator(new CustomerTagGenerator());
    }

    @Override
    protected StringBuilder build(BusinessObject obj, boolean selfLinkRequired, Object randomNum) {
        AccountBO account = (AccountBO) obj;
        StringBuilder strBuilder = getAssociatedGenerator().build(account.getCustomer(), randomNum);
        strBuilder.append(" / ");
        if (selfLinkRequired) {
            createAccountLink(strBuilder, account, randomNum); // create self
                                                               // link
        } else {
            // TODO internationalize this
            strBuilder.append("<b>" + getAccountName(account) + "</b>"); // get
                                                                         // Self
                                                                         // Node
                                                                         // Value
        }
        return strBuilder;
    }

    private void createAccountLink(StringBuilder strBuilder, AccountBO account, Object randomNum) {
        strBuilder.append("<a href=\"");
        strBuilder.append(getAction(account));
        strBuilder.append(account.getGlobalAccountNum());
        strBuilder.append("&randomNum=");
        strBuilder.append(randomNum);
        strBuilder.append("\">");
        // TODO internationalize this
        strBuilder.append(getAccountName(account));
        strBuilder.append("</a>");
    }

    private String getAccountName(AccountBO account) {
        if (account.getType() == AccountTypes.SAVINGS_ACCOUNT) {
            return ((SavingsBO) account).getSavingsOffering().getPrdOfferingName();
        } else if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
            return ((LoanBO) account).getLoanOffering().getPrdOfferingName();
        }
        return "";
    }

    private String getAction(AccountBO account) {
        if (account.getType() == AccountTypes.SAVINGS_ACCOUNT) {
            return "savingsAction.do?method=get&globalAccountNum=";
        } else if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
            return "loanAccountAction.do?method=get&globalAccountNum=";
        }
        return "";
    }
}

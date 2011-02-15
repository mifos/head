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
package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ViewDepositDueDetailsPage extends MifosPage{

    public ViewDepositDueDetailsPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("savingsaccountdepositduedetails");

    }

    public Float getTotalAmountDue(){
        int rowCount = selenium.getXpathCount("//table[@id='DepositDueDetailsTable']/tbody/tr").intValue();
        return Float.parseFloat(selenium.getTable("DepositDueDetailsTable." + (rowCount - 2) + ".1"));
    }

    public Float getNextdeposit(){
        return Float.parseFloat(selenium.getTable("DepositDueDetailsTable.1.1"));
    }

    public Float getSubTotal(){
        int rowCount = selenium.getXpathCount("//table[@id='DepositDueDetailsTable']/tbody/tr").intValue();
        return Float.parseFloat(selenium.getTable("DepositDueDetailsTable." + (rowCount - 3) + ".1"));
    }

    public void verifyTotalAmountDue(Integer numberOfGroupMembers, Float amountPerMember){
        Assert.assertEquals(getNextdeposit(), (numberOfGroupMembers*amountPerMember));
        Assert.assertEquals(getTotalAmountDue(), (numberOfGroupMembers*amountPerMember+getSubTotal()));
    }

}

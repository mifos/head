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

package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class TransactionHistoryPage extends AbstractPage {

    public final static String TABLE_ID = "trxnhistoryList";
    public final static String VOLUNTARY = "Voluntary";

    public TransactionHistoryPage(Selenium selenium) {
        super(selenium);
        verifyPage("viewsavingstrxnhistory");
    }

    public void verifyTableTypeAfterDeposit(int rowCount) {
        for (int i = 1; i <= rowCount; ++i) {
            String[] type = getType(i).split(" ");
            Assert.assertEquals(type[0], VOLUNTARY);
        }
    }

    public String getType(int row) {
        return selenium.getTable(TABLE_ID + "." + row + ".3");
    }
}

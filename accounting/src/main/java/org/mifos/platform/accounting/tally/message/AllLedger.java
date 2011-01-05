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

package org.mifos.platform.accounting.tally.message;

public class AllLedger {

    private final Boolean isDeemedPositive;

    private final String ledgerName;

    private final String amount;

    private final String branchName;

    public Boolean getIsDeemedPositive() {
	return isDeemedPositive;
    }

    public String getAmount() {
	return this.amount;
    }

    public String getBranchName() {
	return this.branchName;
    }

    public String getLedgerName() {
	return ledgerName;
    }

    public AllLedger(String ledgerName, Boolean isDeemedPositive,
	    String amount, String branchName) {
	this.ledgerName = ledgerName;
	this.isDeemedPositive = isDeemedPositive;
	this.amount = amount;
	this.branchName = branchName;
    }

    @Override
    public String toString() {
	return isDeemedPositive + ";" + ledgerName + ";" + amount + ";"
		+ branchName;
    }
}
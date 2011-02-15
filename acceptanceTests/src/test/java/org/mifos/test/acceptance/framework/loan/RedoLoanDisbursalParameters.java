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

package org.mifos.test.acceptance.framework.loan;

import java.util.ArrayList;
import java.util.List;

public class RedoLoanDisbursalParameters {
    private String disbursalDateDD;
    private String disbursalDateMM;
    private String disbursalDateYYYY;
    private String loanAmount;
    private String interestRate;
    private String numberOfInstallments;

    private final List<Integer> clientsIDs = new ArrayList<Integer>();
    private final List<String> clientsAmounts = new ArrayList<String>();
    private final List<String> clientsPurposes = new ArrayList<String>();
    private int clientsCount = 0;

    static public RedoLoanDisbursalParameters createObjectWithClearedParameters() {
        RedoLoanDisbursalParameters parameters = new RedoLoanDisbursalParameters();
        parameters.setDisbursalDateDD("");
        parameters.setDisbursalDateMM("");
        parameters.setDisbursalDateYYYY("");
        parameters.setLoanAmount("0");
        parameters.setInterestRate("0");
        parameters.setNumberOfInstallments("0");
        return parameters;
    }

    public String getDisbursalDateDD() {
        return this.disbursalDateDD;
    }
    public void setDisbursalDateDD(String disbursalDateDD) {
        this.disbursalDateDD = disbursalDateDD;
    }
    public String getDisbursalDateMM() {
        return this.disbursalDateMM;
    }
    public void setDisbursalDateMM(String disbursalDateMM) {
        this.disbursalDateMM = disbursalDateMM;
    }
    public String getDisbursalDateYYYY() {
        return this.disbursalDateYYYY;
    }
    public void setDisbursalDateYYYY(String disbursalDateYYYY) {
        this.disbursalDateYYYY = disbursalDateYYYY;
    }
    public String getLoanAmount() {
        return this.loanAmount;
    }
    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }
    public String getInterestRate() {
        return this.interestRate;
    }
    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }
    public String getNumberOfInstallments() {
        return this.numberOfInstallments;
    }
    public void setNumberOfInstallments(String numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public void addClient(int clientID, String amount, String purpose) {
        this.clientsIDs.add(clientID);
        this.clientsAmounts.add(amount);
        this.clientsPurposes.add(purpose);
        this.clientsCount++;
    }

    public int getCLientsID(int i) {
        return this.clientsIDs.get(i);
    }

    public String getClientsAmount(int i) {
        return this.clientsAmounts.get(i);
    }

    public String getClientsPurpose(int i) {
        return this.clientsPurposes.get(i);
    }

    public void setClientsCount(int clients_count) {
        this.clientsCount = clients_count;
    }

    public int getClientsCount() {
        return clientsCount;
    }
}

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

import java.util.HashMap;
import java.util.Map;

public class PerformanceHistoryAtributes {

    private Integer loanCycle;
    private String amountOfLastLoan;
    private Integer noOfActiveLoan;
    private Double delinquentPortfolio;
    final private Map<String,Integer> loanCyclePerProduct;

    public PerformanceHistoryAtributes() {
        super();
        loanCycle= 0;
        amountOfLastLoan = "0.0";
        noOfActiveLoan = 0;
        loanCyclePerProduct = new HashMap<String, Integer>();
    }

    public Integer getLoanCycle() {
        return this.loanCycle;
    }

    public void incrementLoanCycle(){
        if(loanCycle != null){
            this.loanCycle++;
        }
    }

    public String getAmountOfLastLoan() {
        return this.amountOfLastLoan;
    }

    public void setAmountOfLastLoan(String amountOfLastLoan) {
        this.amountOfLastLoan = amountOfLastLoan;
    }

    public Double getDelinquentPortfolio() {
        return this.delinquentPortfolio;
    }

    public void setDelinquentPortfolio(Double delinquentPortfolio) {
        this.delinquentPortfolio = delinquentPortfolio;
    }

    public Map<String, Integer> getLoanCyclePerProduct() {
        return this.loanCyclePerProduct;
    }

    public void incrementLoanCycleForProduct(String product){
        Integer loanCycle = loanCyclePerProduct.get(product);
        if(loanCycle==null){
            loanCycle=0;
        }
            loanCyclePerProduct.put(product, loanCycle+1);
    }

    public Integer getNoOfActiveLoan() {
        return this.noOfActiveLoan;
    }

    public void incrementNoOfActiveLoan(){
        if(noOfActiveLoan != null){
            this.noOfActiveLoan++;
        }
    }

    public void decrementNoOfActiveLoan(){
        if(noOfActiveLoan != null && noOfActiveLoan>0){
            this.noOfActiveLoan--;
        }
    }
}

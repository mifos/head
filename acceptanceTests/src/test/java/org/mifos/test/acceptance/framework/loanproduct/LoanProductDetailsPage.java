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

package org.mifos.test.acceptance.framework.loanproduct;
import java.math.BigDecimal;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class LoanProductDetailsPage  extends MifosPage {

        public final static String lOAN_AMOUNT_SAME_TABLE = "loanAmountSameTable";
        public final static String LOAN_AMOUNT_FROM_CYCLE_TABLE = "loanAmountFromCycleTable";
        public final static String LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE = "loanAmountFromLastTable";

        public final static String INSTALLMENTS_SAME_TABLE = "noOfInstallSameTable";
        public final static String INSTALLMENTS_FROM_CYCLE_TABLE = "noOfInstallFromCycleTable";
        public final static String INSTALLMENTS_FROM_LAST_AMOUNT_TABLE = "noOfInstallFromLastTable";

        public LoanProductDetailsPage(Selenium selenium) {
            super(selenium);
        }

        public LoanProductDetailsPage verifyPage() {
            verifyPage("LoanProductDetails");
            return this;
        }

        public EditLoanProductPage editLoanProduct() {
            selenium.click("loanproductdetails.link.editLoanProduct");
            waitForPageToLoad();
            return new EditLoanProductPage(selenium);

        }

        public void verifyInterestAmount(String amount) {
            Assert.assertTrue(selenium.isTextPresent(amount));
        }

        public void verifyDefaultLoanAmount(String amount) {
            Assert.assertTrue(selenium.isTextPresent(amount));
        }

    public void verifyVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        Assert.assertTrue(selenium.isTextPresent("Minimum gap between installments: " + minGap  + " days"));
        if ("".equals(maxGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: " + maxGap  + " days"));
        }
        if ("".equals(minInstalmentAmount)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: " + minInstalmentAmount)) ;
        }
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: Yes"));
    }

    public void verifyVariableInstalmentOptionUnChecked() {
        Assert.assertTrue(!selenium.isTextPresent("Minimum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Maximum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Minimum installment amount:")) ;
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: No"));
    }

    public LoanProductDetailsPage verifyCashFlowOfEditedLoan(String warningThreshold, String indebetedValue, String repaymentCapacityValue) {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: " + warningThreshold + " %"));
        }
        if ("".equals(indebetedValue)) {
            Assert.assertTrue(selenium.isTextPresent("Indebtedness Rate: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Indebtedness Rate: " + indebetedValue + " %"));
        }
        if ("".equals(repaymentCapacityValue)) {
            Assert.assertTrue(selenium.isTextPresent("Repayment Capacity: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Repayment Capacity: " + repaymentCapacityValue + " %"));
        }
        return this;
    }

    public LoanProductDetailsPage verifyCashFlowUncheckedInEditedProduct() {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: No"));
        Assert.assertTrue(!selenium.isTextPresent("Warning Threshold:"));
        return this;
    }

    public void verifyLoanAmountTableTypeSame(String min, String max, String def) {
    	
    	BigDecimal minAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.0").replaceAll(",", "")));
    	BigDecimal maxAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.1").replaceAll(",", "")));
    	BigDecimal defaultAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.2").replaceAll(",", "")));
    	
    	BigDecimal expectedMinAmount = BigDecimal.valueOf(Double.valueOf(min.replaceAll(",", "")));
    	BigDecimal expectedMaxAmount = BigDecimal.valueOf(Double.valueOf(max.replaceAll(",", "")));
    	BigDecimal expectedDefaultAmount = BigDecimal.valueOf(Double.valueOf(def.replaceAll(",", "")));
    	
    	Assert.assertEquals(minAmount, expectedMinAmount);
    	Assert.assertEquals(maxAmount, expectedMaxAmount);
    	Assert.assertEquals(defaultAmount, expectedDefaultAmount);
//        Assert.assertEquals(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.0"), min);
//        Assert.assertEquals(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.1"), max);
//        Assert.assertEquals(selenium.getTable(lOAN_AMOUNT_SAME_TABLE+".1.2"), def);
    }

    public void verifyInstallmentsTableTypeFromCycle(String[][] installCycles) {
        for(int i = 1; i <= installCycles.length; i++) {
            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_CYCLE_TABLE+"."+i+".1"), installCycles[i-1][0]);
            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_CYCLE_TABLE+"."+i+".2"), installCycles[i-1][1]);
            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_CYCLE_TABLE+"."+i+".3"), installCycles[i-1][2]);
        }
    }

    public void verifyLoanAmountTableTypeFromCycle(String[][] loanAmountCycles) {
        for(int i = 1; i <= loanAmountCycles.length; i++) {
        	
        	
        	BigDecimal minAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_CYCLE_TABLE+"."+i+".1").replaceAll(",", "")));
        	BigDecimal maxAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_CYCLE_TABLE+"."+i+".2").replaceAll(",", "")));
        	BigDecimal defaultAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_CYCLE_TABLE+"."+i+".3").replaceAll(",", "")));
        	
        	BigDecimal expectedMinAmount = BigDecimal.valueOf(Double.valueOf(loanAmountCycles[i-1][0]));
        	BigDecimal expectedMaxAmount = BigDecimal.valueOf(Double.valueOf(loanAmountCycles[i-1][1]));
        	BigDecimal expectedDefaultAmount = BigDecimal.valueOf(Double.valueOf(loanAmountCycles[i-1][2]));
        	
        	Assert.assertEquals(minAmount, expectedMinAmount);
        	Assert.assertEquals(maxAmount, expectedMaxAmount);
        	Assert.assertEquals(defaultAmount, expectedDefaultAmount);
        }
    }

    public void verifyInstallmentTableTypeFromLastAmount(String[][] installmentsByLastAmount) {
        StringBuffer last = new StringBuffer("");
        for(int i = 1; i <= installmentsByLastAmount.length; i++) {
            last.delete(0, last.length());
            if(i > 1) {
                int tmp = Integer.valueOf(installmentsByLastAmount[i-2][0]);
                tmp += 1;
                String newLast = String.valueOf(tmp);
                last.append(newLast).append(".0 - ").append(installmentsByLastAmount[i-1][0]).append(".0");
            }
            else {
                last.append("0.0 - ").append(installmentsByLastAmount[0][0]).append(".0");
            }
//            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".0"), last.toString());
            
            
            BigDecimal minAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".1").replaceAll(",", "")));
        	BigDecimal maxAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".2").replaceAll(",", "")));
        	BigDecimal defaultAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".3").replaceAll(",", "")));
        	
        	BigDecimal expectedMinAmount = BigDecimal.valueOf(Double.valueOf(installmentsByLastAmount[i-1][1]));
        	BigDecimal expectedMaxAmount = BigDecimal.valueOf(Double.valueOf(installmentsByLastAmount[i-1][2]));
        	BigDecimal expectedDefaultAmount = BigDecimal.valueOf(Double.valueOf(installmentsByLastAmount[i-1][3]));
        	
        	Assert.assertEquals(minAmount, expectedMinAmount);
        	Assert.assertEquals(maxAmount, expectedMaxAmount);
        	Assert.assertEquals(defaultAmount, expectedDefaultAmount);
            
//            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".1"), installmentsByLastAmount[i-1][1]);
//            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".2"), installmentsByLastAmount[i-1][2]);
//            Assert.assertEquals(selenium.getTable(INSTALLMENTS_FROM_LAST_AMOUNT_TABLE+"."+i+".3"), installmentsByLastAmount[i-1][3]);
        }
    }

    public void verifyAmountTableTypeFromLastAmount(String[][] amountsByLastAmount) {
        StringBuffer last = new StringBuffer("");
        for(int i = 1; i <= amountsByLastAmount.length; i++) {
            last.delete(0, last.length());
            if(i > 1) {
                int tmp = Integer.valueOf(amountsByLastAmount[i-2][0]);
                tmp += 1;
                String newLast = String.valueOf(tmp);
                last.append(newLast).append(".0 - ").append(amountsByLastAmount[i-1][0]).append(".0");
            }
            else {
                last.append("0.0 - ").append(amountsByLastAmount[0][0]).append(".0");
            }
            
        	BigDecimal minAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".1").replaceAll(",", "")));
        	BigDecimal maxAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".2").replaceAll(",", "")));
        	BigDecimal defaultAmount = BigDecimal.valueOf(Double.valueOf(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".3").replaceAll(",", "")));
        	
        	BigDecimal expectedMinAmount = BigDecimal.valueOf(Double.valueOf(amountsByLastAmount[i-1][1]));
        	BigDecimal expectedMaxAmount = BigDecimal.valueOf(Double.valueOf(amountsByLastAmount[i-1][2]));
        	BigDecimal expectedDefaultAmount = BigDecimal.valueOf(Double.valueOf(amountsByLastAmount[i-1][3]));
        	
        	Assert.assertEquals(minAmount, expectedMinAmount);
        	Assert.assertEquals(maxAmount, expectedMaxAmount);
        	Assert.assertEquals(defaultAmount, expectedDefaultAmount);
            
//            Assert.assertEquals(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".0"), last.toString());
//            Assert.assertEquals(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".1"), amountsByLastAmount[i-1][1]);
//            Assert.assertEquals(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".2"), amountsByLastAmount[i-1][2]);
//            Assert.assertEquals(selenium.getTable(LOAN_AMOUNT_FROM_LAST_AMOUNT_TABLE+"."+i+".3"), amountsByLastAmount[i-1][3]);
        }
    }

    public void verifyInstallments(String min, String max, String def) {
        Assert.assertEquals(selenium.getTable(INSTALLMENTS_SAME_TABLE+".1.0"), min);
        Assert.assertEquals(selenium.getTable(INSTALLMENTS_SAME_TABLE+".1.1"), max);
        Assert.assertEquals(selenium.getTable(INSTALLMENTS_SAME_TABLE+".1.2"), def);
    }
}

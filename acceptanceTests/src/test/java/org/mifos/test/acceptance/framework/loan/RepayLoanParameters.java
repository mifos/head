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

package org.mifos.test.acceptance.framework.loan;

public class RepayLoanParameters {
    private String modeOfRepayment;
    private String receiptId;
    private String recieptDateDD;
    private String recieptDateMM;
    private String recieptDateYYYY;
    
    public String getModeOfRepayment() {
        return this.modeOfRepayment;
    }
    public void setModeOfRepayment(String modeOfRepayment) {
        this.modeOfRepayment = modeOfRepayment;
    }
    public String getReceiptId() {
        return this.receiptId;
    }
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    public String getRecieptDateDD() {
        return this.recieptDateDD;
    }
    public void setRecieptDateDD(String recieptDateDD) {
        this.recieptDateDD = recieptDateDD;
    }
    public String getRecieptDateMM() {
        return this.recieptDateMM;
    }
    public void setRecieptDateMM(String recieptDateMM) {
        this.recieptDateMM = recieptDateMM;
    }
    public String getRecieptDateYYYY() {
        return this.recieptDateYYYY;
    }
    public void setRecieptDateYYYY(String recieptDateYYYY) {
        this.recieptDateYYYY = recieptDateYYYY;
    }
}

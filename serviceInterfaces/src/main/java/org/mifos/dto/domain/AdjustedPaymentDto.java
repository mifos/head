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
package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.Date;

public class AdjustedPaymentDto implements Serializable {

    private static final long serialVersionUID = 6842718215097913178L;

    private Date paymentDate;
    private String amount;
    private Short paymentType;
    
    public Date getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public Short getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(Short paymentType) {
        this.paymentType = paymentType;
    }
    
    public AdjustedPaymentDto(final String amount, final Date paymentDate, final Short paymentType) {
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
    }
    
}
